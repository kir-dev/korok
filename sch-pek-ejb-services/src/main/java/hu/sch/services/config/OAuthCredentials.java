package hu.sch.services.config;

public class OAuthCredentials {

    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;
    private final String loginUrl;
    private final String scope;

    public OAuthCredentials(String clientId, String clientSecret, String tokenUrl, String loginUrl, String scope) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUrl = tokenUrl;
        this.loginUrl = loginUrl;
        this.scope = scope;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getScope() {
        return scope;
    }
}
