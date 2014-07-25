package hu.sch.ejb;

import hu.sch.domain.enums.SvieMembershipType;
import hu.sch.domain.enums.SvieStatus;
import hu.sch.domain.user.RegisteringUser;
import hu.sch.domain.user.StudentStatus;
import hu.sch.domain.user.Gender;
import hu.sch.domain.user.User;
import hu.sch.domain.user.UserStatus;
import hu.sch.services.AccountManager;
import hu.sch.services.RegistrationManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.exceptions.InvalidNewbieStateException;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.services.exceptions.UserAlreadyExistsException;
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
    public boolean canUserRegisterWithNeptun(final RegisteringUser registeringUser)
            throws UserAlreadyExistsException, InvalidNewbieStateException, UserNotFoundException {

        final User user = userManager.findUserByNeptun(registeringUser.getNeptun());
        if (user == null) {
            final TypedQuery<RegisteringUser> neptunQuery =
                    em.createNamedQuery(RegisteringUser.findRegUserByNeptun, RegisteringUser.class);

            neptunQuery.setParameter("neptun", registeringUser.getNeptun().toUpperCase());
            neptunQuery.setParameter("dateOfBirth", registeringUser.getDateOfBirth());

            try {
                final RegisteringUser dbPerson = neptunQuery.getSingleResult();
                checkNewbieState(dbPerson, registeringUser);
                return true;
                //
            } catch (NoResultException ex) {
                throw new UserNotFoundException("reg.error.invalid-neptun-dateOfBirth");
            }
        } else {
            throw new UserAlreadyExistsException("reg.error.user_exists", user.getScreenName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canUserRegisterWithEducationId(final RegisteringUser registeringUser)
            throws UserAlreadyExistsException, InvalidNewbieStateException, UserNotFoundException {

        final RegisteringUser dbPerson = findRegUserByEducationId(registeringUser);
        final User user = userManager.findUserByNeptun(dbPerson.getNeptun());

        if (user == null) {
            checkNewbieState(dbPerson, registeringUser);
            return true;
        } else {
            throw new UserAlreadyExistsException("reg.error.user_exists", user.getScreenName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRegistration(final RegisteringUser regUser, final String password)
            throws UserNotFoundException, PekEJBException {

        final User user = new User();
        user.setScreenName(regUser.getScreenName());
        user.setEmailAddress(regUser.getMail());
        user.setFirstName(regUser.getFirstName());
        user.setLastName(regUser.getLastName());
        user.setDateOfBirth(new Date(regUser.getDateOfBirth().getTime()));
        user.setStudentStatus(regUser.isNewbie() ? StudentStatus.NEWBIE : StudentStatus.ACTIVE);
        user.setSvieStatus(SvieStatus.NEMTAG);
        user.setSvieMembershipType(SvieMembershipType.NEMTAG);
        user.setGender(Gender.NOTSPECIFIED);
        user.setUserStatus(UserStatus.INACTIVE);

        //if we don't have the neptun code, we have to search user by educationId
        if (regUser.getNeptun() == null || regUser.getNeptun().isEmpty()) {
            final RegisteringUser dbRegPerson = findRegUserByEducationId(regUser);
            regUser.setNeptun(dbRegPerson.getNeptun());
        }

        user.setNeptunCode(regUser.getNeptun());

        accountManager.createUser(user, password, null);
    }

    private RegisteringUser findRegUserByEducationId(final RegisteringUser regUser)
            throws UserNotFoundException {
        //get user's neptun from the database
        final TypedQuery<RegisteringUser> educationIdQuery =
                em.createNamedQuery(RegisteringUser.findRegUserByEducationId, RegisteringUser.class);

        educationIdQuery.setParameter("educationid", regUser.getEducationId());
        educationIdQuery.setParameter("dateOfBirth", regUser.getDateOfBirth());

        try {
            final RegisteringUser dbPerson = educationIdQuery.getSingleResult();
            return dbPerson;
        } catch (NoResultException ex) {
            throw new UserNotFoundException("reg.error.invalid-educationId-dateOfBirth");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUidTaken(final String uid) {
        final User user = userManager.findUserByScreenName(uid);

        return user != null;
    }

    /**
     * Checks if the user tries to register with wrong registration method. Ex.:
     * He's a newbie and tries as active or he's active and tries as newbie
     *
     * @param dbUser
     * @param registeringUser
     * @throws InvalidNewbieStateException when user tries to register with a
     * wrong method, with different key in the message whether the user is
     * newbie or not
     */
    private void checkNewbieState(final RegisteringUser dbUser, final RegisteringUser registeringUser)
            throws InvalidNewbieStateException {
        //newbie tries to register as active student
        if (dbUser.isNewbie() && !registeringUser.isNewbie()) {
            throw new InvalidNewbieStateException("reg.error.invalid-newbie-state.newbie-as-active");
        }

        //active student tries to register as newbie
        if (!dbUser.isNewbie() && registeringUser.isNewbie()) {
            throw new InvalidNewbieStateException("reg.error.invalid-newbie-state.active-as-newbie");
        }
    }
}
