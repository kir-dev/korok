package hu.sch.services;

import hu.sch.domain.user.LostPasswordToken;
import hu.sch.domain.user.User;
import hu.sch.services.exceptions.PekEJBException;
import javax.ejb.Local;

/**
 * Account management. User creation, confirmation, password changing and
 * recover lost credentials.
 *
 * @author balo
 */
@Local
public interface AccountManager {

    /**
     * Confirms a user's registration.
     *
     * @param user
     * @param password
     *
     * @throws hu.sch.services.exceptions.PekEJBException
     */
    void confirm(User user, String password) throws PekEJBException;

    /**
     * Create a new user.
     *
     * Add the user to the directory service as well.
     *
     * @param user the user to be created
     * @param password
     * @param currentUserId currently logged in user. It can be null if no user
     * is logged in yet.
     *
     * @throws hu.sch.services.exceptions.PekEJBException
     */
    void createUser(User user, String password, Long currentUserId) throws PekEJBException;

    /**
     * Changes the user's password.
     *
     * @param screenName the user's screen name (username)
     * @param oldPwd
     * @param newPwd
     * @throws hu.sch.services.exceptions.PekEJBException if the old password
     * does not match the stored one.
     */
    void changePassword(String screenName, String oldPwd, String newPwd)
            throws PekEJBException;

    /**
     * Searches the user in the datastore by email and sends an email with the
     * screen name. It sends different messages depends on
     * {@link SystemManagerLocal#getNewbieTime()}.
     *
     * @param email
     * @return true if we found the user and the email sent successfully.
     * @throws hu.sch.services.exceptions.PekEJBException when user not found.
     * @throws IllegalArgumentException when the argument is null or empty.
     */
    boolean sendUserNameReminder(String email) throws PekEJBException;

    /**
     * Searches the user in the datastore by email and sends an email with a
     * password change link and the screen name. It sends different messages
     * depends on {@link SystemManagerLocal#getNewbieTime()}.
     *
     * @param email
     * @return true if we found the user and the email sent successfully.
     * @throws hu.sch.services.exceptions.PekEJBException when user not found.
     * @throws IllegalArgumentException when the argument is null or empty.
     */
    boolean sendLostPasswordChangeLink(String email) throws PekEJBException;

    /**
     * Searches the user in the datastore by the {@link LostPasswordToken#token}
     * and returns it if found.
     *
     * @param tokenKey
     * @return
     * @throws hu.sch.services.exceptions.PekEJBException if the token is
     * invalid or expired, or any persistence exception occured
     */
    public User getUserByLostPasswordToken(String tokenKey) throws PekEJBException;

    /**
     * If the given token key is valid then replace the password of
     * {@link LostPasswordToken#subjectUser} with the given value.
     *
     * @param tokenKey
     * @param password
     * @throws hu.sch.services.exceptions.PekEJBException if the token expired
     * or invalid
     */
    void replaceLostPassword(String tokenKey, String password) throws PekEJBException;

    /**
     * Removes expired tokens from the database.
     */
    void removeExpiredLostPasswordTokens();

    /**
     * Authenticate user.
     *
     * @param username
     * @param password
     * @return true only if username and password matches
     */
    boolean authenticate(String username, String password);
}
