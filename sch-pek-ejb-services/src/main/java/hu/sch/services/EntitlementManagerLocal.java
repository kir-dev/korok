package hu.sch.services;

import hu.sch.domain.user.User;
import javax.ejb.Local;

/**
 *
 * @author aldaris
 */
@Local
public interface EntitlementManagerLocal {

    User createUserEntry(User user);

    User findUser(String neptun, String email);

    User findUser(Long virId);
}
