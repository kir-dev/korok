package hu.sch.services.config;

import hu.sch.domain.user.StudentStatus;

/**
 *
 * @author tomi
 */
public interface Configuration {

    String getDevEmail();

    String getErrorReportingEmail();

    Environment getEnvironment();

    String getFontPath();

    ImageUploadConfig getImageUploadConfig();

    String getKorokDomain();

    String getProfileDomain();

    String getSupportBaseUrl();

    int getSupportDefaultId();

    OAuthCredentials getOAuthCredentials();

    String getVersionInfoLink();

    StudentStatus getNewUserStudentStatus();

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
}
