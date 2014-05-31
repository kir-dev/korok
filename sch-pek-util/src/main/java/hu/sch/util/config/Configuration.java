package hu.sch.util.config;

/**
 *
 * @author tomi
 */
public interface Configuration {

    String getDevEmail();

    Environment getEnvironment();

    ImageUploadConfig getImageUploadConfig();

    // TODO: unify this with profile domian (github/#106)
    String getKorokDomain();

    String getProfileDomain();

    String getInternalApiSecret();
}
