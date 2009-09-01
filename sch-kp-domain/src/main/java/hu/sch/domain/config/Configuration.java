/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 *
 * @author aldaris
 */
public class Configuration {

    private static final String SPRINGLDAP_FILE = "springldap.file";
    private static final String TIMES_FONT_FILE = "times.font.file";
    private static Properties properties = new Properties();
    private static String baseDir;

    private Configuration() {
    }

    public static void init() {
        baseDir = System.getProperty("korok.resource.dir");
        if (baseDir == null) {
            throw new IllegalArgumentException(
                    "System property 'korok.resource.dir' isn't setted! Can't initialize application!");
        }
        try {
            FileInputStream fis = new FileInputStream(new File(baseDir + "config.properties"));
            properties.load(fis);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error while loading properties file!", ex);
        }
    }

    public static String getSpringLdapPath() {
        return baseDir + properties.getProperty(SPRINGLDAP_FILE);
    }

    public static String getFontPath() {
        return baseDir + properties.getProperty(TIMES_FONT_FILE);
    }
}
