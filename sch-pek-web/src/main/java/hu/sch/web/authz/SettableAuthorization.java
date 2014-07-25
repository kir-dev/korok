package hu.sch.web.authz;

import org.apache.wicket.request.Request;

/**
 * This is ONLY for development.
 *
 * Attributes are settable - check out the /dev page in development mode.
 *
 * @author tomi
 */
public class SettableAuthorization extends SessionBasedAuthorization {

    private Long userId;

    public SettableAuthorization(Long userId) {
        this.userId = userId;
    }

    @Override
    public Long getUserid(Request wicketRequest) {
        return userId;
    }

    @Override
    public boolean isLoggedIn(Request wicketRequest) {
        return true;
    }
}
