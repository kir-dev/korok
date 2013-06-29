package hu.sch.ejb;

import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.SvieStatus;
import hu.sch.domain.User;
import hu.sch.services.EntitlementManagerLocal;
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
public class EntitlementManagerBean implements EntitlementManagerLocal {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    @PersistenceContext
    EntityManager em;
    private static final long serialVersionUID = 1L;

    public User createUserEntry(User user) {
        if (user.getNeptunCode() != null) {
            Query q = em.createNamedQuery(User.findUserByNeptunCode);
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
        User user = mapReturn(f);
        user.setNeptunCode(f.getNeptunCode());
        user.setLastName(f.getLastName());
        user.setFirstName(f.getFirstName());
        user.setNickName(f.getNickName());
        user.setEmailAddress(f.getEmailAddress());
        user.setSvieMembershipType(SvieMembershipType.NEMTAG);
        user.setSvieStatus(SvieStatus.NEMTAG);

        return user;
    }

    public User findUser(String neptun, String email) {
        return mapReturn((User) em.createNamedQuery(User.findUser).
                setParameter("neptunkod", neptun).
                setParameter("emailcim", email).
                getSingleResult());
    }

    public User findUser(Long virId) {
        return mapReturn(em.find(User.class, virId));
    }
}
