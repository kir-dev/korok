package hu.sch.ejb;

import hu.sch.domain.config.Configuration;
import hu.sch.domain.enums.SvieMembershipType;
import hu.sch.domain.enums.SvieStatus;
import hu.sch.domain.user.Gender;
import hu.sch.domain.user.LostPasswordToken;
import hu.sch.domain.user.User;
import hu.sch.domain.user.UserStatus;
import static hu.sch.ejb.MailManagerBean.getMailString;
import hu.sch.services.AccountManager;
import hu.sch.services.Roles;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.exceptions.DuplicatedUserException;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.services.exceptions.PekErrorCode;
import hu.sch.util.hash.Hashing;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author balo
 */
@Stateless
public class AccountManagerBean implements AccountManager {

    private static Logger logger = LoggerFactory.getLogger(AccountManagerBean.class);
    //
    private static final int PASSWORD_SALT_LENGTH = 8;
    private static final long LOST_PW_TOKEN_VALID_MS = 24 * 60 * 60 * 1000; //24 hours in ms
    //
    @PersistenceContext
    private EntityManager em;
    //
    @EJB(name = "UserManagerBean")
    private UserManagerLocal userManager;
    @EJB(name = "SystemManagerBean")
    private SystemManagerLocal systemManager;
    @EJB
    private MailManagerBean mailManager;
    @Resource
    private SessionContext sessionContext;

    public AccountManagerBean() {
    }

