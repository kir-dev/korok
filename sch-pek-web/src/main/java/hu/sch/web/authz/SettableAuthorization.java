package hu.sch.web.authz;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import hu.sch.services.Role;
import hu.sch.services.UserManagerLocal;
import javax.inject.Inject;
import org.apache.wicket.Application;
import org.apache.wicket.cdi.CdiContainer;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is ONLY for development.
 *
 * Attributes are settable - check out the /dev page in development mode.
 *
 * @author tomi
 */
public class SettableAuthorization implements UserAuthorization {

    private static final Logger logger = LoggerFactory.getLogger(SettableAuthorization.class);

    private Long userId;

    @Inject
    private UserManagerLocal userManager;

    public SettableAuthorization(Long userId) {
        this.userId = userId;
    }

    @Override
    public void init(Application wicketApplication) {
        CdiContainer.get().getNonContextualManager().inject(this);
        logger.info("Settable authorization module has been initialized.");
    }

    @Override
    public Long getUserid(Request wicketRequest) {
        return userId;
    }

    @Override
    public String getRemoteUser(Request wicketRequest) {
        User user = userManager.findUserById(userId);
        if (user != null) {
            return user.getScreenName();
        }

        return null;
    }

    @Override
    public boolean isGroupLeaderInGroup(Request wicketRequest, Group group) {
        // TODO: make this settable
        return true;
    }

    @Override
    public boolean isGroupLeaderInSomeGroup(Request wicketRequest) {
        // TODO: make this settable
        return true;
    }

    @Override
    public boolean hasAbstractRole(Request wicketRequest, Role role) {
        // TODO: make this settable
        return true;
    }

    @Override
    public User getUserAttributes(Request wicketRequest) {
        return userManager.findUserById(userId, true);
    }

    @Override
    public boolean isLoggedIn(Request wicketRequest) {
        return true;
    }
}