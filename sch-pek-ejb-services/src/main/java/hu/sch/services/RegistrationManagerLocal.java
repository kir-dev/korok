package hu.sch.services;

import hu.sch.domain.user.RegisteringUser;
import hu.sch.services.exceptions.InvalidNewbieStateException;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.services.exceptions.UserAlreadyExistsException;
import hu.sch.services.exceptions.UserNotFoundException;
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
     * It never returns false, it throws appropriate exception when the
     * requirements are not met.
     *
     * @param registeringUser
     * @return true if there is a neptun code with the given date of birth and
     * newbie status in our list
     * @throws IllegalArgumentException the user gives invalid neptun-date of
     * birth pair
     * @throws UserAlreadyExistsException user already registered
     * @throws InvalidNewbieStateException the user chooses different newbie
     * state than we have
     */
    boolean canUserRegisterWithNeptun(final RegisteringUser registeringUser)
            throws UserAlreadyExistsException, InvalidNewbieStateException, UserNotFoundException;

    /**
     * Checks if there is an eductaion id with the given date of birth and
     * newbie status in our list.
     *
     * It never returns false, it throws appropriate exception when the
     * requirements are not met.
     *
     * @param registeringUser
     * @return true if there is an education id with the given date of birth and
     * newbie status in our list
     * @throws UserAlreadyExistsException
     * @throws InvalidNewbieStateException
     * @throws UserNotFoundException
     */
    boolean canUserRegisterWithEducationId(final RegisteringUser registeringUser)
            throws UserAlreadyExistsException, InvalidNewbieStateException, UserNotFoundException;

    /**
     *
     * @param registeringUser
     */
    void doRegistration(final RegisteringUser registeringUser, final String password)
            throws UserNotFoundException, PekEJBException;

    /**
     * Checks if the given uid already registered.
     *
     * @param uid
     * @return
     */
    boolean isUidTaken(final String uid);
}
