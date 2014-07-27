package hu.sch.web.authz;

import hu.sch.services.config.OAuthCredentials;
import hu.sch.util.hash.Hashing;
import hu.sch.web.session.VirSession;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Hex;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.slf4j.LoggerFactory;

public class OAuthSignInFlow {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OAuthSignInFlow.class);
    private final OAuthCredentials cred;
    private final Request request;

    public OAuthSignInFlow(OAuthCredentials oauthCred, Request request) {
        this.cred = oauthCred;
        this.request = request;
    }

    public void start() {
        try {
            saveReturnUrl();
            redirectToOAuthLogin();
        } catch (OAuthSystemException ex) {
            logger.error("Could not start oauth flow", ex);
        }
    }

    private void redirectToOAuthLogin() throws OAuthSystemException {
        // redirect url is not necessary since auth.sch will ignore it and redirect
        // to a specified url
        OAuthClientRequest oauthRequest = OAuthClientRequest
                .authorizationLocation(cred.getLoginUrl())
                .setClientId(cred.getClientId())
                .setState(generateUserState())
                .setResponseType("code")
                .setScope(cred.getScope())
                .buildQueryMessage();

        throw new RedirectToUrlException(oauthRequest.getLocationUri());
    }

    private void saveReturnUrl() {
        VirSession.get().setReturnUrl("/" + request.getUrl().toString());
    }

    private String generateUserState() {
        WebClientInfo info = new WebClientInfo(RequestCycle.get());
        String stateToken = generateRandomToken();
        byte[] input = info.getUserAgent().concat(stateToken).getBytes(StandardCharsets.UTF_8);

        String state = Hashing.sha1(input).toBase64();

        VirSession session = VirSession.get();
        session.setOauthUserState(state);
        // make sure it's been bound
        session.bind();

        return state;
    }

    private String generateRandomToken() {
        SecureRandom sr = new SecureRandom();
        byte[] buf = new byte[40];
        sr.nextBytes(buf);

        return Hex.encodeHexString(buf);
    }

}
