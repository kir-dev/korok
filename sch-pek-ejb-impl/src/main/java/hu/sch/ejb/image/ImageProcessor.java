package hu.sch.ejb.image;

import hu.sch.util.config.ImageUploadConfig;
import hu.sch.domain.user.ProfileImage;
import hu.sch.domain.user.User;
import hu.sch.services.exceptions.PekException;
import hu.sch.services.exceptions.PekErrorCode;
import hu.sch.util.hash.Hashing;
import hu.sch.util.net.MediaType;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
public class ImageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ImageProcessor.class);
    private final User user;
    private final ImageUploadConfig config;
    private byte[] imageAsBytes = null;
    private ProfileImage image;
    private String oldImagePath = null;
    private String resultPath = null;

    public ImageProcessor(User user, ProfileImage image, ImageUploadConfig imageConfig) {
        this.user = user;
        this.image = image;
        this.config = imageConfig;

        if (user.getPhotoPath() != null) {
            oldImagePath = user.getPhotoFullPath(imageConfig.getBasePath());
        }
    }

    /**
     * Gets the last processed image as a byte array.
     *
     * @return
     */
    public byte[] getImage() {
        return imageAsBytes;
    }

    /**
     * Gets the path for the last processed image.
     *
     * @return
     */
    public String getResultPath() {
        return resultPath;
    }

    /**
     * Resizes the image to fit the max size configured in app config and saves
     * it to the disk.
     *
     * Deletes the old image if exists.
     *
     * @param img
     * @return the relative path of the image
     * @throws PekException if anything goes wrong with the image.
     */
    public String process() {
        validateImage();
        resize();
        resultPath = store();

        // if everything was OK, delete the old image
        deleteOld();
        return resultPath;
    }

    public void resize() {
        try {
            ImageResizer ir = new ImageResizer(image.getData(), config.getMaxSize());
            imageAsBytes = ir.resizeImage().getBytes();
        } catch (IOException ex) {
            final String msg = "An error occured while processing the image.";
            logger.warn(msg, ex);
            throw new PekException(PekErrorCode.FILE_OPEN_FAILED, msg, ex);
        }
    }

    public void deleteOld() {
        if (oldImagePath == null) {
            return;
        }

        new File(oldImagePath).delete();
    }

    public String store() {
        if (imageAsBytes == null) {
            throw new IllegalStateException("Image has not been resized yet.");
        }

        String newFilename = appendImageExtension(Hashing.sha1(imageAsBytes).toHex());
        return new ImageSaver(user, config).save(newFilename, imageAsBytes).getRelativePath();
    }

    private void validateImage() {
        MediaType type = MediaType.parse(image.getMimeType());

        if (!type.isAny(MediaType.IMAGE_GIF, MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)) {
            String msg = String.format("Mime-type (%s) is not supported. Supported types are: image/gif, image/jpeg, image/png", type.getContentType());
            throw new PekException(PekErrorCode.INVALID_IMAGE_MIME_TYPE, msg);
        }
    }

    private String appendImageExtension(String filename) {
        return filename + "." + ImageResizer.IMAGE_EXT;
    }
}
