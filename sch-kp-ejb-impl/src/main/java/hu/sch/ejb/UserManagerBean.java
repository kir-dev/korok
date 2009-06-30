/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.MembershipPK;
import hu.sch.domain.User;
import hu.sch.domain.PointRequest;
import hu.sch.domain.MembershipType;
import hu.sch.services.UserManagerLocal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author hege
 */
@Stateless()
public class UserManagerBean implements UserManagerLocal {

    @PersistenceContext
    EntityManager em;

    public List<User> getAllUsers() {
        throw new UnsupportedOperationException();
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

    public void addUserToGroup(User felhasznalo, Group csoport, Date kezdet, Date veg) {
        MembershipPK cspk =
                new MembershipPK(felhasznalo.getId(), csoport.getId());
        Membership m = em.find(Membership.class, cspk);
        if (m != null) { //már létező csoporttagság
            return;
        }
        User f = em.find(User.class, felhasznalo.getId());
        Group cs = em.find(Group.class, csoport.getId());
        m = new Membership();
        m.setId(cspk);
        m.setRights(0L);
        m.setUser(f);
        m.setGroup(cs);
        m.setStart(kezdet);
        m.setEnd(veg);
        f.getMemberships().add(m);
        cs.getMemberships().add(m);

        em.merge(m);
        em.merge(f);
        em.merge(cs);
        em.flush();
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
        Membership m =
                em.find(Membership.class, new MembershipPK(user.getId(), group.getId()));

        m.setStart(start);
        m.setEnd(end);
    }

    public void deleteMembership(User user, Group group) {
        /*Query q = em.createNamedQuery(Membership.deleteByUserIdAndGroupId);
        q.setParameter("userId", user.getId());
        q.setParameter("groupId", group.getId());
        
        q.executeUpdate();
        em.flush();
        em.clear(); //???*/
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
        } catch (Exception e) {
            return null;
        }
    }

    public Group findGroupWithCsoporttagsagokById(Long id) {
        Query q = em.createNamedQuery(Group.findWithMemberships);
        q.setParameter("id", id);
        try {
            Group group = (Group) q.getSingleResult();
            return group;
        } catch (Exception e) {
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

    public Membership getCsoporttagsag(Long userId, Long groupId) {
        Membership cst =
                em.find(Membership.class, new MembershipPK(userId, groupId));
        return cst;
    }

    public void updateMemberRights(Membership oldOne, Membership newOne, MembershipType type) {
        if (type == MembershipType.KORVEZETO) {
            if (newOne == null) {
                throw new EJBException();
            }
            if (oldOne != null) {
                Membership oldPersisted =
                        em.find(Membership.class, oldOne.getId());
                oldPersisted.setRights(MembershipType.addOrRemoveEntitlement(oldPersisted.getRights(), type));
                oldPersisted.setRights(MembershipType.addOrRemoveEntitlement(oldPersisted.getRights(), MembershipType.VOLTKORVEZETO));
            }
            Membership newPersisted =
                    em.find(Membership.class, newOne.getId());
            newPersisted.setRights(MembershipType.addOrRemoveEntitlement(newPersisted.getRights(), type));
        } else {
            if (oldOne != null) {
                Membership oldPersisted =
                        em.find(Membership.class, oldOne.getId());
                oldPersisted.setRights(MembershipType.addOrRemoveEntitlement(oldPersisted.getRights(), type));
            }
            if (newOne != null) {
                Membership newPersisted =
                        em.find(Membership.class, newOne.getId());
                newPersisted.setRights(MembershipType.addOrRemoveEntitlement(newPersisted.getRights(), type));
            }
        }
    }

    public void setMemberToOldBoy(Membership user) {
        Membership temp =
                em.find(Membership.class,
                new MembershipPK(user.getUser().getId(), user.getGroup().getId()));
        temp.setEnd(new Date());
    }

    public void setOldBoyToActive(Membership cst) {
        Membership temp = em.find(Membership.class,
                new MembershipPK(cst.getUser().getId(), cst.getGroup().getId()));
        temp.setEnd(null);
    }

    /**
     * Visszaadja az adott körhöz tartozó körvezetőt
     * @param csoportId amelyik körnek a vezetőjére vagyunk kiváncsiak
     * @return a csoport körvezetője
     * @deprecated Nem jó megoldás ez, mert a körvezetőnek lehet más joga is, valahogy
     * bitművelettel kéne megoldani, elméletben van BIT_AND group by művelete a hibernate-nek.
     * Amíg ez nincs javítva, addig kénytelen leszel Java-val keresni az aktív felhasználókon.
     */
    @Deprecated
    public User findKorvezetoForCsoport(Long csoportId) {

        Query q = em.createQuery("SELECT ms.user FROM Membership ms JOIN " +
                "ms.user " +
                "WHERE ms.group.id=:groupId AND ms.rights = 1 ");
        q.setParameter("groupId", csoportId);
        try {
            User user = (User) q.getSingleResult();
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}
