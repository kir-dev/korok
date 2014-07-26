package hu.sch.ejb;

import hu.sch.domain.enums.SvieMembershipType;
import hu.sch.domain.enums.SvieStatus;
import hu.sch.domain.user.Gender;
import hu.sch.domain.user.StudentStatus;
import hu.sch.domain.user.User;
import hu.sch.domain.user.UserStatus;
import hu.sch.services.AccountManager;
import hu.sch.services.RegistrationManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.dto.RegisteringUser;
import hu.sch.services.exceptions.InvalidNewbieStateException;
import hu.sch.services.exceptions.UserNotFoundException;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author balo
 */
@Stateless
public class RegistrationManager implements RegistrationManagerLocal {

    @PersistenceContext
    private EntityManager em;
    //
    @EJB(name = "UserManagerBean")
    private UserManagerLocal userManager;
    @EJB(name = "AccountManagerBean")
    private AccountManager accountManager;
    //
    private static final Logger logger = LoggerFactory.getLogger(RegistrationManager.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public User doRegistration(final RegisteringUser regUser) {

        final User user = new User();
        user.setScreenName(regUser.getScreenName());
        user.setEmailAddress(regUser.getMail());
        user.setFirstName(regUser.getFirstName());
        user.setLastName(regUser.getLastName());
        user.setStudentStatus(StudentStatus.ACTIVE);
        user.setUserStatus(UserStatus.ACTIVE);

        user.setAuthSchId(regUser.getAuthSchId());
        user.setBmeId(regUser.getBmeId());

        user.setDormitory(regUser.getDormitory());
        user.setRoom(regUser.getRoomNumber());

        return accountManager.createUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUidTaken(final String uid) {
        final User user = userManager.findUserByScreenName(uid);

        return user != null;
    }

}
