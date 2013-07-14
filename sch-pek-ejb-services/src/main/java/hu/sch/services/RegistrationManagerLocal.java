package hu.sch.services;

import hu.sch.domain.RegisteringPerson;
import hu.sch.services.exceptions.InvalidNewbieStateException;
import hu.sch.services.exceptions.PersonNotFoundException;
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
     * @throws IllegalArgumentException the user gives invalid neptun-date of birth pair
     * @throws UserAlreadyExistsException user already registered
     * @throws InvalidNewbieStateException the user chooses different newbie state than we have
     */
    void canPersonRegisterWithNeptun(final RegisteringPerson registeringPerson)
            throws UserAlreadyExistsException, InvalidNewbieStateException, PersonNotFoundException;

    /**
     * Checks if there is an eductaion id with the given date of birth and
     * newbie status in our list.
     *
     * @param registeringPerson
     */
    void canPersonRegisterWithEducationId(final RegisteringPerson registeringPerson)
            throws UserAlreadyExistsException, InvalidNewbieStateException, PersonNotFoundException;

    /**
     *
     * @param registeringPerson
     */
    void doRegistration(final RegisteringPerson registeringPerson, final String password)
            throws PersonNotFoundException;

    /**
     * Checks if the given uid already registered.
     *
     * @param uid
     * @return
     */
    boolean isUidTaken(final String uid);
}
