package hu.sch.ejb;

import hu.sch.domain.enums.ValuationStatus;
import hu.sch.domain.user.User;
import hu.sch.domain.*;
import hu.sch.domain.config.Configuration;
import hu.sch.domain.user.ProfileImage;
import hu.sch.domain.user.UserAttribute;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.ejb.image.ImageProcessor;
import hu.sch.ejb.image.ImageSaver;
import hu.sch.services.*;
import hu.sch.services.exceptions.DuplicatedUserException;
import hu.sch.services.exceptions.NotImplementedException;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.services.exceptions.PekErrorCode;
import hu.sch.util.hash.Hashing;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.*;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hege
 * @author messo
 * @author tomi
 */
@Stateless
public class UserManagerBean implements UserManagerLocal {

    private static int PASSWORD_SALT_LENGTH = 8;
    private static Logger logger = LoggerFactory.getLogger(UserManagerBean.class);
    @PersistenceContext
    EntityManager em;
    @EJB(name = "LogManagerBean")
    LogManagerLocal logManager;
    @EJB
    MailManagerBean mailManager;
    @EJB(name = "PostManagerBean")
    PostManagerLocal postManager;
    @EJB(name = "SystemManagerBean")
    private SystemManagerLocal systemManager;
    @Resource
    private SessionContext sessionContext;

    public UserManagerBean() {
    }

    // for testing
    public UserManagerBean(EntityManager em) {
        this.em = em;
    }

    @Override
    public User findUserById(Long userId) {
        return findUserById(userId, false);
    }

    @Override
    public User findUserById(Long userId, boolean includeMemberships) {
        if (userId.equals(0L)) {
            // ha nincs használható userId, akkor ne menjünk el a DB-hez.
            return null;
        }

        if (!includeMemberships) {
            return em.find(User.class, userId);
        }

        TypedQuery<User> q = em.createNamedQuery(User.findWithMemberships, User.class);
        q.setParameter("id", userId);

        try {
            return q.getSingleResult();
        } catch (Exception ex) {
            logger.warn("Can't find user with memberships for this id: " + userId);
            return null;
        }
    }

    @Override
    public User findUserByScreenName(String screenName) {
        try {
            return em.createNamedQuery(User.findByScreenName, User.class)
                    .setParameter("screenName", screenName)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.info("User with {} screenname was not found.", screenName);
        }

        return null;
    }

    @Override
    public User findUserByNeptun(final String neptun) {
        return findUserByNeptun(neptun, false);
    }

    @Override
    public User findUserByNeptun(final String neptun, boolean includeMemberships) {
        try {
            final User user = em.createNamedQuery(User.findUserByNeptunCode, User.class)
                    .setParameter("neptun", neptun)
                    .getSingleResult();

            if (includeMemberships) {
                Hibernate.initialize(user.getMemberships());
            }

            return user;
        } catch (NoResultException ex) {
            logger.info("User not found with {} neptun.", neptun);
        }
        return null;
    }

    @Override
    public User findUserByEmail(final String email) throws DuplicatedUserException {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.emailAddress = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.info("Could not find user with email: {}", email);
        } catch (NonUniqueResultException ex) {
            throw new DuplicatedUserException(String.format("Duplicate user with %s email", email), ex);
        }

