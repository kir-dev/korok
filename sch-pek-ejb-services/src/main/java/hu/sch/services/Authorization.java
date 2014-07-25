package hu.sch.services;

import hu.sch.domain.user.User;
import javax.ejb.Local;

/**
 *
 * @author tomi
 */
@Local
public interface Authorization {

    boolean hasRole(Long userId, Role role);

    boolean hasRole(User user, Role role);
}
