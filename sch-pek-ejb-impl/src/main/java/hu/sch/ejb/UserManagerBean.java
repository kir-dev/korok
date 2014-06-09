package hu.sch.ejb;

import hu.sch.domain.user.User;
import hu.sch.domain.*;
import hu.sch.util.config.Configuration;
import hu.sch.domain.user.ProfileImage;
import hu.sch.ejb.image.ImageProcessor;
import hu.sch.ejb.image.ImageRemoverService;
import hu.sch.ejb.image.ImageSaver;
import hu.sch.services.*;
import hu.sch.services.exceptions.DuplicatedUserException;
import hu.sch.util.exceptions.PekException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hege
 * @author messo
 * @author tomi
 * @author ksisu
 */
@Stateless
public class UserManagerBean implements UserManagerLocal {

    private static Logger logger = LoggerFactory.getLogger(UserManagerBean.class);
    @PersistenceContext
    private EntityManager em;
    private SystemManagerLocal systemManager;
    private Configuration config;

    public UserManagerBean() {
    }

    public UserManagerBean(EntityManager em) {
        this.em = em;
    }

    @Inject
    public void setSystemManager(SystemManagerLocal systemManager) {
        this.systemManager = systemManager;
    }

    @Inject
    public void setConfig(Configuration config) {
        this.config = config;
    }

    @Override
    public User findUserById(Long userId) {
        if (userId == null || userId.equals(0L)) {
            // TODO: github/#41: introduce exception instead of silent null
            return null;
        }
        return em.find(User.class, userId);
    }

    @Override
    public User findUserByIdWithMemberships(Long userId) {
        if (userId == null || userId.equals(0L)) {
            // ha nincs használható userId, akkor ne menjünk el a DB-hez.
            return null;
        }

        TypedQuery<User> q = em.createNamedQuery(User.findWithMemberships, User.class);
        q.setParameter("id", userId);

        try {
            return q.getSingleResult();
        } catch (Exception ex) {
            logger.warn("Can't find user with memberships for this id: " + userId);
            // TODO: github/#41: rethrow exception with wrapper
            return null;
        }
    }

    @Override
    public User findUserByIdWithIMAccounts(Long userId) {
        try {
            return em.createNamedQuery(User.findWithIMAccounts, User.class)
                    .setParameter("id", userId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            logger.warn("Could not find user with id {}", userId);
        }

        return null;
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
    public User findUserByConfirmationCode(final String code) {
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.confirmationCode = :code", User.class);
        q.setParameter("code", code);

        logger.debug("Find user with confirmation code=" + code);

        User result = null;
        try {
            result = q.getSingleResult();
        } catch (NoResultException ex) {
            logger.info("No user was found with {} confirmation code.", code);
        } catch (NonUniqueResultException ex) {
            logger.error("Multiple users were found for the same {} confirmation code.", code);
        }

        return result;
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
    public void updateUser(User user) throws PekException {
        updateUser(user, null);
    }

    @Override
    public User updateUser(User user, ProfileImage image) throws PekException {
        // process image
        if (image != null) {
            ImageProcessor proc = new ImageProcessor(user, image, config.getImageUploadConfig());
            String imagePath = proc.process();
            user.setPhotoPath(imagePath);
        }

        // save user
        return em.merge(user);
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
    public List<PointHistory> getCommunityPointsForUser(User user) {
        TypedQuery<PointHistory> q = em.createNamedQuery(PointHistory.findByUser, PointHistory.class);
        q.setParameter("user", user);

        return q.getResultList();
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

            ImageSaver imageSaver = new ImageSaver(user, config.getImageUploadConfig());
            String imgPath = imageSaver.copy(si.getImageFullPath(config.getImageUploadConfig().getBasePath())).getRelativePath();
            user.setPhotoPath(imgPath);

            removeSpotImage(user, si);

            return true;
        } catch (NoResultException ex) {
            logger.error("No user with {} screen name.", screenName);
        } catch (PekException ex) {
            logger.error("Could not copy image. Error code: {}", ex.getErrorCode());
        }
        return false;
    }

    @Override
    public void declineRecommendedPhoto(User user) {
        TypedQuery<SpotImage> q = em.createNamedQuery(SpotImage.findByNeptun, SpotImage.class);
        q.setParameter("neptunCode", user.getNeptunCode());
        SpotImage img = q.getSingleResult();

        removeSpotImage(user, img);
    }

    /**
     * Deletes the spot image from the file system and db.
     *
     * @param img the image to delete
     */
    private void removeSpotImage(User user, SpotImage img) {
        try {
            Files.deleteIfExists(Paths.get(img.getImageFullPath(config.getImageUploadConfig().getBasePath())));
        } catch (IOException ex) {
            logger.warn("IO Error while deleting file.", ex);
            // nothing to do.
        }

        em.remove(img);
        // update user to not show recommended photo
        user.setShowRecommendedPhoto(false);
    }

    @Override
    public void removeProfileImage(User user) throws PekException {
        new ImageRemoverService(config).removeProfileImage(user);
        user.setPhotoPath(null);
        updateUser(user);
    }
}
