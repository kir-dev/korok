package hu.sch.ejb.config;

import hu.sch.services.config.ImageUploadConfig;
import hu.sch.services.config.Configuration;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of Configuration internface.
 *
 * This is the one which is used throughout the application.
 *
 * @author  aldaris
 * @author  tomi
 */
@ApplicationScoped
public class ConfigurationImpl implements Configuration {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationImpl.class);
    private static final String PROPERTY_NAME = "application.resource.dir";
    private static final String TIMES_FONT_FILE = "times.font.file";
    private static final String APPLICATION_FOLDER = "korok";
    private static final String CONFIG_FILE = "config.properties";
    private static final String IMAGE_UPLOAD_PATH = "image.upload.path";
    private static final String IMAGE_MAX_SIZE = "image.upload.max";
    private static final String DOMAIN_PROFILE = "domain.profile";
    private static final String DOMAIN_KOROK = "domain.korok";
    private static final String SUPPORT_BASE_URL = "support.baseUrl";
    private static final String SUPPORT_DEFAULT_ID = "support.defaultId";

    private final Properties properties = new Properties();
    private final String baseDir;
    private Environment environment = null;

    public ConfigurationImpl() {
        String dir = System.getProperty(PROPERTY_NAME);

        if (dir == null) {
            throw new IllegalArgumentException(
                    "System property '" + PROPERTY_NAME + "' isn't set! Can't initialize application!");
        }
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        baseDir = dir;

        try(FileInputStream fis =
                new FileInputStream(new File(baseDir + APPLICATION_FOLDER + "/" + CONFIG_FILE))) {

            properties.load(fis);
            logger.debug(properties.toString());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error while loading properties file!", ex);
        }
    }

    @Override
    public Environment getEnvironment() {
        if (environment == null) {
            String env = properties.getProperty("wicket.configuration", "DEVELOPMENT");
            try {
                environment = Environment.valueOf(env);
            } catch (IllegalArgumentException ex) {
                System.err.println("Illegal 'wicket.configuration' in the config.properties. Fallbacking to DEVELOPMENT.");
                environment = Environment.DEVELOPMENT;
            }
            logger.warn("The application is running in {} mode!", environment.toString());
        }
        return environment;
    }

    @Override
    public String getDevEmail() {
        return properties.getProperty("devMail");
    }

    @Override
    public String getProfileDomain() {
        return properties.getProperty(DOMAIN_PROFILE);
    }

    @Override
    public String getKorokDomain() {
        return properties.getProperty(DOMAIN_KOROK);
    }

    @Override
    public String getSupportBaseUrl() {
        return properties.getProperty(SUPPORT_BASE_URL);
    }

    @Override
    public int getSupportDefaultId() {
        return Integer.parseInt(properties.getProperty(SUPPORT_DEFAULT_ID));
    }

    @Override
    public String getFontPath() {
        return baseDir + APPLICATION_FOLDER + "/" + properties.getProperty(TIMES_FONT_FILE);
    }

    @Override
    public ImageUploadConfig getImageUploadConfig() {
        String path = properties.getProperty(IMAGE_UPLOAD_PATH);
        int size = Integer.parseInt(properties.getProperty(IMAGE_MAX_SIZE, "400"));

        return new ImageUploadConfig(path, size);
    }
}