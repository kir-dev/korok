package hu.sch.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of Configuration internface.
 *
 * This is the one which is used throughout the application.
 *
 * @author aldaris
 * @author tomi
 */
@ApplicationScoped
class ConfigurationImpl implements Configuration {

    private static final String DEFAULT_AVATAR_SIZE = "400";
    private static final String DEFAULT_THUMBNAIL_SIZE = "150";

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationImpl.class);
    private static final String PROPERTY_NAME = "application.resource.dir";
    private static final String ENVIRONMENT = "environment";
    private static final String APPLICATION_FOLDER = "korok";
    private static final String CONFIG_FILE = "config.properties";
    private static final String AVATAR_UPLOAD_PATH = "image.upload.path";
    private static final String AVATAR_MAX_SIZE = "image.upload.max";
    private static final String THUMBNAIL_SIZE = "image.upload.thumbnail";
    private static final String DOMAIN = "domain";
    private static final String INTERNAL_API_SECRET = "api.secret";
    private final Properties properties = new Properties();
    private String baseDir;
    private Environment environment = null;

    @PostConstruct
    public void initialize() {
        baseDir = getBaseDir();
        loadPropertiesFromFile();
        loadEnvironment();

        verify();
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public String getDevEmail() {
        return properties.getProperty("devMail");
    }

    @Override
    public String getDomain() {
        return properties.getProperty(DOMAIN);
    }

    @Override
    public ImageUploadConfig getImageUploadConfig() {
        String path = properties.getProperty(AVATAR_UPLOAD_PATH);
        int size = Integer.parseInt(properties.getProperty(AVATAR_MAX_SIZE, DEFAULT_AVATAR_SIZE));
        int thumbnail = Integer.parseInt(properties.getProperty(THUMBNAIL_SIZE, DEFAULT_THUMBNAIL_SIZE));

        return new ImageUploadConfig(path, size, thumbnail);
    }

    @Override
    public String getInternalApiSecret() {
        return properties.getProperty(INTERNAL_API_SECRET);
    }

    private void loadEnvironment() {
        // try environment system property first, then the properties file
        String env = System.getProperty(ENVIRONMENT);
        if (StringUtils.isBlank(env)) {
            env = properties.getProperty(ENVIRONMENT, "DEVELOPMENT");
        }
        try {
            environment = Environment.valueOf(env);
        } catch (IllegalArgumentException ex) {
            System.err.println("Illegal environment value. Fallbacking to DEVELOPMENT.");
            environment = Environment.DEVELOPMENT;
        }
        logger.warn("The application is running in {} mode!", environment.toString());
    }

    private String getBaseDir() throws IllegalArgumentException {
        String dir = System.getProperty(PROPERTY_NAME);
        if (dir == null) {
            throw new IllegalArgumentException(
                    "System property '" + PROPERTY_NAME + "' isn't set! Can't initialize application!");
        }
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        return dir;
    }

    private void loadPropertiesFromFile() throws IllegalArgumentException {
        try (FileInputStream fis =
                new FileInputStream(new File(baseDir + APPLICATION_FOLDER + "/" + CONFIG_FILE))) {

            properties.load(fis);
            logger.debug(properties.toString());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error while loading properties file!", ex);
        }
    }

    // verifies the configuration that it only contains legal values
    private void verify() {
        // TODO: github/#109
    }
}
