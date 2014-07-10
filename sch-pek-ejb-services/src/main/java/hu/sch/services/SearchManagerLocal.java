package hu.sch.services;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 * Search functionality.
 *
 * @author tomi
 */
@Local
public interface SearchManagerLocal {

    /**
     * Search users based on the following properties: - full name - email -
     * nick name - screen name (username) - room number
     *
     * @param keyword the keyword to search for
     * @return the list of users that matches.
     */
    List<User> searchUsers(String keyword, int page, int perPage);

    /**
     * Lists all the users whose birtday is the given date.
     *
     * @param date
     * @return
     */
    List<User> searchBirthdayUsers(Date date);

    /**
     * Search groups by their name and shortname
     * @param term
     * @param page
     * @param perPage
     * @return
     */
    List<Group> searchGroups(String term, int page, int perPage);

    /**
     * Count the users for a given term.
     *
     * @param term
     * @return
     */
    long countUsers(String term);

    /**
     * Count the group for a given term.
     *
     * @param term
     * @return
     */
    long countGroup(String term);

}
