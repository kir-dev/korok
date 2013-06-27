/**
 * Copyright (c) 2008-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
    private static final String SPRINGLDAP_FILE = "springldap.file";
    private static final String TIMES_FONT_FILE = "times.font.file";
    private static final String APPLICATION_FOLDER = "korok";
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = new Properties();
    private static String baseDir;
    private static Environment environment = null;

    static {
        baseDir = System.getProperty(PROPERTY_NAME);
        if (baseDir == null) {
            throw new IllegalArgumentException(
                    "System property '" + PROPERTY_NAME + "' isn't setted! Can't initialize application!");
        }
        if (!baseDir.endsWith("/")) {
            baseDir += "/";
        }
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

    public static String getSpringLdapPath() {
        return baseDir + APPLICATION_FOLDER + "/" + properties.getProperty(SPRINGLDAP_FILE);
    }

    public static String getFontPath() {
        return baseDir + APPLICATION_FOLDER + "/" + properties.getProperty(TIMES_FONT_FILE);
    }
}
