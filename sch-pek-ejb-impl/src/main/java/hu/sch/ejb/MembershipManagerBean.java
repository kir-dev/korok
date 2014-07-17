package hu.sch.ejb;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.Post;
import hu.sch.domain.PostType;
import hu.sch.domain.enums.SvieMembershipType;
import hu.sch.domain.enums.SvieStatus;
import hu.sch.domain.logging.EventType;
import hu.sch.domain.user.User;
import hu.sch.services.LogManagerLocal;
import hu.sch.services.MembershipManagerLocal;
import hu.sch.services.exceptions.MembershipAlreadyExistsException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
@Stateless
public class MembershipManagerBean implements MembershipManagerLocal {

    private static Logger logger = LoggerFactory.getLogger(MembershipManagerBean.class);

    @PersistenceContext
    private EntityManager em;
    @Inject
    LogManagerLocal logManager;

    @Override
    public void joinGroup(Group group, User user, Date start, Date end, boolean isAuthorized) throws MembershipAlreadyExistsException {
        Membership ms = new Membership();
        User _user = em.find(User.class, user.getId());
        Group _group = em.find(Group.class, group.getId());

        ms.setUser(_user);
        ms.setGroup(_group);
        ms.setStart(start);
        ms.setEnd(null);

        if (!isAuthorized) {
            Post post = new Post();
            post.setMembership(ms);

            Query q = em.createNamedQuery(PostType.searchForPostType);
            q.setParameter("pn", "feldolgozás alatt");
            PostType postType = (PostType) q.getSingleResult();

            post.setPostType(postType);
            Set<Post> posts = new HashSet<Post>();
            posts.add(post);
            ms.setPosts(posts);

            em.persist(post);
        }

        _user.getMemberships().add(ms);
        _group.getMemberships().add(ms);

        em.persist(ms);
        em.merge(_user);
        em.merge(_group);
        try {
            em.flush();
        } catch (PersistenceException ex) {
            if (ex.getCause().getClass().equals(ConstraintViolationException.class)) {
                // már van ilyen tagság
                throw new MembershipAlreadyExistsException(group, user);
            } else {
                // valami egyéb rondaság, dobjuk vissza :)
                throw ex;
            }
        }
        logManager.createLogEntry(group, user, EventType.JELENTKEZES);
    }

    @Override
    public void deleteMembership(Membership membership) {
        Membership temp = em.find(Membership.class, membership.getId());
        User user = membership.getUser();
        boolean userChanged = false;

        em.remove(temp);
        em.flush();
        if (user.getSvieMembershipType().equals(SvieMembershipType.RENDESTAG)
                && user.getSvieStatus().equals(SvieStatus.ELFOGADVA)
                && membership.getGroup().getIsSvie()) {
            try {
                Query q = em.createQuery("SELECT ms.user FROM Membership ms "
                        + "WHERE ms.user = :user AND ms.group.isSvie = true");
                q.setParameter("user", user);
                q.getSingleResult();
            } catch (NoResultException nre) {
                user.setSvieMembershipType(SvieMembershipType.PARTOLOTAG);
                userChanged = true;
            }
        }
        if (user.getSviePrimaryMembership() != null && membership.getId().equals(user.getSviePrimaryMembership().getId())) {
            user.setSviePrimaryMembership(null);
            userChanged = true;
        }
        if (userChanged) {
            em.merge(user);
        }
        logManager.createLogEntry(membership.getGroup(), membership.getUser(), EventType.TAGSAGTORLES);
    }

    @Override
    public Membership findMembership(Long groupId, Long userId) {
        try {
            return em.createNamedQuery(Membership.findMembershipForUserAndGroup, Membership.class)
                    .setParameter("userId", userId)
                    .setParameter("groupId", groupId)
                    .getSingleResult();

        } catch (NoResultException ex) {
            logger.warn("Could not find membership for {} group id and {} user id", groupId, userId);
        } catch (NonUniqueResultException ex) {
            logger.error("More than one membership for group id ({}) - user id ({}) pair.", groupId, userId);
        }
        return null;
    }

    @Override
    public Membership findMembership(Long id) {
        return em.find(Membership.class, id);
    }

    @Override
    public void activateMembership(Membership membership) {
        membership.setEnd(null);
        em.merge(membership);
    }

    @Override
    public void inactivateMembership(Membership membership) {
        membership.setEnd(new Date());
        em.merge(membership);
    }

    @Override
    public Group fetchMembershipsFor(Group group) {
        Query q = em.createNamedQuery(Membership.findMembershipsForGroup)
                .setParameter("id", group.getId());

        group.setMemberships(q.getResultList());

        return group;
    }

    @Override
    public List<Membership> findMembershipsForUser(User user) {
        TypedQuery<Membership> q = em.createQuery(
                "SELECT DISTINCT ms FROM Membership ms "
                + "JOIN FETCH ms.group "
                + "LEFT JOIN FETCH ms.posts p "
                + "LEFT JOIN FETCH p.postType "
                + "WHERE ms.user = :user "
                + "AND ms.end IS NULL "
                + "ORDER BY ms.group.id", Membership.class);
        q.setParameter("user", user);

        return q.getResultList();
    }

    @Override
    public List<Membership> findActiveMembershipsForGroup(Long groupId) {
        return new GroupMembershipFetcher(em, groupId).findActive();
    }

    @Override
    public List<Membership> findInactiveMembershipsForGroup(Long groupId) {
        return new GroupMembershipFetcher(em, groupId).findInactive();
    }
}
