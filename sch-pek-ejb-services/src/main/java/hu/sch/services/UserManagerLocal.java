package hu.sch.services;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.PointHistory;
import hu.sch.domain.SpotImage;
import hu.sch.domain.user.User;
import hu.sch.domain.user.ProfileImage;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.services.exceptions.DuplicatedUserException;
import hu.sch.services.exceptions.PekEJBException;
import javax.ejb.Local;
import java.util.List;

/**
 * Felhasználó kezelés, lokális interfész
 *
 * @author hege
 * @author tomi
 */
@Local
public interface UserManagerLocal {

    /**
     * Gets the user with the specified id.
     *
     * @param userId
     * @return the user or null if there is no user with the id
     */
    User findUserById(Long userId);

    /**
     * Gets the user with the specified id.
     *
     * @param userId
     * @param includeMemberships set it to true and it prefetches the
     * memberships
     * @return the user or null if there is no user with the id
     */
    User findUserById(Long userId, boolean includeMemberships);

    /**
     * Gets the users whose name contains the given fragment.
     *
     * @param nameFragment the name fragment to look for
     * @return list of users, or empty list if there is no matching user.
     */
    List<User> findUsersByName(String nameFragment);

    /**
     * Gets the user with the specific screen name (~username)
     *
     * @param screenName
     * @return the user of null if not found.
     */
    public User findUserByScreenName(String screenName);

    /**
     * Gets the user with the specified neptun code.
     *
     * @param neptun neptun code of the user to lookup
     * @return user or null if the user cannot be found.
     */
    public User findUserByNeptun(String neptun);

    /**
     * Gets the user with the specified neptun code.
     *
     * @param neptun neptun code of the user to lookup
     * @param includeMemberships set it to true and it prefetches the
     * memberships
     * @return user or null if the user cannot be found.
     */
    public User findUserByNeptun(String neptun, boolean includeMemberships);

    /**
     * Gets the user with specified email.
     *
     * @param email the email to look for
     * @return the user or null if no match was found
     * @throws DuplicatedUserException more than one user has the given email.
     * Email should be unique.
     */
    public User findUserByEmail(String email) throws DuplicatedUserException;

    /**
     * Gets the user with specified confirmation code.
     *
     * @param code the confirmation code to look for
     * @return
     */
    public User findUserByConfirmationCode(String code);

    /**
     * Get entrant requests for the given user.
     *
     * @param user
     * @return
     */
    List<EntrantRequest> getEntrantRequestsForUser(User user);

    /**
     * Update user in the database and synchronize the directory service.
     *
     * @param user
     */
    void updateUser(User user) throws PekEJBException;

    /**
     * Update user in the database and synchronize the directory service.
     *
     * Also update the user's profile image: resize it, store it and delete the
     * old one.
     *
     * @param user the user to update
     * @param image the new profile image
     * @throws PekEJBException
     */
    public void updateUser(User user, ProfileImage image) throws PekEJBException;

    /**
     * Gets all semester with point when point is greater 0. Result order by
     * semester decreasing.
     *
     * Egy szemeszterhez tartozó pontot így kapunk: az aktuális és az előző
     * félévben szerzett pontjait körönként összeadjuk, majd ezek négyzetes
     * közepét vesszük. Legfeljebb 100 lehet, és lefele kerekítjük.
     *
     * @param user
     * @return
     */
    public List<PointHistory> getCommunityPointsForUser(User user);

    /**
     * Lekérjük egy adott felhasználóhoz tartozó SPOT képet, ha van ilyen
     *
     * @param user
     * @return spot kép, vagy null
     */
    SpotImage getSpotImage(User user);

    /**
     * Az adott felhasználónévvel rendelkező usernek megpróbáljuk beállítani a
     * javasolt fotót.
     *
     * @param screenName
     * @return sikeres volt-e a beállítás
     */
    boolean acceptRecommendedPhoto(String screenName);

    /**
     * Az adott felhasználó elutasította a javasolt fotót, töröljük a
     * SpotImage-t a DB-ből.
     *
     * @param user
     */
    void declineRecommendedPhoto(User user);

    /**
     * Inverts the visibility of an attribute.
     *
     * @param user
     * @param attr the attribute which visibility has to be altered
     */
    public void invertAttributeVisibility(User user, UserAttributeName attr);

    /**
     * Remove user's profile image.
     * @param user
     */
    public void removeProfileImage(User user) throws PekEJBException;
}
