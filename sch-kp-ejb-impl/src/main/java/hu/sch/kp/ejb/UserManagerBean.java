/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.ejb;

import hu.sch.domain.*;
import hu.sch.kp.services.exceptions.GroupAlreadyExistsException;
import hu.sch.kp.services.exceptions.UserAlreadyExistsException;
import hu.sch.kp.services.UserManagerLocal;
import java.util.Date;
import java.util.LinkedList;
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

    @SuppressWarnings({"unchecked"})
    public List<Felhasznalo> getAllUsers() {
        throw new UnsupportedOperationException();
    }

    public Felhasznalo saveOrAddUser(Felhasznalo user) throws
            UserAlreadyExistsException {
        if (user.getId() != null) {
            em.persist(user);
        } else {
            user = em.merge(user);
        }

        return user;
    }

    public Felhasznalo findUserById(Long userId) {
        try {
            return em.find(Felhasznalo.class, userId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public void addUserToGroup(Felhasznalo felhasznalo, Csoport csoport, Date kezdet, Date veg) {
        CsoporttagsagPK cspk =
                new CsoporttagsagPK(felhasznalo.getId(), csoport.getId());
        Csoporttagsag m = em.find(Csoporttagsag.class, cspk);
        if (m != null) { //már létező csoporttagság
            return;
        }
        Felhasznalo f = em.find(Felhasznalo.class, felhasznalo.getId());
        Csoport cs = em.find(Csoport.class, csoport.getId());
        m = new Csoporttagsag();
        m.setId(cspk);
        m.setJogok(0L);
        m.setFelhasznalo(f);
        m.setCsoport(cs);
        m.setKezdet(kezdet);
        m.setVeg(veg);
        f.getCsoporttagsagok().add(m);
        cs.getCsoporttagsagok().add(m);

        em.merge(m);
        em.merge(f);
        em.merge(cs);
        em.flush();
    }

    @SuppressWarnings({"unchecked"})
    public List<Csoport> getAllGroups() {
        Query q = em.createNamedQuery(Csoport.findAll);

        return q.getResultList();
    }

    public List<String> getEveryGroupName() {
        Query q =
                em.createQuery("SELECT cs.nev FROM Csoport cs " +
                "ORDER BY cs.nev");

        return q.getResultList();
    }

    public List<Csoport> findGroupByName(String name) {
        Query q =
                em.createQuery("SELECT cs FROM Csoport cs " +
                "WHERE cs.nev LIKE :kornev " +
                "ORDER BY cs.nev");
        q.setParameter("kornev", name);

        return q.getResultList();
    }

    public Csoport getGroupByName(String name) {
        Query q = em.createQuery("SELECT cs FROM Csoport cs " +
                "WHERE cs.nev=:kornev");
        q.setParameter("kornev", name);
        try {
            Csoport csoport = (Csoport) q.getSingleResult();
            return csoport;
        } catch (Exception e) {
            return null;
        }
    }

    public Csoport findGroupById(Long id) {
        return em.find(Csoport.class, id);
    }

    public Csoport saveOrAddGroup(Csoport group) throws
            GroupAlreadyExistsException {
        /*Csoport oldgroup = findGroupByName(group.getNev());
        if (oldgroup != null && oldgroup.getId() != group.getId()) {
        throw new GroupAlreadyExistsException("A csoport már létezik: " + group.getNev());
        }*/
        group = em.merge(group);
        em.flush();

        return group;
    }

    public void modifyMembership(Felhasznalo user, Csoport group, Date start, Date end) {
        Csoporttagsag m =
                em.find(Csoporttagsag.class, new CsoporttagsagPK(user.getId(), group.getId()));

        m.setKezdet(start);
        m.setVeg(end);
    }

    public void deleteMembership(Felhasznalo user, Csoport group) {
        /*Query q = em.createNamedQuery(Csoporttagsag.deleteByUserIdAndGroupId);
        q.setParameter("userId", user.getId());
        q.setParameter("groupId", group.getId());
        
        q.executeUpdate();
        em.flush();
        em.clear(); //???*/
    }

    @SuppressWarnings({"unchecked"})
    public List<Felhasznalo> getCsoporttagokWithoutOregtagok(Long csoportId) {
        //Csoport cs = em.find(Csoport.class, csoportId);
        Query q =
                em.createQuery("SELECT cst.felhasznalo FROM Csoporttagsag cst JOIN " +
                "cst.felhasznalo " +
                "WHERE cst.csoport.id=:csoportId AND cst.veg=NULL " +
                "ORDER BY cst.felhasznalo.vezeteknev ASC, cst.felhasznalo.keresztnev ASC");

        q.setParameter("csoportId", csoportId);

        return q.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Felhasznalo> getCsoporttagok(Long csoportId) {
        //Csoport cs = em.find(Csoport.class, csoportId);
        Query q =
                em.createQuery("SELECT cst.felhasznalo FROM Csoporttagsag cst JOIN " +
                "cst.felhasznalo " +
                "WHERE cst.csoport.id=:csoportId " +
                "ORDER BY cst.felhasznalo.vezeteknev ASC, cst.felhasznalo.keresztnev ASC");

        q.setParameter("csoportId", csoportId);

        return q.getResultList();
    }

    public List<BelepoIgeny> getBelepoIgenyekForUser(Felhasznalo felhasznalo) {
        Query q = em.createQuery("SELECT b FROM BelepoIgeny b " +
                "WHERE b.felhasznalo=:felhasznalo " +
                "ORDER BY b.ertekeles.szemeszter DESC, b.belepotipus ASC");
        q.setParameter("felhasznalo", felhasznalo);

        return q.getResultList();
    }

    public List<PontIgeny> getPontIgenyekForUser(Felhasznalo felhasznalo) {
        Query q = em.createQuery("SELECT p FROM PontIgeny p " +
                "WHERE p.felhasznalo=:felhasznalo " +
                "ORDER BY p.ertekeles.szemeszter DESC, p.pont DESC");
        q.setParameter("felhasznalo", felhasznalo);

        return q.getResultList();
    }

    public Csoport getGroupHierarchy() {
        Query q = em.createNamedQuery("groupHierarchy");
        List<Csoport> csoportok = q.getResultList();
        Csoport rootCsoport = new Csoport();
        List<Csoport> rootCsoportok = new LinkedList<Csoport>();
        rootCsoport.setAlcsoportok(rootCsoportok);

        for (Csoport cs : csoportok) {
            if (cs.getSzulo() != null) {
                if (cs.getSzulo().getAlcsoportok() == null) {
                    cs.getSzulo().setAlcsoportok(new LinkedList<Csoport>());
                }
                cs.getSzulo().getAlcsoportok().add(cs);
            } else {
                rootCsoportok.add(cs);
            }
        }

        return rootCsoport;
    }

    public Felhasznalo findUserWithCsoporttagsagokById(Long userId) {
        Query q = em.createNamedQuery("findUserWithCsoporttagsagok");
        q.setParameter("id", userId);
        try {
            Felhasznalo user = (Felhasznalo) q.getSingleResult();
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public Csoport findGroupWithCsoporttagsagokById(Long id) {
        Query q = em.createNamedQuery("findCsoportWithCsoporttagsagok");
        q.setParameter("id", id);
        try {
            Csoport group = (Csoport) q.getSingleResult();
            return group;
        } catch (Exception e) {
            return null;
        }
    }

    public void groupInfoUpdate(Csoport cs) {
        Csoport csoport = em.find(Csoport.class, cs.getId());
        csoport.setAlapitasEve(cs.getAlapitasEve());
        csoport.setNev(cs.getNev());
        csoport.setWebpage(cs.getWebpage());
        csoport.setLeiras(cs.getLeiras());
        csoport.setLevelezoLista(cs.getLevelezoLista());
    }

    public Csoporttagsag getCsoporttagsag(Long userId, Long groupId) {
        Csoporttagsag cst = em.find(Csoporttagsag.class, new CsoporttagsagPK(userId, groupId));
        return cst;
    }

    public void updateMemberRights(Csoporttagsag oldOne, Csoporttagsag newOne, TagsagTipus type) {
        if (type == TagsagTipus.KORVEZETO) {
            if (newOne == null) {
                throw new EJBException();
            }
            if (oldOne != null) {
                Csoporttagsag oldPersisted = em.find(Csoporttagsag.class, oldOne.getId());
                oldPersisted.setJogok(TagsagTipus.addOrRemoveEntitlement(oldPersisted.getJogok(), type));
                oldPersisted.setJogok(TagsagTipus.addOrRemoveEntitlement(oldPersisted.getJogok(), TagsagTipus.VOLTKORVEZETO));
            }
            Csoporttagsag newPersisted = em.find(Csoporttagsag.class, newOne.getId());
            newPersisted.setJogok(TagsagTipus.addOrRemoveEntitlement(newPersisted.getJogok(), type));
        } else {
            if (oldOne != null) {
                Csoporttagsag oldPersisted = em.find(Csoporttagsag.class, oldOne.getId());
                oldPersisted.setJogok(TagsagTipus.addOrRemoveEntitlement(oldPersisted.getJogok(), type));
            }
            if (newOne != null) {
                Csoporttagsag newPersisted = em.find(Csoporttagsag.class, newOne.getId());
                newPersisted.setJogok(TagsagTipus.addOrRemoveEntitlement(newPersisted.getJogok(), type));
            }
        }
    }

    public void setMemberToOldBoy(Csoporttagsag user) {
        Csoporttagsag temp = em.find(Csoporttagsag.class,
                new CsoporttagsagPK(user.getFelhasznalo().getId(), user.getCsoport().getId()));
        temp.setVeg(new Date());
    }

    public Felhasznalo findKorvezetoForCsoport(Long csoportId) {
        Query q = em.createQuery("SELECT cs FROM Csoport cs " +
                "WHERE cs.nev=:kornev");

        em.createQuery("SELECT cst.felhasznalo FROM Csoporttagsag cst JOIN " +
                "cst.felhasznalo " +
                "WHERE cst.csoport.id=:csoportId AND cst.jogok = 1 ");
        q.setParameter("csoportId", csoportId);
        try {
            Felhasznalo user = (Felhasznalo) q.getSingleResult();
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}
