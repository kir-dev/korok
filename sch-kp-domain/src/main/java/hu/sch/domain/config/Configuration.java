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

    private static final String PROPERTY_NAME = "application.resource.dir";
    private static final String SPRINGLDAP_FILE = "springldap.file";
    private static final String TIMES_FONT_FILE = "times.font.file";
    private static final String APPLICATION_FOLDER = "korok";
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = new Properties();
    private static String baseDir;

    private Configuration() {
    }

    public static void init() {
        baseDir = System.getProperty(PROPERTY_NAME);
        if (baseDir == null) {
            throw new IllegalArgumentException(
                    "System property '" + PROPERTY_NAME + "' isn't setted! Can't initialize application!");
        }
        if (!baseDir.endsWith("/")) {
            baseDir += "/";
        }
        try {
            FileInputStream fis = new FileInputStream(new File(baseDir + APPLICATION_FOLDER + "/" + CONFIG_FILE));
            properties.load(fis);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error while loading properties file!", ex);
        }
    }

    public static String getSpringLdapPath() {
        return baseDir + APPLICATION_FOLDER + "/" + properties.getProperty(SPRINGLDAP_FILE);
    }

    public static String getFontPath() {
        return baseDir + APPLICATION_FOLDER + "/" + properties.getProperty(TIMES_FONT_FILE);
    }
}
