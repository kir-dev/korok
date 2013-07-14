package hu.sch.ejb;

import hu.sch.domain.profile.Person;
import hu.sch.domain.RegisteringPerson;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.RegistrationManagerLocal;
import hu.sch.services.exceptions.InvalidNewbieStateException;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.services.exceptions.UserAlreadyExistsException;
import java.text.SimpleDateFormat;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.log4j.Logger;

/**
 *
 * @author balo
 */
@Stateless
public class RegistrationManager implements RegistrationManagerLocal {

    @PersistenceContext
    private EntityManager em;
    //
    @EJB(name = "LdapManagerBean")
    private LdapManagerLocal ldapManager;
    //
    private static Logger log = Logger.getLogger(RegistrationManager.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void canPersonRegisterWithNeptun(final RegisteringPerson registeringPerson)
            throws UserAlreadyExistsException, InvalidNewbieStateException, PersonNotFoundException {

        if (!isUserExists(registeringPerson.getNeptun())) {
            final TypedQuery<RegisteringPerson> neptunQuery =
                    em.createNamedQuery(RegisteringPerson.findRegPersonByNeptun, RegisteringPerson.class);

            neptunQuery.setParameter("neptun", registeringPerson.getNeptun().toUpperCase());
            neptunQuery.setParameter("dateOfBirth", registeringPerson.getDateOfBirth());

            try {
                final RegisteringPerson dbPerson = neptunQuery.getSingleResult();
                checkNewbieState(dbPerson, registeringPerson);
                //
            } catch (NoResultException ex) {
                throw new PersonNotFoundException("reg.error.invalid-neptun-dateOfBirth");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void canPersonRegisterWithEducationId(final RegisteringPerson registeringPerson)
            throws UserAlreadyExistsException, InvalidNewbieStateException, PersonNotFoundException {

        final RegisteringPerson dbPerson = findRegPersonByEducationId(registeringPerson);
        isUserExists(dbPerson.getNeptun());
        checkNewbieState(dbPerson, registeringPerson);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRegistration(final RegisteringPerson regPerson, final String password)
            throws PersonNotFoundException {

        final Person person = new Person();
        person.setUid(regPerson.getUid());
        person.setMail(regPerson.getMail());
        person.setFirstName(regPerson.getFirstName());
        person.setLastName(regPerson.getLastName());
        person.setDateOfBirth(new SimpleDateFormat("yyyyMMdd").format(regPerson.getDateOfBirth()));
        person.setStudentStatus(regPerson.isNewbie() ? "newbie" : "active");
        person.setStatus("Inactive");

        //if we don't have the neptun code, we have to search user by educationId
        if (regPerson.getNeptun() == null || regPerson.getNeptun().isEmpty()) {
            final RegisteringPerson dbRegPerson = findRegPersonByEducationId(regPerson);
            regPerson.setNeptun(dbRegPerson.getNeptun());
        }

        person.setNeptun(regPerson.getNeptun());

        ldapManager.register(person, password, regPerson.isNewbie());
    }

    private RegisteringPerson findRegPersonByEducationId(final RegisteringPerson regPerson)
            throws PersonNotFoundException {
        //get user's neptun from the database
        final TypedQuery<RegisteringPerson> educationIdQuery =
                em.createNamedQuery(RegisteringPerson.findRegPersonByEducationId, RegisteringPerson.class);

        educationIdQuery.setParameter("educationid", regPerson.getEducationId());
        educationIdQuery.setParameter("dateOfBirth", regPerson.getDateOfBirth());

        try {
            final RegisteringPerson dbPerson = educationIdQuery.getSingleResult();
            return dbPerson;
        } catch (NoResultException ex) {
            throw new PersonNotFoundException("reg.error.invalid-educationId-dateOfBirth");
        }
    }

    /**
     * Search the user in the ldap with the given neptun code.
     *
     * @param neptun
     * @return false if user is not registered yet, never returns true
     * @throws UserAlreadyExistsException when user already registered, with
     * different key in the message whether the user is active or not
     */
    private boolean isUserExists(final String neptun) throws UserAlreadyExistsException {
        try {
            final Person dummy = ldapManager.getPersonByNeptun(neptun);
            if (dummy.isActive()) {
                throw new UserAlreadyExistsException("reg.error.user_exists.active", dummy.getUid());
            }
            throw new UserAlreadyExistsException("reg.error.user_exists.inactive", dummy.getUid());
        } catch (PersonNotFoundException pnfe) {
        }

        return false;
    }

    @Override
    public boolean isUidTaken(final String uid) {
        try {
            ldapManager.getPersonByUid(uid);
            return true;
        } catch (PersonNotFoundException ex) {
        }

        return false;
    }

    /**
     * Checks if the user tries to register with wrong registration method. Ex.:
     * He's a newbie and tries as active or he's active and tries as newbie
     *
     * @param dbPerson
     * @param registeringPerson
     * @throws InvalidNewbieStateException when user tries to register with a
     * wrong method, with different key in the message whether the user is
     * newbie or not
     */
    private void checkNewbieState(final RegisteringPerson dbPerson, final RegisteringPerson registeringPerson)
            throws InvalidNewbieStateException {
        //newbie tries to register as active student
        if (dbPerson.isNewbie() && !registeringPerson.isNewbie()) {
            throw new InvalidNewbieStateException("reg.error.invalid-newbie-state.newbie-as-active");
        }

        //active student tries to register as newbie
        if (!dbPerson.isNewbie() && registeringPerson.isNewbie()) {
            throw new InvalidNewbieStateException("reg.error.invalid-newbie-state.active-as-newbie");
        }
    }
}
