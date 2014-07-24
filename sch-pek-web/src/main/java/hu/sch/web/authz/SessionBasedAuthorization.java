package hu.sch.web.authz;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import hu.sch.web.session.VirSession;
import javax.inject.Inject;
import org.apache.wicket.Application;
import org.apache.wicket.cdi.CdiContainer;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionBasedAuthorization implements UserAuthorization {

    private static final Logger logger = LoggerFactory.getLogger(SessionBasedAuthorization.class);

    @Inject
    private UserManagerLocal userManager;

    @Override
    public void init(Application wicketApplication) {
        // NOTE: we can do this here because the UserManager is stateless!
        CdiContainer.get().getNonContextualManager().inject(this);
        logger.info("SessionBasedAuthorization successfully initialized.");
    }

    @Override
    public Long getUserid(Request wicketRequest) {
        return VirSession.get().getUserId();
    }

    @Override
    public boolean isGroupLeaderInGroup(Request wicketRequest, Group group) {
        // TODO: check for group leadership
        return true;
    }

    @Override
    public boolean isGroupLeaderInSomeGroup(Request wicketRequest) {
        // TODO: check for group leadership
        return true;
    }

    @Override
    public boolean hasAbstractRole(Request wicketRequest, String role) {
        // TODO: get it from memberships
        return true;
    }

    @Override
    public User getUserAttributes(Request wicketRequest) {
        Long id = getUserid(wicketRequest);
        if (id != null) {
            return userManager.findUserById(id);
        }

        return null;
    }

    @Override
    public String getRemoteUser(Request wicketRequest) {
        User user = getUserAttributes(wicketRequest);
        if (user != null) {
            return user.getScreenName();
        }

        return null;
    }

}
