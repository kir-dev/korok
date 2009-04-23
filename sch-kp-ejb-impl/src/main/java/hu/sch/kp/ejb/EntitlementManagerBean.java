/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.ejb;

import hu.sch.domain.Felhasznalo;
import hu.sch.kp.services.EntitlementManagerRemote;
import hu.sch.kp.services.UserManagerLocal;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author aldaris
 */
@Stateless
public class EntitlementManagerBean implements EntitlementManagerRemote {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    @PersistenceContext
    EntityManager em;

    public Felhasznalo createUserEntry(Felhasznalo user) {
        if (!user.getNeptunkod().isEmpty()) {
            Query q = em.createNamedQuery("findUserByNeptunCode");
            q.setParameter("neptun", user.getNeptunkod());
            try {
                Felhasznalo exists = (Felhasznalo) q.getSingleResult();
                return exists;
            } catch (Exception e) {
            }
        }
        em.persist(user);
        em.flush();
        return user;
    }
}
