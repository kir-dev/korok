package hu.sch.services;

import hu.sch.domain.user.User;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 * Search functionality.
 * @author tomi
 */
@Local
public interface SearchManagerLocal {

    /**
     * Search users based on the following properties:
     * - full name
     * - email
     * - nick name
     * - email address
     * - room number
     *
     * @param keyword the keyword to search for
     * @return the list of users that matches.
     */
    List<User> searchUsers(String keyword);

    /**
     * Lists all the users whose birtday is the given date.
     * 
     * @param date
     * @return
     */
    List<User> searchBirthdayUsers(Date date);

}
