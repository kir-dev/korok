/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.User;
import hu.sch.domain.PointRequest;
import hu.sch.domain.Post;
import hu.sch.domain.PostType;
import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.SvieStatus;
import hu.sch.domain.logging.Event;
import hu.sch.domain.logging.EventType;
import hu.sch.services.LogManagerLocal;
import hu.sch.services.MailManagerLocal;
import hu.sch.services.PostManagerLocal;
import hu.sch.services.UserManagerLocal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author hege
 */
@Stateless
@SuppressWarnings("unchecked")
public class UserManagerBean implements UserManagerLocal {

    @PersistenceContext
    EntityManager em;
    @EJB(name = "LogManagerBean")
    LogManagerLocal logManager;
    @EJB(name = "MailManagerBean")
    MailManagerLocal mailManager;
    @EJB(name = "PostManagerBean")
    PostManagerLocal postManager;
    private static Logger log = Logger.getLogger(UserManagerBean.class);
    private static Event DELETEMEMBERSHIP_EVENT;
    private static Event CREATEMEMBERSHIP_EVENT;

    @PostConstruct
    private void initialize() {
        if (DELETEMEMBERSHIP_EVENT == null) {
            Query q = em.createNamedQuery(Event.getEventForEventType);
            q.setParameter("evt", EventType.TAGSAGTORLES);
            DELETEMEMBERSHIP_EVENT = (Event) q.getSingleResult();
            q.setParameter("evt", EventType.JELENTKEZES);
            CREATEMEMBERSHIP_EVENT = (Event) q.getSingleResult();
        }
    }

    public List<User> getAllUsers() {
        Query q = em.createNamedQuery(User.findAll);
        return q.getResultList();
    }

    public void updateUserAttributes(User user) {
        User f = em.find(User.class, user.getId());
        boolean changed = false;

        if (user.getEmailAddress() != null && !user.getEmailAddress().equals(f.getEmailAddress())) {
            f.setEmailAddress(user.getEmailAddress());
            changed = true;
        }
        if (user.getNickName() != null && !user.getNickName().equals(f.getNickName())) {
            f.setNickName(user.getNickName());
            changed = true;
        }
        if (user.getNeptunCode() != null && !user.getNeptunCode().equals(f.getNeptunCode())) {
            f.setNeptunCode(user.getNeptunCode());
            changed = true;
        }
        if (user.getLastName() != null && !user.getLastName().equals(f.getLastName())) {
            f.setLastName(user.getLastName());
            changed = true;
        }
        if (user.getFirstName() != null && !user.getFirstName().equals(f.getFirstName())) {
            f.setFirstName(user.getFirstName());
            changed = true;
        }

        if (changed) {
            em.merge(f);
            em.flush();
        }
    }

