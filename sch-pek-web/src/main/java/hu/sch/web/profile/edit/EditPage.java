package hu.sch.web.profile.edit;

import hu.sch.domain.user.User;
import hu.sch.web.profile.ProfilePage;

/**
 *
 * @author konvergal
 */
public class EditPage extends ProfilePage {

    public User user;

    public EditPage() {
        super();

        user = userManager.findUserByScreenName(getRemoteUser());
        if (user == null) {
            getSession().error("A felhasználó nem található!");
            setResponsePage(getApplication().getHomePage());
            return;
        }

        setHeaderLabelText(user.getScreenName());
        add(new PersonFormPanel("personFormPanel", user));
    }
}
