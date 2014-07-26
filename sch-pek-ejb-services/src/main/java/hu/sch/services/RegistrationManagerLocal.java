package hu.sch.services;

import hu.sch.domain.user.User;
import hu.sch.services.dto.RegisteringUser;
import javax.ejb.Local;

/**
 *
 * @author balo
 */
@Local
public interface RegistrationManagerLocal {

    /**
     * Register user.
     * @param registeringUser
     */
    User doRegistration(final RegisteringUser registeringUser);

    /**
     * Checks if the given uid already registered.
     *
     * @param uid
     * @return
     */
    boolean isUidTaken(final String uid);
}
