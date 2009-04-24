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
@Stateless(mappedName="EntitlementManager")
public class EntitlementManagerBean implements EntitlementManagerRemote {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    @PersistenceContext
    EntityManager em;

    public Felhasznalo createUserEntry(Felhasznalo user) {
        if (user.getNeptunkod() != null) {
            Query q = em.createNamedQuery("findUserByNeptunCode");
            q.setParameter("neptun", user.getNeptunkod());
            try {
                Felhasznalo exists = (Felhasznalo) q.getSingleResult();
                return mapReturn(exists);
            } catch (Exception e) {
            }
        }
        Felhasznalo newUser = mapNew(user);
        em.persist(newUser);
        em.flush();
        return mapReturn(newUser);
    }

    protected Felhasznalo mapReturn(Felhasznalo f) {
        Felhasznalo felhasznalo = new Felhasznalo();
        felhasznalo.setId(f.getId());
        felhasznalo.setNeptunkod(f.getNeptunkod());
        
        return felhasznalo;
    }

    protected Felhasznalo mapNew(Felhasznalo f) {
        Felhasznalo felhasznalo = mapReturn(f);
        felhasznalo.setNeptunkod(f.getNeptunkod());
        felhasznalo.setVezeteknev(f.getVezeteknev());
        felhasznalo.setKeresztnev(f.getKeresztnev());
        felhasznalo.setBecenev(f.getBecenev());
        felhasznalo.setEmailcim(f.getEmailcim());

        return felhasznalo;
    }
}
