package hu.sch.test.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 *
 * @author aldaris
 */
class TestConfig {

    public static final String GF_ROOT = "test.gf.install.root";
    private static final Properties configuration = new Properties();

    static {
        String configFile = System.getProperty("korok.test.config");
        if (configFile == null) {
            System.out.println("Hiba az inicailázálás során, nincsen konfiguráció megadva!");
        }
        try {
            configuration.load(new InputStreamReader(new FileInputStream(configFile)));
        } catch (IOException ioe) {
            throw new RuntimeException("Error, couldn't load configuration!");
        }
    }

    private TestConfig() {
    }

    public static String getProperty(String key) {
        return configuration.getProperty(key);
    }
}
