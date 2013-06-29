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
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(configFile));
            configuration.load(isr);
        } catch (IOException ioe) {
            throw new RuntimeException("Error, couldn't load configuration!");
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    private TestConfig() {
    }

    public static String getProperty(String key) {
        return configuration.getProperty(key);
    }
}