        return null;
    }

    @Override
    public List<EntrantRequest> getEntrantRequestsForUser(User felhasznalo) {
        Query q = em.createQuery("SELECT e FROM EntrantRequest e "
                + "WHERE e.user=:user "
                + "ORDER BY e.valuation.semester DESC, e.entrantType ASC");
        q.setParameter("user", felhasznalo);

        return q.getResultList();
    }

    @Override
    public List<PointRequest> getPointRequestsForUser(User felhasznalo) {
        Query q = em.createQuery("SELECT p FROM PointRequest p "
                + "WHERE p.user=:user "
                + "ORDER BY p.valuation.semester DESC, p.valuation.group.name ASC");
        q.setParameter("user", felhasznalo);

        return q.getResultList();
    }

    @Override
    public void createUser(User user, String password) throws PekEJBException {
        byte[] salt = generateSalt();
        String passwordDigest = hashPassword(password, salt);

        boolean isAdmin = sessionContext.isCallerInRole(Roles.ADMIN);

        if (!isAdmin) {
            user.setSalt(Base64.encodeBase64String(salt));
            user.setPasswordDigest(passwordDigest);
        }

        user.setConfirmationCode(generateConfirmationCode());
        sendConfirmationEmail(user, isAdmin);

        em.persist(user);
    }

    @Override
    public void updateUser(User user) throws PekEJBException {
        updateUser(user, null);
    }

    @Override
    public void updateUser(User user, ProfileImage image) throws PekEJBException {
        // process image
        if (image != null) {
            ImageProcessor proc = new ImageProcessor(user, image, Configuration.getImageUploadConfig());
            String imagePath = proc.process();
            user.setPhotoPath(imagePath);
        }

        // save user
        em.merge(user);
    }

    @Override
    public List<User> findUsersByName(String name) {
        Query q = em.createQuery("SELECT u FROM User u WHERE UPPER(concat(concat(u.lastName, ' '), "
                + "u.firstName)) LIKE UPPER(:name) "
                + "ORDER BY u.lastName ASC, u.firstName ASC");
        q.setParameter("name", "%" + name + "%");

        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Semester> getAllValuatedSemesterForUser(User user) {
        return em.createNamedQuery(User.getAllValuatedSemesterForUser).setParameter("user", user).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSemesterPointForUser(User user, Semester semester) {
        // Beszerezzük a pontokat
        List<PointRequest> pontigenyek = getPointRequestsForUser(user);

        // Ebbe a Map-be lesz tárolva, hogy melyik körtől hány pontot kapott
        Map<Group, Integer> points = new HashMap<Group, Integer>();
        for (PointRequest pr : pontigenyek) {
            Valuation v = pr.getValuation();
            // Csak ha az adott értékelés a legfrisebb verzió ÉS a pont elfogadott
            if (!v.isObsolete() && v.getPointStatus().equals(ValuationStatus.ELFOGADVA)) {
                // Csak akkor, ha a vizsgált vagy az előző félévre vonatkozik
                if (v.getSemester().equals(semester) || v.getSemester().equals(semester.getPrevious())) {
                    if (points.containsKey(v.getGroup()) == false) {
                        points.put(v.getGroup(), 0);
                    }
                    points.put(v.getGroup(), points.get(v.getGroup()) + pr.getPoint());
                }
            }
        }
        // Az összeg
        int sum = 0;
        // Négyzetösszeget számolunk
        for (Integer pointFromGroup : points.values()) {
            sum += pointFromGroup * pointFromGroup;
        }

        return (int) Math.min(Math.sqrt(sum), 100);
    }

    @Override
    public SpotImage getSpotImage(User user) {
        TypedQuery<SpotImage> q = em.createNamedQuery(SpotImage.findByNeptun, SpotImage.class);
        q.setParameter("neptunCode", user.getNeptunCode());
        try {
            return q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public boolean acceptRecommendedPhoto(String screenName) {
        User user = findUserByScreenName(screenName);

        TypedQuery<SpotImage> q = em.createNamedQuery(SpotImage.findByNeptun, SpotImage.class);
        q.setParameter("neptunCode", user.getNeptunCode());
        try {
            SpotImage si = q.getSingleResult();

            ImageSaver imageSaver = new ImageSaver(user);
            String imgPath = imageSaver.copy(si.getImageFullPath()).getRelativePath();
            user.setPhotoPath(imgPath);

            removeSpotImage(si);

            return true;
        } catch (NoResultException ex) {
            logger.error("No user with {} screen name.", screenName);
        } catch (PekEJBException ex) {
            logger.error("Could not copy image. Error code: {}", ex.getErrorCode());
        }
        return false;
    }

    @Override
    public void declineRecommendedPhoto(User user) {
        TypedQuery<SpotImage> q = em.createNamedQuery(SpotImage.findByNeptun, SpotImage.class);
        q.setParameter("neptunCode", user.getNeptunCode());
        SpotImage img = q.getSingleResult();

        removeSpotImage(img);
    }

    @Override
    public void invertAttributeVisibility(User user, UserAttributeName attr) {
        User managedUser = em.merge(user);

        boolean done = false;
        for (UserAttribute a : managedUser.getPrivateAttributes()) {
            if (a.getAttributeName() == attr) {
                a.setVisible(!a.isVisible());
                done = true;
                break;
            }
        }

        // user's attribute list does not contian the given attribute
        // which means it is NOT visible, so we'll make it visible
        if (!done) {
            managedUser.getPrivateAttributes().add(new UserAttribute(attr, true));
        }
    }

    @Override
    public void changePassword(String screenName, String oldPwd, String newPwd) throws PekEJBException {
        User user = findUserByScreenName(screenName);
        byte[] salt = Base64.decodeBase64(user.getSalt());
        String passwordHash = hashPassword(oldPwd, salt);

        if (!passwordHash.equals(user.getPasswordDigest())) {
            logger.info("Password change requested with invalid password for user {}", user.getId());
            throw new PekEJBException(PekErrorCode.USER_PASSWORD_INVALID);
        }

        user.setPasswordDigest(hashPassword(newPwd, salt));
        em.merge(user);
    }

    /**
     * Deletes the spot image from the file system and db.
     *
     * @param img the image to delete
     */
    private void removeSpotImage(SpotImage img) {
        try {
            Files.deleteIfExists(Paths.get(img.getImageFullPath()));
        } catch (IOException ex) {
            logger.warn("IO Error while deleting file.", ex);
            // nothing to do.
        }

        // this updates the user record via a trigger.
        // usr_show_recommended will be false after the update.
        em.remove(img);
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
            final User result = findUserByEmail(email);

            if (result == null) {
                throw new PekEJBException(PekErrorCode.USER_NOTFOUND);
            } else {
                final String subject =
                        MailManagerBean.getMailString(MailManagerBean.MAIL_USERNAME_REMINDER_SUBJECT);

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
     * Generates and sets a random confirmation code for the user.
     */
    private String generateConfirmationCode() {
        Random rnd = new SecureRandom();
        byte[] bytes = new byte[48];
        String confirm = null;

        TypedQuery<Long> q = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.confirmationCode = :confirm", Long.class);

        // check for uniqueness!
        do {
            rnd.nextBytes(bytes);
            confirm = Base64.encodeBase64URLSafeString(bytes);
            q.setParameter("confirm", confirm);
        } while (!q.getSingleResult().equals(0L));

        // 48 byte of randomness encoded into 64 characters
        return confirm;
    }

    private boolean sendConfirmationEmail(User user, boolean isCreatedByAdmin) {
        String subject, body;

        subject = MailManagerBean.getMailString(MailManagerBean.MAIL_CONFIRMATION_SUBJECT);

        if (isCreatedByAdmin) {
            body = String.format(
                    MailManagerBean.getMailString(MailManagerBean.MAIL_CONFIRMATION_ADMIN_BODY),
                    user.getFullName(),
                    generateConfirmationLink(user));
        } else {
            body = String.format(
                    MailManagerBean.getMailString(MailManagerBean.MAIL_CONFIRMATION_BODY),
                    user.getFullName(),
                    generateConfirmationLink(user));
        }

        return mailManager.sendEmail(user.getEmailAddress(), subject, body);
    }

    private String generateConfirmationLink(User user) {
        String domain = Configuration.getProfileDomain();
        return String.format("https://%s/profile/confirm/code/%s", domain, user.getConfirmationCode());
    }
    // TODO: password policy
}
