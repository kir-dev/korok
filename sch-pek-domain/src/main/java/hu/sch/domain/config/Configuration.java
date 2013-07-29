package hu.sch.domain.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author  aldaris
 * @author  tomi
 */
public class Configuration {

    /**
     * Környezetet leíró enum a finomhangolásért
     */
    public enum Environment {

        /**
         * Fejlesztői környezet, ilyenkor a Wicket is DEV módban van
         */
        DEVELOPMENT,
        /**
         * Mielőtt kiraknánk a serverre a kész rendszert, szükség lehet arra, hogy
         * DEV módban menjen a Wicket, de az autorizáció modul már ne a DummyAuthorization
         * legyen, hanem a rendes AgentBasedAuthorization.
         */
        STAGING,
        /**
         * A kész publikus verzió mindenképp ilyen környezettel legyen deploy-olva,
         * mert ilyenkor a wicket már rendesen a DEPLOYMENT-et látja, mint
         * configurationType.
         */
        PRODUCTION,
        /**
         * A funkcionális, illetve egyéb JUnit tesztekhez használatos konfiguráció,
         * ebben az esetben a DummyAuthorization modul kerül használatra, mivel
         * nincs ebben a környezetben agent.
         */
        TESTING
    };
    private static final Logger logger = Logger.getLogger(Configuration.class.getSimpleName());
    private static final String PROPERTY_NAME = "application.resource.dir";
    private static final String TIMES_FONT_FILE = "times.font.file";
    private static final String APPLICATION_FOLDER = "korok";
    private static final String CONFIG_FILE = "config.properties";
    private static final String LDAP_HOST = "ldap.host";
    private static final String LDAP_PORT = "ldap.port";
    private static final String LDAP_USER = "ldap.user";
    private static final String LDAP_PASSWORD = "ldap.password";
    private static final String IMAGE_UPLOAD_PATH = "image.upload.path";
    private static final String IMAGE_MAX_SIZE = "image.upload.max";

    private static final Properties properties = new Properties();
    private static final String baseDir;
    private static Environment environment = null;

    static {
        String dir = System.getProperty(PROPERTY_NAME);

        if (dir == null) {
            throw new IllegalArgumentException(
                    "System property '" + PROPERTY_NAME + "' isn't setted! Can't initialize application!");
        }
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        baseDir = dir;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(baseDir + APPLICATION_FOLDER + "/" + CONFIG_FILE));
            properties.load(fis);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error while loading properties file!", ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    public static Environment getEnvironment() {
        if (environment == null) {
            String env = properties.getProperty("wicket.configuration", "DEVELOPMENT");
            try {
                environment = Environment.valueOf(env);
            } catch (IllegalArgumentException ex) {
                System.err.println("Illegal 'wicket.configuration' in the config.properties. Fallbacking to DEVELOPMENT.");
                environment = Environment.DEVELOPMENT;
            }
            logger.log(Level.WARNING, "The application is running in {0} mode!", environment.toString());
        }
        return environment;
    }

    public static String getDevEmail() {
        return properties.getProperty("devMail");
    }

    private Configuration() {
    }

    public static String getFontPath() {
        return baseDir + APPLICATION_FOLDER + "/" + properties.getProperty(TIMES_FONT_FILE);
    }

    public static LdapConfig getLdapConfig() {
        String user = properties.getProperty(LDAP_USER);
        String password = properties.getProperty(LDAP_PASSWORD);
        String host = properties.getProperty(LDAP_HOST);
        int port = Integer.parseInt(properties.getProperty(LDAP_PORT));

        return new LdapConfig(host, port, user, password);
    }

    public static ImageUploadConfig getImageUploadConfig() {
        String path = properties.getProperty(IMAGE_UPLOAD_PATH);
        int size = Integer.parseInt(properties.getProperty(IMAGE_MAX_SIZE, "400"));

        return new ImageUploadConfig(path, size);
    }
}
