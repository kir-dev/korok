package hu.sch.domain.config;

/**
 *
 * @author tomi
 */
public class ImageUploadConfig {

    private final String basePath;
    private final int maxSize;

    public ImageUploadConfig(String basePath, int maxSize) {
        this.basePath = basePath;
        this.maxSize = maxSize;
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
}
