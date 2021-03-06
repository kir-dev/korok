package hu.sch.web.authz;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import hu.sch.services.Authorization;
import hu.sch.services.MembershipManagerLocal;
import hu.sch.services.Role;
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

    @Inject
    private MembershipManagerLocal membershipManager;

    @Inject
    private Authorization authorization;

    @Override
    public void init(Application wicketApplication) {
        // NOTE: we can do this here because the UserManager is stateless!
        CdiContainer.get().getNonContextualManager().inject(this);
        logger.info("{} has been successfully initialized.", getClass().getSimpleName());
    }

    @Override
    public Long getCurrentUserId(Request wicketRequest) {
        return VirSession.get().getUserId();
    }

    @Override
    public boolean isGroupLeaderInGroup(Request wicketRequest, Group group) {
        return membershipManager.isGroupLeader(getCurrentUserId(wicketRequest), group);
    }

    @Override
    public boolean isGroupLeaderInSomeGroup(Request wicketRequest) {
        return membershipManager.hasGroupLeadership(getCurrentUserId(wicketRequest));
    }

    @Override
    public boolean hasAbstractRole(Request wicketRequest, Role role) {
        return authorization.hasRole(getCurrentUserId(wicketRequest), role);
    }

    @Override
    public User getCurrentUser(Request wicketRequest) {
        Long id = getCurrentUserId(wicketRequest);
        if (id != null) {
            return userManager.findUserById(id, true);
        }

        return null;
    }

    @Override
    public String getRemoteUser(Request wicketRequest) {
        User user = getCurrentUser(wicketRequest);
        if (user != null) {
            return user.getScreenName();
        }

        return null;
    }

    @Override
    public boolean isLoggedIn(Request wicketRequest) {
        return VirSession.get().isUserSignedIn();
    }
}
