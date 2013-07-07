package hu.sch.services;

import hu.sch.domain.profile.RegisteringPerson;
import hu.sch.services.exceptions.UserAlreadyExistsException;
import javax.ejb.Local;

/**
 *
 * @author balo
 */
@Local
public interface RegistrationManagerLocal {

    /**
     * Checks if there is a neptun code with the given date of birth and newbie
     * status in our list.
     *
     * @param registeringPerson
     * @return
     */
    boolean canPersonRegisterWithNeptun(final RegisteringPerson registeringPerson) throws UserAlreadyExistsException;

    /**
     * Checks if there is an eductaion id with the given date of birth and
     * newbie status in our list.
     *
     * @param registeringPerson
     * @return
     */
    boolean canPersonRegisterWithEducationId(final RegisteringPerson registeringPerson) throws UserAlreadyExistsException;

    /**
     *
     * @param registeringPerson
     */
    void reg(final RegisteringPerson registeringPerson);
}
