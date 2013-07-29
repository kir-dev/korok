package hu.sch.web.profile.admin;

import hu.sch.domain.user.User;
import hu.sch.web.error.NotFound;
import hu.sch.web.profile.ProfilePage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author konvergal
 */
public class AdminPage extends ProfilePage {

    public User user;

    public AdminPage() {
        error();
    }

    public AdminPage(PageParameters params) {
        if (!isCurrentUserAdmin()) {
            error();
        }
        String uid = params.get("uid").toString();
        if (uid == null) {
            error();
        }

        user = userManager.findUserByScreenName(uid);
        if (user == null) {
            error();
        }

        setHeaderLabelText(user.getFullName() + " szerkesztése");
        add(new AdminPersonFormPanel("adminPanel", user));
    }

    private void error() {
        throw new RestartResponseException(NotFound.class);
    }
}
