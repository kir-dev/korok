package hu.sch.web.session;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

/**
 *
 * @author hege
 */
public class VirSession extends WebSession {

    private Long userId;
    private String oauthUserState;
    private String accessToken;

    public VirSession(Request request) {
        super(request);
    }

    public static VirSession get() {
        return (VirSession)WebSession.get();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
        dirty();
    }

    public String getOauthUserState() {
        return oauthUserState;
    }

    public void setOauthUserState(String oauthUserState) {
        this.oauthUserState = oauthUserState;
        dirty();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        dirty();
    }

    public boolean isUserSignedIn() {
        return getUserId() != null;
    }
}