    public AccountManagerBean(final EntityManager em) {
        this.em = em;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createUser(User user, String password) throws PekEJBException {
        final byte[] salt = generateSalt();
        final String passwordDigest = hashPassword(password, salt);

        final boolean isAdmin = sessionContext.isCallerInRole(Roles.ADMIN);

        if (!isAdmin) {
            user.setSalt(Base64.encodeBase64String(salt));
            user.setPasswordDigest(passwordDigest);
        }

        user.setSvieMembershipType(SvieMembershipType.NEMTAG);
        user.setSvieStatus(SvieStatus.NEMTAG);
        user.setGender(Gender.NOTSPECIFIED);
        user.setConfirmationCode(generateConfirmationCode());
        sendConfirmationEmail(user, isAdmin);

        em.persist(user);
    }

    /**
     * Generates and sets a random confirmation code for the user.
     */
    private String generateConfirmationCode() {
        final Random rnd = new SecureRandom();
        final byte[] bytes = new byte[48];
        String confirm = null;

        final TypedQuery<Long> q = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.confirmationCode = :confirm", Long.class);

        // check for uniqueness!
        do {
            rnd.nextBytes(bytes);
            confirm = Base64.encodeBase64URLSafeString(bytes);
            q.setParameter("confirm", confirm);
        } while (!q.getSingleResult().equals(0L));

        // 48 byte of randomness encoded into 64 characters
        return confirm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void confirm(final User user, final String password) throws PekEJBException {
        if (password != null) {
            byte[] salt = generateSalt();
            String passwordDigest = hashPassword(password, salt);

            user.setSalt(Base64.encodeBase64String(salt));
            user.setPasswordDigest(passwordDigest);
        }

        user.setConfirmationCode(null);
        user.setUserStatus(UserStatus.ACTIVE);

        em.merge(user);
    }

    /**
     * Sends an email to the user with the confirmation code.
     *
     * @param user
     * @param isCreatedByAdmin the email contains different texts depends from
     * this (selfreg vs. admin reg)
     * @return
     */
    private boolean sendConfirmationEmail(User user, boolean isCreatedByAdmin) {
        String subject, body;

        subject = getMailString(MailManagerBean.MAIL_CONFIRMATION_SUBJECT);

        if (isCreatedByAdmin) {
            body = String.format(
                    getMailString(MailManagerBean.MAIL_CONFIRMATION_ADMIN_BODY),
                    user.getFullName(),
                    generateConfirmationLink(user));
        } else {
            body = String.format(
                    getMailString(MailManagerBean.MAIL_CONFIRMATION_BODY),
                    user.getFullName(),
                    generateConfirmationLink(user));
        }

        return mailManager.sendEmail(user.getEmailAddress(), subject, body);
    }

    private String generateConfirmationLink(final User user) {
        return String.format("https://%s/profile/confirm/code/%s",
                Configuration.getProfileDomain(),
                user.getConfirmationCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changePassword(String screenName, String oldPwd, String newPwd) throws PekEJBException {
        User user = userManager.findUserByScreenName(screenName);
        byte[] salt = Base64.decodeBase64(user.getSalt());
        String passwordHash = hashPassword(oldPwd, salt);

        if (!passwordHash.equals(user.getPasswordDigest())) {
            logger.info("Password change requested with invalid password for user {}", user.getId());
            throw new PekEJBException(PekErrorCode.USER_PASSWORD_INVALID);
        }

        user.setPasswordDigest(hashPassword(newPwd, salt));
        em.merge(user);
    }

    private String hashPassword(String password, byte[] salt) throws PekEJBException {
        byte[] passwordBytes;
        try {
            passwordBytes = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.error("UTF-8 is not supported.", ex);
            throw new PekEJBException(PekErrorCode.SYSTEM_ENCODING_NOTSUPPORTED);
        }

        byte[] hashInput = new byte[passwordBytes.length + salt.length];
        System.arraycopy(passwordBytes, 0, hashInput, 0, passwordBytes.length);
        System.arraycopy(salt, 0, hashInput, passwordBytes.length, salt.length);

        return Hashing.sha1(hashInput).toBase64();
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[PASSWORD_SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        return salt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendUserNameReminder(final String email) throws PekEJBException {

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email argument can't be null when sending user name reminder");
        }

        try {
            final User result = userManager.findUserByEmail(email);

            if (result == null) {
                throw new PekEJBException(PekErrorCode.USER_NOTFOUND);
            } else {
                final String subject = MailManagerBean.getMailString(MailManagerBean.MAIL_USERNAME_REMINDER_SUBJECT);

                final String messageBody;
                if (systemManager.getNewbieTime()) {
                    messageBody = String.format(
                            MailManagerBean.getMailString(MailManagerBean.MAIL_USERNAME_REMINDER_BODY_NEWBIE),
                            result.getFirstName(), result.getScreenName());
                } else {
                    messageBody = String.format(
                            MailManagerBean.getMailString(MailManagerBean.MAIL_USERNAME_REMINDER_BODY),
                            result.getFirstName(), result.getScreenName());
                }

                return mailManager.sendEmail(email, subject, messageBody);
            }
        } catch (DuplicatedUserException ex) {
            logger.error("sendUserNameReminder: Duplicated user with email={}", email);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendLostPasswordChangeLink(final String email) throws PekEJBException {

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email argument can't be null when sending password change link");
        }

        try {
            final User user = userManager.findUserByEmail(email);

            if (user == null) {
                throw new PekEJBException(PekErrorCode.USER_NOTFOUND);
            }

            final String subject = MailManagerBean.getMailString(MailManagerBean.MAIL_LOST_PASSWORD_SUBJECT);

            final String body;
            if (systemManager.getNewbieTime()) {
                body = MailManagerBean.getMailString(MailManagerBean.MAIL_LOST_PASSWORD_BODY_NEWBIE);
            } else {
                body = MailManagerBean.getMailString(MailManagerBean.MAIL_LOST_PASSWORD_BODY);
            }

            String name = user.getFirstName();
            if (systemManager.getNewbieTime()) {
                name = user.getFullName();
            }

            logger.debug("sendLostPasswordChangeLink, user found={}", user.toString());

            final LostPasswordToken token = getTokenByUser(user);

            final String message = String.format(body,
                    name, user.getScreenName(), generateLostPasswordLink(token));

            return mailManager.sendEmail(email, subject, message);
        } catch (DuplicatedUserException ex) {
            logger.error("sendLostPasswordChangeLink: Duplicated user with email={}", email);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceLostPassword(final String tokenKey, final String password)
            throws PekEJBException {

        //checks the token again (validity, expiry, etc)
        final User user = getUserByLostPasswordToken(tokenKey);

        logger.info("Replace lost password for user={}", user.getScreenName());

        byte[] salt = generateSalt();
        String passwordDigest = hashPassword(password, salt);

        user.setSalt(Base64.encodeBase64String(salt));
        user.setPasswordDigest(passwordDigest);

        //removes the used token
        em.remove(em.find(LostPasswordToken.class, user.getId()));

        em.merge(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByLostPasswordToken(final String tokenKey)
            throws PekEJBException {

        final TypedQuery<LostPasswordToken> q
                = em.createNamedQuery(LostPasswordToken.getByToken, LostPasswordToken.class);
        q.setParameter("token", tokenKey);

        try {
            final LostPasswordToken token = q.getSingleResult();

            final long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis > token.getCreated().getTime() + LOST_PW_TOKEN_VALID_MS) {
                logger.info("Somebody tried to use an expired token={}", tokenKey);
                throw new PekEJBException(PekErrorCode.VALIDATION_TOKEN_EXPIRED);
            }

            return token.getSubjectUser();

        } catch (NoResultException | NonUniqueResultException ex) {
            logger.info("Somebody tried to use an invalid token={}", tokenKey);
            throw new PekEJBException(PekErrorCode.VALIDATION_TOKEN_NOTFOUND);
        } catch (PersistenceException ex) {
            logger.error("Unexpected exception while checking lost password token.", ex);
            throw new PekEJBException(PekErrorCode.UNKNOWN);
        }
    }

    private String generateLostPasswordLink(final LostPasswordToken token) {
        return String.format("https://%s/profile/confirm/pwcode/%s",
                Configuration.getProfileDomain(),
                token.getToken());
    }

    private LostPasswordToken getTokenByUser(final User user) {
        //remove existing token if it exists because LostPasswordToken is immutable
        final LostPasswordToken existingToken = em.find(LostPasswordToken.class, user.getId());
        if (existingToken != null) {
            em.remove(existingToken);
        }

        //create new token
        final Random rnd = new SecureRandom();
        final byte[] bytes = new byte[48];
        rnd.nextBytes(bytes);

        final String newTokenKey = Base64.encodeBase64URLSafeString(bytes);

        logger.debug("create new lostpw token with code={}, length={}",
                newTokenKey, newTokenKey.length());

        final LostPasswordToken newToken = new LostPasswordToken(user, newTokenKey, new Date());
        em.persist(newToken);
        em.flush();

        return newToken;
    }
}
