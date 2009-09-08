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
    public void initialize() {
        if (DELETEMEMBERSHIP_EVENT == null) {
            Query q = em.createNamedQuery(Event.getEventForEventType);
            q.setParameter("evt", EventType.TAGSAGTORLES);
            DELETEMEMBERSHIP_EVENT = (Event) q.getSingleResult();
            q.setParameter("evt", EventType.JELENTKEZES);
            CREATEMEMBERSHIP_EVENT = (Event) q.getSingleResult();
        }
        StringBuilder sb = new StringBuilder(200);
    }

    public List<User> getAllUsers() {
        Query q = em.createNamedQuery(User.findAll);
        return q.getResultList();
    }

    public void updateUserAttributes(User user) {
        User f = em.find(User.class, user.getId());
        if (user.getEmailAddress() != null) {
            f.setEmailAddress(user.getEmailAddress());
        }
        if (user.getNickName() != null) {
            f.setNickName(user.getNickName());
        }
        if (user.getNeptunCode() != null) {
            f.setNeptunCode(user.getNeptunCode());
        }
        if (user.getLastName() != null) {
            f.setLastName(user.getLastName());
        }
        if (user.getFirstName() != null) {
            f.setFirstName(user.getFirstName());
        }

        em.flush();
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

    public void modifyMembership(User user, Group group, Date start, Date end) {
//        Membership m =
//                em.find(Membership.class, new MembershipPK(user.getId(), group.getId()));
//
//        m.setStart(start);
//        m.setEnd(end);
    }

    public void deleteMembership(Membership ms) {
        Membership temp = em.find(Membership.class, ms.getId());
        em.remove(temp);
        em.flush();
        if (ms.getUser().getSvieMembershipType().equals(SvieMembershipType.RENDESTAG) &&
                ms.getUser().getSvieStatus().equals(SvieStatus.ELFOGADVA) &&
                ms.getGroup().getIsSvie()) {
            try {
                Query q = em.createQuery("SELECT ms.user FROM Membership ms " +
                        "WHERE ms.user = :user AND ms.group.isSvie = true");
                q.setParameter("user", ms.getUser());
                q.getSingleResult();
            } catch (NoResultException nre) {
                User temp2 = em.find(User.class, ms.getUser().getId());
                temp2.setSvieMembershipType(SvieMembershipType.PARTOLOTAG);
            }
        }
        logManager.createLogEntry(ms.getGroup(), ms.getUser(), DELETEMEMBERSHIP_EVENT);
    }

    public List<User> getCsoporttagokWithoutOregtagok(Long csoportId) {
        //Group cs = em.find(Group.class, csoportId);
        Query q =
                em.createQuery("SELECT ms.user FROM Membership ms JOIN " +
                "ms.user " +
                "WHERE ms.group.id=:groupId AND ms.end=NULL " +
                "ORDER BY ms.user.lastName ASC, ms.user.firstName ASC");

        q.setParameter("groupId", csoportId);

        return q.getResultList();
    }

    public List<User> getUsersWithPrimaryMembership(Long groupId) {
        Query q = em.createQuery("SELECT ms.user FROM Membership ms JOIN " +
                "ms.user " +
                "WHERE ms.group.id=:groupId AND ms.user.sviePrimaryMembership = ms");
        q.setParameter("groupId", groupId);
        return q.getResultList();
    }

    public List<User> getDelegatedUsersForGroup(Long groupId) {
        Query q = em.createQuery("SELECT ms.user FROM Membership ms JOIN " +
                "ms.user " +
                "WHERE ms.group.id=:groupId AND ms.user.sviePrimaryMembership = ms AND ms.user.delegated = true");
        q.setParameter("groupId", groupId);
        return q.getResultList();
    }

    public List<User> getCsoporttagok(Long csoportId) {
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
            log.warn("Can't find user with memberships", ex);
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
    public Membership getCsoporttagsag(Long memberId) {
        Membership cst = em.find(Membership.class, memberId);
        return cst;
    }

    public void setMemberToOldBoy(Membership ms) {
        Membership temp = em.find(Membership.class, ms.getId());
        temp.setEnd(new Date());
    }

    public void setUserDelegateStatus(User user, boolean isDelegated) {
        User temp = em.find(User.class, user.getId());

        temp.setDelegated(isDelegated);
    }

    public void setOldBoyToActive(Membership ms) {
        Membership temp = em.find(Membership.class, ms.getId());
        temp.setEnd(null);
    }

    @Override
    public void updateUser(User user) {
        em.merge(user);
    }

    @Override
    public void updateGroup(Group group) {
        em.merge(group);
    }
}
