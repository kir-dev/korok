package hu.sch.web.wicket.util;

import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import javax.inject.Inject;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
public class LoadableDetachableUserModel extends LoadableDetachableModel<User> {

    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(LoadableDetachableUserModel.class);
    @Inject
    private UserManagerLocal userManager;
    private transient User user = null;
    private String screenName;

    public LoadableDetachableUserModel(User u) {
        user = u;
        screenName = u.getScreenName();
    }

    @Override
    protected User load() {
        if (user != null) {
            return user;
        }
        user = userManager.findUserByScreenName(screenName);
        return user;
    }
}