    public User findUserById(Long userId) {
        try {
            return em.find(User.class, userId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public void addUserToGroup(User user, Group group, Date start, Date veg) {
        Membership ms = new Membership();
        User _user = em.find(User.class, user.getId());
        Group _group = em.find(Group.class, group.getId());

        ms.setUser(_user);
        ms.setGroup(_group);
        ms.setStart(start);
        ms.setEnd(null);
        Post post = new Post();
        post.setMembership(ms);

        Query q = em.createNamedQuery(PostType.searchForPostType);
        q.setParameter("pn", "feldolgozás alatt");
        PostType postType = (PostType) q.getSingleResult();

        post.setPostType(postType);
        List<Post> posts = new ArrayList<Post>();
        posts.add(post);
        ms.setPosts(posts);
        _user.getMemberships().add(ms);
        _group.getMemberships().add(ms);

        em.persist(post);
        em.persist(ms);
        em.merge(_user);
        em.merge(_group);
        em.flush();
        logManager.createLogEntry(group, user, CREATEMEMBERSHIP_EVENT);
    }

    public List<Group> getAllGroups() {
        Query q = em.createNamedQuery(Group.findAll);

        return q.getResultList();
    }

    public List<String> getEveryGroupName() {
        Query q =
                em.createQuery("SELECT g.name FROM Group g " +
                "ORDER BY g.name");

        return q.getResultList();
    }

    public List<Group> findGroupByName(String name) {
        Query q =
                em.createQuery("SELECT g FROM Group g " +
                "WHERE g.name LIKE :groupName " +
                "ORDER BY g.name");
        q.setParameter("groupName", name);

        return q.getResultList();
    }

    public Group getGroupByName(String name) {
        Query q = em.createQuery("SELECT g FROM Group g " +
                "WHERE g.name=:groupName");
        q.setParameter("groupName", name);
        try {
            Group csoport = (Group) q.getSingleResult();
            return csoport;
        } catch (Exception e) {
            return null;
        }
    }

    public Group findGroupById(Long id) {
        return em.find(Group.class, id);
    }

    public void deleteMembership(Membership ms) {
        Membership temp = em.find(Membership.class, ms.getId());
        User user = ms.getUser();
        boolean userChanged = false;

        em.remove(temp);
        em.flush();
        if (user.getSvieMembershipType().equals(SvieMembershipType.RENDESTAG) &&
                user.getSvieStatus().equals(SvieStatus.ELFOGADVA) &&
                ms.getGroup().getIsSvie()) {
            try {
                Query q = em.createQuery("SELECT ms.user FROM Membership ms " +
                        "WHERE ms.user = :user AND ms.group.isSvie = true");
                q.setParameter("user", user);
                q.getSingleResult();
            } catch (NoResultException nre) {
                user.setSvieMembershipType(SvieMembershipType.PARTOLOTAG);
                userChanged = true;
            }
        }
        if (user.getSviePrimaryMembership() != null && ms.getId().equals(user.getSviePrimaryMembership().getId())) {
            user.setSviePrimaryMembership(null);
            userChanged = true;
        }
        if (userChanged) {
            em.merge(user);
        }
        logManager.createLogEntry(ms.getGroup(), ms.getUser(), DELETEMEMBERSHIP_EVENT);
    }

    public List<User> getCsoporttagokWithoutOregtagok(Long csoportId) {
        Query q =
                em.createQuery("SELECT ms.user FROM Membership ms JOIN " +
                "ms.user " +
                "WHERE ms.group.id=:groupId AND ms.end=NULL " +
                "ORDER BY ms.user.lastName ASC, ms.user.firstName ASC");

        q.setParameter("groupId", csoportId);

        return q.getResultList();
    }

    public List<User> getUsersWithPrimaryMembership(Long groupId) {
        Query q = em.createQuery("SELECT ms.user FROM Membership ms " +
                "WHERE ms.group.id=:groupId AND ms.user.sviePrimaryMembership = ms " +
                "AND ms.user.svieStatus = :svieStatus");
        q.setParameter("groupId", groupId);
        q.setParameter("svieStatus", SvieStatus.ELFOGADVA);
        return q.getResultList();
    }

    public List<User> getMembersForGroup(Long csoportId) {
        //Group cs = em.find(Group.class, csoportId);
        Query q =
                em.createQuery("SELECT ms.user FROM Membership ms JOIN " +
                "ms.user " +
                "WHERE ms.group.id=:groupId " +
                "ORDER BY ms.user.lastName ASC, ms.user.firstName ASC");

        q.setParameter("groupId", csoportId);

        return q.getResultList();
    }

    public List<EntrantRequest> getBelepoIgenyekForUser(User felhasznalo) {
        Query q = em.createQuery("SELECT e FROM EntrantRequest e " +
                "WHERE e.user=:user " +
                "ORDER BY e.valuation.semester DESC, e.entrantType ASC");
        q.setParameter("user", felhasznalo);

        return q.getResultList();
    }

    public List<PointRequest> getPontIgenyekForUser(User felhasznalo) {
        Query q = em.createQuery("SELECT p FROM PointRequest p " +
                "WHERE p.user=:user " +
                "ORDER BY p.valuation.semester DESC, p.valuation.group.name ASC");
        q.setParameter("user", felhasznalo);

        return q.getResultList();
    }

    public Group getGroupHierarchy() {
        Query q = em.createNamedQuery("groupHierarchy");
        List<Group> csoportok = q.getResultList();
        Group rootCsoport = new Group();
        List<Group> rootCsoportok = new ArrayList<Group>();
        rootCsoport.setSubGroups(rootCsoportok);

        for (Group cs : csoportok) {
            if (cs.getParent() != null) {
                if (cs.getParent().getSubGroups() == null) {
                    cs.getParent().setSubGroups(new ArrayList<Group>());
                }
                cs.getParent().getSubGroups().add(cs);
            } else {
                rootCsoportok.add(cs);
            }
        }

        return rootCsoport;
    }

    public User findUserWithCsoporttagsagokById(Long userId) {
        Query q = em.createNamedQuery(User.findWithMemberships);
        q.setParameter("id", userId);
        try {
            User user = (User) q.getSingleResult();
            return user;
        } catch (Exception ex) {
            log.warn("Can't find user with memberships for this id: " + userId);
            return null;
        }
    }

    public Group findGroupWithCsoporttagsagokById(Long id) {
        Query q = em.createNamedQuery(Group.findWithMemberships);
        q.setParameter("id", id);
        try {
            Group group = (Group) q.getSingleResult();
            return group;
        } catch (Exception ex) {
            log.warn("Can't find group with memberships", ex);
            return null;
        }
    }

    public void groupInfoUpdate(Group cs) {
        Group csoport = em.find(Group.class, cs.getId());
        csoport.setFounded(cs.getFounded());
        csoport.setName(cs.getName());
        csoport.setWebPage(cs.getWebPage());
        csoport.setIntroduction(cs.getIntroduction());
        csoport.setMailingList(cs.getMailingList());
    }

    @Override
    public Membership getMembership(Long memberId) {
        Membership cst = em.find(Membership.class, memberId);
        return cst;
    }

    @Override
    public Membership getMembership(final Long groupId, final Long userId) {
        Query q = em.createQuery("SELECT ms FROM Membership ms WHERE ms.user.id = :userId " +
                "AND ms.group.id = :groupId");
        q.setParameter("groupId", groupId);
        q.setParameter("userId", userId);
        return (Membership) q.getSingleResult();
    }

    public void setMemberToOldBoy(Membership ms) {
        ms.setEnd(new Date());
        em.merge(ms);
    }

    public void setUserDelegateStatus(User user, boolean isDelegated) {
        user.setDelegated(isDelegated);
        em.merge(user);
    }

    public void setOldBoyToActive(Membership ms) {
        ms.setEnd(null);
        em.merge(ms);
    }

    @Override
    public void updateUser(User user) {
        em.merge(user);
    }

    @Override
    public void updateGroup(Group group) {
        em.merge(group);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getGroupLeaderForGroup(Long groupId) {
        Query q = em.createNamedQuery(Post.getGroupLeaderForGroup);
        q.setParameter("id", groupId);
        try {
            User ret = (User) q.getSingleResult();
            return ret;
        } catch (NoResultException nre) {
            log.error("Nem találtam meg ennek a körnek a körvezetőjét: " + groupId);
            return null;
        }
    }

    public List<Group> getAllGroupsWithCount() {
        Query q = em.createQuery("SELECT new hu.sch.domain.Group(g, " +
                "(SELECT COUNT(*) FROM Membership ms WHERE ms.user.sviePrimaryMembership = ms " +
                "AND ms.group.id = g.id AND ms.user.svieStatus = 'ELFOGADVA' " +
                "AND ms.user.svieMembershipType = 'RENDESTAG')) " +
                "FROM Group g WHERE g.status='akt'");
        return q.getResultList();
    }
}
