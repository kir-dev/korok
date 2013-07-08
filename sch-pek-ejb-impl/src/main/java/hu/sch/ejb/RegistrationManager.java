package hu.sch.ejb;

import hu.sch.domain.profile.Person;
import hu.sch.domain.RegisteringPerson;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.RegistrationManagerLocal;
import hu.sch.services.exceptions.InvalidNewbieStateException;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.services.exceptions.UserAlreadyExistsException;
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

        if (!checksIfUserExists(registeringPerson.getNeptun())) {
            final TypedQuery<RegisteringPerson> neptunQuery =
                    em.createNamedQuery(RegisteringPerson.findRegPersonByNeptun, RegisteringPerson.class);

            neptunQuery.setParameter("neptun", registeringPerson.getNeptun().toUpperCase());
            neptunQuery.setParameter("dateOfBirth", registeringPerson.getDateOfBirth());

            try {
                final RegisteringPerson dbPerson = neptunQuery.getSingleResult();

                //newbie tries to register as active student
                if (dbPerson.isNewbie() && !registeringPerson.isNewbie()) {
                    throw new InvalidNewbieStateException("reg.neptun.error.invalid-newbie-state.newbie-as-active");
                }

                //active student tries to register as newbie
                if (!dbPerson.isNewbie() && registeringPerson.isNewbie()) {
                    throw new InvalidNewbieStateException("reg.neptun.error.invalid-newbie-state.active-as-newbie");
                }

            } catch (NoResultException ex) {
                throw new PersonNotFoundException("reg.neptun.error.invalid-neptun-dateOfBirth");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canPersonRegisterWithEducationId(final RegisteringPerson registeringPerson)
            throws UserAlreadyExistsException {

        //checkExistingPerson(neptun);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reg(final RegisteringPerson registeringPerson) {
//        person.setStatus("Inactive");
//        ldapManager.registerPerson(person, newPass);
    }

    /**
     * Search the user in the ldap with the given neptun code.
     *
     * @param neptun
     * @return false if user is not registered yet
     * @throws UserAlreadyExistsException when user already registered, with
     * different key in the message whether the user is active or not
     */
    private boolean checksIfUserExists(final String neptun) throws UserAlreadyExistsException {
        try {
            final Person dummy = ldapManager.getPersonByNeptun(neptun);
            if (dummy.isActive()) {
                throw new UserAlreadyExistsException("reg.neptun.error.user_exists.active", dummy.getUid());
            }
            throw new UserAlreadyExistsException("reg.neptun.error.user_exists.inactive", dummy.getUid());
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
}
