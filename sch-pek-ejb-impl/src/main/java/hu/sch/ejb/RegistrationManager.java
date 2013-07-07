package hu.sch.ejb;

import hu.sch.domain.profile.Person;
import hu.sch.domain.RegisteringPerson;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.RegistrationManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.services.exceptions.UserAlreadyExistsException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author balo
 */
@Stateless
public class RegistrationManager implements RegistrationManagerLocal {

    @PersistenceContext
    EntityManager em;
    //
    @EJB(name = "LdapManagerBean")
    LdapManagerLocal ldapManager;
    //
    private static Logger log = LoggerFactory.getLogger(RegistrationManager.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canPersonRegisterWithNeptun(final RegisteringPerson registeringPerson)
            throws UserAlreadyExistsException {

        //checkExistingPerson(neptun);
        return false;
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

    private boolean existingPerson(final String neptun) throws UserAlreadyExistsException {
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
}
