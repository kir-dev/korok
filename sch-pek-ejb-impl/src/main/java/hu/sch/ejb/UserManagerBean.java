package hu.sch.ejb;

import com.unboundid.ldap.sdk.LDAPException;
import hu.sch.domain.user.User;
import hu.sch.domain.*;
import hu.sch.domain.user.UserAttribute;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.domain.user.UserStatus;
import hu.sch.ejb.ldap.LdapSynchronizer;
import hu.sch.services.*;
import hu.sch.services.exceptions.CreateFailedException;
import hu.sch.services.exceptions.DuplicatedUserException;
import hu.sch.services.exceptions.InvalidPasswordException;
import hu.sch.services.exceptions.UpdateFailedException;
import java.util.*;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hege
 * @author messo
 * @author tomi
 */
@Stateless
public class UserManagerBean implements UserManagerLocal {

    private static Logger logger = LoggerFactory.getLogger(UserManagerBean.class);
    @PersistenceContext
    EntityManager em;
    @EJB(name = "LogManagerBean")
    LogManagerLocal logManager;
    @EJB(name = "MailManagerBean")
    MailManagerLocal mailManager;
    @EJB(name = "PostManagerBean")
    PostManagerLocal postManager;

    @Override
    public User findUserById(Long userId) {
        return findUserById(userId, false);
    }

    @Override
    public User findUserById(Long userId, boolean includeMemberships) {
        if (userId.equals(0L)) {
            // ha nincs használható userId, akkor ne menjünk el a DB-hez.
            return null;
        }

        if (!includeMemberships) {
            return em.find(User.class, userId);
        }

        TypedQuery<User> q = em.createNamedQuery(User.findWithMemberships, User.class);
        q.setParameter("id", userId);

        try {
            return q.getSingleResult();
        } catch (Exception ex) {
            logger.warn("Can't find user with memberships for this id: " + userId);
            return null;
        }
    }

    @Override
    public User findUserByScreenName(String screenName) {
        try {
            return em.createNamedQuery(User.findByScreenName, User.class)
                    .setParameter("screenName", screenName)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.info("User with {} screenname was not found.", screenName);
        }

        return null;
    }

