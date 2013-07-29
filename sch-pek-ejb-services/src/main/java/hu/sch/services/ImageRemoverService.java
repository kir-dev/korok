package hu.sch.services;

import hu.sch.domain.user.User;
import java.io.File;
import java.util.Objects;

/**
 *
 * @author tomi
 */
public class ImageRemoverService {

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
            return new File(user.getPhotoFullPath()).delete();
        }

        return false;
    }
}
