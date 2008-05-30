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
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author hege
 */
@Stateless
public class UserManagerBean implements UserManagerLocal {

    @PersistenceContext
    EntityManager em;

    @SuppressWarnings({"unchecked"})
    public List<Felhasznalo> getAllUsers() {
        throw new UnsupportedOperationException();
    }

    public Felhasznalo saveOrAddUser(Felhasznalo user) throws UserAlreadyExistsException {
        Felhasznalo olduser = findUserByLoginName(user.getBecenev());
        System.out.println("olduser: " + olduser + " newuser: " + user);
        if (olduser != null && !olduser.getId().equals(user.getId())) { //ha volt már ilyen user, akkor nem mentjük el

            throw new UserAlreadyExistsException("User már létezik ezzel a login névvel: " + user.getBecenev());
        }

        user = em.merge(user);
        em.flush();
        return user;
    }

    public Felhasznalo findUserById(Long userId) {
        try {
            return em.find(Felhasznalo.class, userId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public Felhasznalo findUserByLoginName(String loginName) {
        /*Query q = em.createNamedQuery(Felhasznalo.findByLoginName);
        q.setParameter("loginName", loginName);

        try {
            return (Felhasznalo) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }*/
        throw new UnsupportedOperationException();
    }

    public void addUserToGroup(Felhasznalo felhasznalo, Csoport csoport, Date kezdet, Date veg) {
        Csoporttagsag m = new Csoporttagsag();
        m.setFelhasznalo(felhasznalo);
        m.setCsoport(csoport);
        m.setKezdet(kezdet);
        m.setVeg(veg);
        felhasznalo.getCsoporttagsagok().add(m);
        csoport.getCsoporttagsagok().add(m);

        em.merge(m);
        felhasznalo = em.merge(felhasznalo);
        csoport = em.merge(csoport);
        em.flush();
    }

    @SuppressWarnings({"unchecked"})
    public List<Csoport> getAllGroups() {
        Query q = em.createNamedQuery(Csoport.findAll);

        return q.getResultList();
    }

    public Csoport findGroupByName(String name) {
        /*Query q = em.createNamedQuery(Csoport.findByName);
        q.setParameter("name", name);

        try {
            return (Csoport) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }*/
        throw new UnsupportedOperationException();
    }

    public Csoport saveOrAddGroup(Csoport group) throws GroupAlreadyExistsException {
        /*Csoport oldgroup = findGroupByName(group.getNev());
        if (oldgroup != null && oldgroup.getId() != group.getId()) {
            throw new GroupAlreadyExistsException("A csoport már létezik: " + group.getNev());
        }*/
        group = em.merge(group);
        em.flush();

        return group;
    }

    public void modifyMembership(Felhasznalo user, Csoport group, Date start, Date end) {
        Csoporttagsag m = em.find(Csoporttagsag.class, new CsoporttagsagPK(user.getId(), group.getId()));

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
    public List<Felhasznalo> getCsoporttagok(Long csoportId) {
        //Csoport cs = em.find(Csoport.class, csoportId);
        Query q = em.createQuery("SELECT cst.felhasznalo FROM Csoporttagsag cst JOIN " +
                "cst.felhasznalo " +
                "WHERE cst.csoport.id=:csoportId " +
                "ORDER BY cst.felhasznalo.vezeteknev ASC, cst.felhasznalo.keresztnev ASC");
        
        q.setParameter("csoportId", csoportId);
        
        return q.getResultList();
    }
}
