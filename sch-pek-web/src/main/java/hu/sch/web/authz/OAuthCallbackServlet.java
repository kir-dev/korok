package hu.sch.web.authz;

import hu.sch.services.dto.OAuthUserInfo;
import com.google.gson.Gson;
import hu.sch.services.AuthSchUserIntegration;
import hu.sch.services.config.Configuration;
import hu.sch.services.config.OAuthCredentials;
import hu.sch.web.session.VirSession;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthCallbackServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(OAuthCallbackServlet.class);
    private static final String REGISTER_URL = "/profile/register";
    private static final String ERROR_HTML_URL = "/oauth_error.html";

    @Inject
    private Configuration config;

    @Inject
    private AuthSchUserIntegration userIntegration;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String accessToken = getAccessToken(req);
            OAuthUserInfo userInfo = getUserInfo(accessToken);
            Long userId = updateSession(userInfo, accessToken);
            if (userId != null) {
                userIntegration.updateUser(userId, userInfo);

                String returnUrl = getSession().getReturnUrl();
                if (returnUrl == null) {
                    resp.sendRedirect("/");
                } else {
                    resp.sendRedirect(returnUrl);
                    getSession().setReturnUrl(null);
                }
            } else {
                resp.sendRedirect(REGISTER_URL);
            }

            logger.info("User successfully logged in via OAuth.");
        } catch (OAuthProblemException | OAuthSystemException | OAuthFlowException ex) {
            logger.error("Error during oauth flow", ex);
            getSession().invalidate();
            sendErrorResponse(resp);
        }
    }

    private String getAccessToken(HttpServletRequest request)
            throws OAuthProblemException, OAuthSystemException, OAuthFlowException {

        OAuthAuthzResponse resp = OAuthAuthzResponse.oauthCodeAuthzResponse(request);

        if (!checkUserState(resp.getState())) {
            logger.error("User state did not match.");
            throw new OAuthFlowException("User state did not match.");
        }

        OAuthCredentials cred = config.getOAuthCredentials();
        OAuthClientRequest oauthRequest = OAuthClientRequest
                .tokenLocation(cred.getTokenUrl())
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(cred.getClientId())
                .setClientSecret(cred.getClientSecret())
                .setCode(resp.getCode())
                .setScope(cred.getScope())
                .buildBodyMessage();

        OAuthClient client = new OAuthClient(new URLConnectionClient());
        OAuthJSONAccessTokenResponse response = client.accessToken(oauthRequest);

        getSession().setAccessToken(response.getAccessToken());
        logger.debug("Access token: {}", response.getAccessToken());
        return response.getAccessToken();
    }

    private OAuthUserInfo getUserInfo(String accessToken) throws OAuthSystemException, IOException, OAuthProblemException {
        OAuthClientRequest request = null;
        request = new OAuthBearerClientRequest("https://auth.sch.bme.hu/api/profile")
                .setAccessToken(accessToken)
                .buildQueryMessage();
        OAuthClient client = new OAuthClient(new URLConnectionClient());

        OAuthResourceResponse response = client.resource(request, OAuth.HttpMethod.GET, OAuthResourceResponse.class);

        Gson gson = new Gson();
        return gson.fromJson(response.getBody(), OAuthUserInfo.class);
    }

    private boolean checkUserState(String state) {
        return state != null && state.equals(getSession().getOauthUserState());
    }

    private VirSession getSession() {
        if (VirSession.exists()) {
            return VirSession.get();
        }
        // theoretically this should never be reached. :)
        throw new IllegalStateException("There is no session, probably oauth_callback servlet was called directly.");
    }

    private void sendErrorResponse(HttpServletResponse response) throws IOException {
        response.sendRedirect(ERROR_HTML_URL);
    }

    // returns the user id when present, null otherwise
    private Long updateSession(OAuthUserInfo userInfo, String accessToken) throws IOException {
        getSession().setAccessToken(accessToken);

        String virIdStr = userInfo.getLinkedAccounts().get("vir");
        if (StringUtils.isBlank(virIdStr)) {
            return null;
        }

        Long id = Long.valueOf(virIdStr);
        getSession().setUserId(id);
        return id;
    }
}
