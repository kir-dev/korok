/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb;

import hu.sch.domain.User;
import hu.sch.services.EntitlementManagerRemote;
import hu.sch.services.UserManagerLocal;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author aldaris
 */
@Stateless(mappedName = "EntitlementManager")
public class EntitlementManagerBean implements EntitlementManagerRemote {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    @PersistenceContext
    EntityManager em;
    private final String FINDUSER =
            "SELECT f FROM Felhasznalo f WHERE upper(f.neptunkod) = upper(:neptunkod) OR " +
            "upper(f.emailcim) = upper(:emailcim)";

    public User createUserEntry(User user) {
        if (user.getNeptunCode() != null) {
            Query q = em.createNamedQuery("findUserByNeptunCode");
            q.setParameter("neptun", user.getNeptunCode());
            try {
                User exists = (User) q.getSingleResult();
                return mapReturn(exists);
            } catch (Exception e) {
            }
        }
        User newUser = mapNew(user);
        em.persist(newUser);
        em.flush();
        return mapReturn(newUser);
    }

    protected User mapReturn(User f) {
        User felhasznalo = new User();
        felhasznalo.setId(f.getId());
        felhasznalo.setNeptunCode(f.getNeptunCode());
        felhasznalo.setEmailAddress(f.getEmailAddress());

        return felhasznalo;
    }

    protected User mapNew(User f) {
        User felhasznalo = mapReturn(f);
        felhasznalo.setNeptunCode(f.getNeptunCode());
        felhasznalo.setLastName(f.getLastName());
        felhasznalo.setFirstName(f.getFirstName());
        felhasznalo.setNickName(f.getNickName());
        felhasznalo.setEmailAddress(f.getEmailAddress());

        return felhasznalo;
    }

    public User findUser(String neptun, String email) {
        return mapReturn((User) em.createQuery(FINDUSER).
                setParameter("neptunkod", neptun).
                setParameter("emailcim", email).
                getSingleResult());
    }

    public User findUser(Long virId) {
        return mapReturn(em.find(User.class, virId));
    }
}
