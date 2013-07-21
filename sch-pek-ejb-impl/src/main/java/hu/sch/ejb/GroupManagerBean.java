package hu.sch.ejb;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.Post;
import hu.sch.domain.PostType;
import hu.sch.domain.SvieStatus;
import hu.sch.domain.user.User;
import hu.sch.services.GroupManagerLocal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
@Stateless
public class GroupManagerBean implements GroupManagerLocal {

    private static final Logger logger = LoggerFactory.getLogger(GroupManagerBean.class);
    @PersistenceContext
    private EntityManager em;

    public GroupManagerBean() {
    }

    /**
     * For testing purposes.
     */
    public GroupManagerBean(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Group> getAllGroups() {
        return getAllGroups(false);
    }

    @Override
    public List<Group> getAllGroups(boolean includeMemberCount) {
        if (!includeMemberCount) {
            return em.createNamedQuery(Group.findAll).getResultList();
        }

        // TODO: make constant values constant and/or enums
        Query q = em.createQuery("SELECT g, "
                + "(SELECT COUNT(*) FROM Membership ms WHERE ms.user.sviePrimaryMembership = ms "
                + "AND ms.group.id = g.id AND ms.user.svieStatus = 'ELFOGADVA' "
                + "AND ms.user.svieMembershipType = 'RENDESTAG') "
                + "FROM Group g WHERE g.status='akt'");

        List<Object[]> groupsAndCount = q.getResultList();
        List<Group> groups = new ArrayList<>(groupsAndCount.size());
        for (Object[] results : groupsAndCount) {
            Group g = (Group) results[0];
            g.setNumberOfPrimaryMembers((Long) results[1]);
            groups.add(g);
        }
        return groups;
    }

    @Override
    public List<Group> getGroupTree() {
        Query q = em.createNamedQuery(Group.groupHierarchy);
        List<Group> groups = q.getResultList();
        List<Group> rootGroups = new ArrayList<>();

        for (Group cs : groups) {
            if (cs.getParent() != null) {
                if (cs.getParent().getSubGroups() == null) {
                    cs.getParent().setSubGroups(new ArrayList<Group>());
                }
                cs.getParent().getSubGroups().add(cs);
            } else {
                rootGroups.add(cs);
            }
        }

        return rootGroups;
    }

    @Override
    public Group findGroupById(Long id) {
        return findGroupById(id, false);
    }

    @Override
    public Group findGroupById(Long id, boolean fetchMemberships) {
        if (!fetchMemberships) {
            return em.find(Group.class, id);
        }

        TypedQuery<Group> q = em.createNamedQuery(Group.findWithMemberships, Group.class);
        q.setParameter("id", id);

        try {
            return q.getSingleResult();
        } catch (NoResultException ex) {
            logger.warn("Can't find group with memberships.", ex);
        } catch (NonUniqueResultException ex) {
            logger.error(String.format("More than one entry with %d id", id), ex);
        }

        return null;
    }

    @Override
    public Group findGroupByName(String name) {
        try {
            return em.createNamedQuery(Group.findByName, Group.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NonUniqueResultException ex) {
            logger.warn("Group name is not unique: " + name, ex);
        } catch (Exception ex) {
            logger.warn("Could not retrieve group with name: " + name, ex);
        }
        return null;
    }

    @Override
    public List<Group> findGroupsByName(String nameFragment) {
        Query q =
                em.createQuery("SELECT g FROM Group g "
                + "WHERE UPPER(g.name) LIKE UPPER(:groupName) "
                + "ORDER BY g.name");
        q.setParameter("groupName", nameFragment);

        return q.getResultList();
    }

    @Override
    public List<User> findMembersByGroupAndPost(Long groupId, String post) {
        Query q = em.createNamedQuery(Group.findMembersByGroupAndPost);
        q.setParameter("groupId", groupId);
        q.setParameter("post", post);

        return q.getResultList();
    }

    @Override
    public List<User> findMembersWithPrimaryMembership(Long groupId) {
        Query q = em.createQuery("SELECT ms.user FROM Membership ms "
                + "WHERE ms.group.id=:groupId AND ms.user.sviePrimaryMembership = ms "
                + "AND ms.user.svieStatus = :svieStatus "
                + "ORDER BY ms.user.lastName, ms.user.firstName");
        q.setParameter("groupId", groupId);
        q.setParameter("svieStatus", SvieStatus.ELFOGADVA);

        return q.getResultList();
    }

    public List<User> findActiveMembers(long groupId) {
        Query q = em.createQuery("SELECT ms.user FROM Membership ms JOIN "
                + "ms.user WHERE ms.group.id = :groupId AND ms.end = NULL "
                + "ORDER BY ms.user.lastName ASC, ms.user.firstName ASC");
        q.setParameter("groupId", groupId);

        return q.getResultList();
    }


    @Override
    public void updateGroup(Group group) {
        em.merge(group);
    }

    @Override
    public User findLeaderForGroup(Long groupId) {
        final String queryString = "SELECT p.membership.user FROM Post p "
                + "WHERE p.postType.postName = :postname AND p.membership.group.id = :id";

        TypedQuery<User> q = em.createQuery(queryString, User.class)
                .setParameter("id", groupId)
                .setParameter("postname", PostType.KORVEZETO);

        try {
            User ret = q.getSingleResult();
            return ret;
        } catch (NoResultException nre) {
            logger.error(String.format("No group was found with id: %d", groupId), nre);
        }
        return null;
    }

    @Override
    public void createGroup(Group group, User leader) {
        Membership ms = new Membership();
        ms.setGroup(group);
        ms.setStart(new Date());
        ms.setUser(leader);

        Post post = new Post();
        post.setMembership(ms);
        PostType leaderPost = em.createNamedQuery(PostType.searchForPostType, PostType.class)
                .setParameter("pn", PostType.KORVEZETO)
                .getSingleResult();
        post.setPostType(leaderPost);

        em.persist(group);
        em.persist(ms);
        em.persist(post);
    }

    @Override
    public List<Group> getSubGroups(Long groupId) {
        return em.createQuery("SELECT g FROM Group g WHERE g.parent.id =:id")
                .setParameter("id", groupId)
                .getResultList();
    }
}
