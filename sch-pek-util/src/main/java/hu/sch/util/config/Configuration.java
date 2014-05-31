package hu.sch.util.config;

/**
 *
 * @author tomi
 */
public interface Configuration {

    String getDevEmail();

    Environment getEnvironment();

    ImageUploadConfig getImageUploadConfig();

    String getDomain();

    String getInternalApiSecret();
}
