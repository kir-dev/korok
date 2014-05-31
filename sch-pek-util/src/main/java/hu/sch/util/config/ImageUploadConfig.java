package hu.sch.util.config;

/**
 *
 * @author tomi
 */
public class ImageUploadConfig {

    private final String basePath;
    private final int maxSize;
    private final int thumbnailSize;

    public ImageUploadConfig(String basePath, int maxSize, int thumbnailSize) {
        this.basePath = basePath;
        this.maxSize = maxSize;
        this.thumbnailSize = thumbnailSize;
    }

    /**
     * Gets the base path for the uploaded images.
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * Gets the maximum size of the image in pixels.
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Gets the size of the thumbnail.
     */
    public int getThumbnailSize() {
        return thumbnailSize;
    }
}
