package hu.sch.services.config;

/**
 *
 * @author tomi
 */
public interface Configuration {

    String getDevEmail();

    Environment getEnvironment();

    ImageUploadConfig getImageUploadConfig();

    String getKorokDomain();

    String getProfileDomain();

    String getInternalApiSecret();
}