    @Override
    public User findUserByNeptun(final String neptun) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.neptunCode = :neptun", User.class)
                    .setParameter("neptun", neptun)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.info("User not found with {} neptun.", neptun);
        }
        return null;
    }

    @Override
    public User findUserByEmail(final String email) throws DuplicatedUserException {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.emailAddress = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.info("Could not find user with email: {}", email);
        } catch (NonUniqueResultException ex) {
            throw new DuplicatedUserException(String.format("Duplicate user with %s email", email), ex);
        }

        return null;
    }

    @Override
    public List<EntrantRequest> getEntrantRequestsForUser(User felhasznalo) {
        Query q = em.createQuery("SELECT e FROM EntrantRequest e "
                + "WHERE e.user=:user "
                + "ORDER BY e.valuation.semester DESC, e.entrantType ASC");
        q.setParameter("user", felhasznalo);

        return q.getResultList();
    }

    @Override
    public List<PointRequest> getPointRequestsForUser(User felhasznalo) {
        Query q = em.createQuery("SELECT p FROM PointRequest p "
                + "WHERE p.user=:user "
                + "ORDER BY p.valuation.semester DESC, p.valuation.group.name ASC");
        q.setParameter("user", felhasznalo);

        return q.getResultList();
    }

    @Override
    public void createUser(User user, String password, UserStatus status) throws CreateFailedException {
        try (LdapSynchronizer sync = new LdapSynchronizer()) {
            sync.createEntry(user, password, status);
            em.persist(user);
        } catch (InvalidPasswordException ex) {
            throw new CreateFailedException("Password is not valid. It must be at least 6 chars long.", ex);
        } catch (LDAPException ex) {
            throw new CreateFailedException("Could not create entry in DS", ex);
        } catch (Exception ex) {
            throw new CreateFailedException("Unknown error.", ex);
        }
    }

    @Override
    public void updateUser(User user) throws UpdateFailedException {
        try (LdapSynchronizer sync = new LdapSynchronizer()) {
            sync.syncEntry(user);
            em.merge(user);
        } catch (Exception ex) {
            throw new UpdateFailedException("Failed updating the DS entry for the user.", ex);
        }
    }

    @Override
    public List<User> findUsersByName(String name) {
        Query q = em.createQuery("SELECT u FROM User u WHERE UPPER(concat(concat(u.lastName, ' '), "
                + "u.firstName)) LIKE UPPER(:name) "
                + "ORDER BY u.lastName ASC, u.firstName ASC");
        q.setParameter("name", "%" + name + "%");

        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Semester> getAllValuatedSemesterForUser(User user) {
        return em.createNamedQuery(User.getAllValuatedSemesterForUser).setParameter("user", user).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSemesterPointForUser(User user, Semester semester) {
        // Beszerezzük a pontokat
        List<PointRequest> pontigenyek = getPointRequestsForUser(user);

        // Ebbe a Map-be lesz tárolva, hogy melyik körtől hány pontot kapott
        Map<Group, Integer> points = new HashMap<Group, Integer>();
        for (PointRequest pr : pontigenyek) {
            Valuation v = pr.getValuation();
            // Csak ha az adott értékelés a legfrisebb verzió ÉS a pont elfogadott
            if (!v.isObsolete() && v.getPointStatus().equals(ValuationStatus.ELFOGADVA)) {
                // Csak akkor, ha a vizsgált vagy az előző félévre vonatkozik
                if (v.getSemester().equals(semester) || v.getSemester().equals(semester.getPrevious())) {
                    if (points.containsKey(v.getGroup()) == false) {
                        points.put(v.getGroup(), 0);
                    }
                    points.put(v.getGroup(), points.get(v.getGroup()) + pr.getPoint());
                }
            }
        }
        // Az összeg
        int sum = 0;
        // Négyzetösszeget számolunk
        for (Integer pointFromGroup : points.values()) {
            sum += pointFromGroup * pointFromGroup;
        }

        return (int) Math.min(Math.sqrt(sum), 100);
    }

    @Override
    public SpotImage getSpotImage(User user) {
        TypedQuery<SpotImage> q = em.createNamedQuery(SpotImage.findByNeptun, SpotImage.class);
        q.setParameter("neptunCode", user.getNeptunCode());
        try {
            return q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public boolean acceptRecommendedPhoto(String screenName) {
        User user = findUserByScreenName(screenName);

        TypedQuery<SpotImage> q = em.createNamedQuery(SpotImage.findByNeptun, SpotImage.class);
        q.setParameter("neptunCode", user.getNeptunCode());
        try {
            SpotImage si = q.getSingleResult();
            user.setPhotoPath(si.getImagePath());
            em.remove(si);
            return true;
        } catch (NoResultException ex) {
            return false;
        }
    }

    @Override
    public void declineRecommendedPhoto(User user) {
        Query q = em.createNamedQuery(SpotImage.deleteByNeptun);
        q.setParameter("neptunCode", user.getNeptunCode());
        q.executeUpdate();
    }

    @Override
    public void invertAttributeVisibility(User user, UserAttributeName attr) {
        User managedUser = em.merge(user);

        boolean done = false;
        for (UserAttribute a : managedUser.getPrivateAttributes()) {
            if (a.getAttributeName() == attr) {
                a.setVisible(!a.isVisible());
                done = true;
                break;
            }
        }

        // user's attribute list does not contian the given attribute
        // which means it is NOT visible, so we'll make it visible
        if (!done) {
            managedUser.getPrivateAttributes().add(new UserAttribute(attr, true));
        }
    }

    @Override
    public void updateUserStatus(User user, UserStatus userStatus) throws UpdateFailedException {
        try (LdapSynchronizer sync = new LdapSynchronizer()) {
            sync.updateStatus(user, userStatus);
        } catch (LDAPException ex) {
            throw new UpdateFailedException("Could not update the user's status in the directory entry.", ex);
        } catch (Exception ex) {
            throw new UpdateFailedException("Unknown error.", ex);
        }
    }

    @Override
    public void changePassword(String screenName, String oldPwd, String newPwd)
            throws InvalidPasswordException, UpdateFailedException {
        try (LdapSynchronizer sync = new LdapSynchronizer()) {
            sync.changePassword(screenName, oldPwd, newPwd);
        } catch (Exception ex) {
            throw new UpdateFailedException("Could not update password.", ex);
        }
    }
}
