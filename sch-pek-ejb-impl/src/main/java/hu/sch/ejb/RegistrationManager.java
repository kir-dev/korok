package hu.sch.ejb;

import hu.sch.domain.user.StudentStatus;
import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.domain.user.UserStatus;
import hu.sch.services.AccountManager;
import hu.sch.services.RegistrationManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.config.Configuration;
import hu.sch.services.dto.RegisteringUser;
import hu.sch.services.exceptions.PekEJBException;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author balo
 */
@Stateless
public class RegistrationManager implements RegistrationManagerLocal {

    private static final UserAttributeName[] VISIBLE_ATTRIBUTES = new UserAttributeName[]{
        UserAttributeName.CELL_PHONE,
        UserAttributeName.EMAIL,
        UserAttributeName.SCREEN_NAME,
        UserAttributeName.ROOM_NUMBER,
    };

    @PersistenceContext
    private EntityManager em;
    @Inject
    private UserManagerLocal userManager;
    @Inject
    private AccountManager accountManager;
    @Inject
    private Configuration config;

    /**
     * {@inheritDoc}
     */
    @Override
    public User doRegistration(final RegisteringUser regUser) throws PekEJBException {
        User user = buildUser(regUser);
        user = accountManager.createUser(user);
        setVisibleAttributes(user);
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUidTaken(final String uid) {
        final User user = userManager.findUserByScreenName(uid);

        return user != null;
    }

    private User buildUser(final RegisteringUser regUser) {
        final User user = new User();
        user.setScreenName(regUser.getScreenName());
        user.setEmailAddress(regUser.getMail());
        user.setFirstName(regUser.getFirstName());
        user.setLastName(regUser.getLastName());
        user.setStudentStatus(config.getNewUserStudentStatus());
        user.setUserStatus(UserStatus.ACTIVE);
        user.setAuthSchId(regUser.getAuthSchId());
        user.setBmeId(regUser.getBmeId());
        user.setDormitory(regUser.getDormitory());
        user.setRoom(regUser.getRoomNumber());
        user.setCreatedAt(new Date());
        return user;
    }

    private void setVisibleAttributes(User user) {
        for (UserAttributeName attr : VISIBLE_ATTRIBUTES) {
            userManager.invertAttributeVisibility(user, attr);
        }
    }

}
