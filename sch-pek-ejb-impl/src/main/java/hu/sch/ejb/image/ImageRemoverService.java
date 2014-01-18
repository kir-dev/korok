package hu.sch.ejb.image;

import hu.sch.domain.user.User;
import hu.sch.services.config.Configuration;
import java.io.File;
import java.util.Objects;

/**
 *
 * @author tomi
 */
public class ImageRemoverService {

    private Configuration config;

    public ImageRemoverService(Configuration config) {
        this.config = config;
    }

    /**
     * Removes the user's profile image from the storage. At the moment the
     * storage is the file system.
     *
     * @param user the user whose profile image should be remove
     * @return true if the image was deleted, false otherwise
     */
    public boolean removeProfileImage(User user) {
        user = Objects.requireNonNull(user);
        if (user.getPhotoPath() != null) {
            return new File(user.getPhotoFullPath(config.getImageUploadConfig().getBasePath())).delete();
        }

        return false;
    }
}
