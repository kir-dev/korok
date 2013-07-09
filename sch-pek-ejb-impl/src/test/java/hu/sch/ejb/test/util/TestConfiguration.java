package hu.sch.ejb.test.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tomi
 */
public class TestConfiguration {

    private static final String PERSISTENCE_CONFIG_PATH = "persistence.config";
    private static final String DB_PASSWORD = "db.password";
    private static Properties properties;

    /**
     * Gets the password for the db.
     */
    public static String getPassword() {
        return getProperties().getProperty(DB_PASSWORD);
    }

    private static Properties getProperties() {
        if (properties == null) {
            String configPath = System.getProperty(PERSISTENCE_CONFIG_PATH);
            properties = new Properties();
            try (FileInputStream fis = new FileInputStream(configPath)) {
                properties.load(fis);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                // ignored
            }
        }
        return properties;
    }
}
