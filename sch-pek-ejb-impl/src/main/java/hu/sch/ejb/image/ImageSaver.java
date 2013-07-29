package hu.sch.ejb.image;

import hu.sch.domain.config.Configuration;
import hu.sch.domain.config.ImageUploadConfig;
import hu.sch.domain.user.User;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.services.exceptions.PekErrorCode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Saves an image to the right location in the file system.
 *
 * If necessary it creates the folders too.
 *
 * @author tomi
 */
public final class ImageSaver {

    private static final Logger logger = LoggerFactory.getLogger(ImageSaver.class);
    private static final ImageUploadConfig config = Configuration.getImageUploadConfig();
    private final User user;
    private Path lastPath = null;

    public ImageSaver(User user) {
        this.user = user;
    }

    public ImageSaver copy(String sourcePath) throws PekEJBException {
        File img = new File(sourcePath);
        byte[] bytes;

        try {
            bytes = Files.readAllBytes(img.toPath());
        } catch (IOException ex) {
            logger.warn(String.format("Could not read file %s", sourcePath), ex);
            bytes = new byte[0];
        }
        return save(img.getName(), bytes);
    }


    public ImageSaver save(String filename, byte[] data) throws PekEJBException {
        try {
            lastPath = Files.write(buildImagePath(filename), data);
            return this;
        } catch (IOException ex) {
            final String msg = "Could not save image to disk.";
            logger.error(msg, ex);
            throw new PekEJBException(PekErrorCode.FILE_CREATE_FAILED, msg, ex);
        }
    }

    /**
     * Gets the last processed images' path relative to the uploads base path.
     * 
     * @return
     */
    public String getRelativePath() {
        if (lastPath == null) {
            throw new IllegalStateException("There is no last saved file path. Call save() first.");
        }
        Path base = Paths.get(config.getBasePath());
        Path relative = base.relativize(lastPath);
        return relative.toString();
    }

    /**
     * Builds the path of the image. Also create the folder if they don't exist.
     *
     * Eg. /path/to/upload/base/u/username/image-hash.ext
     *
     * @param user
     * @param filename
     * @return
     */
    private Path buildImagePath(String filename) {
        Path dir = Paths.get(config.getBasePath(), user.getScreenName().substring(0, 1), user.getScreenName());
        dir.toFile().mkdirs();

        return Paths.get(dir.toString(), filename);
    }
}
