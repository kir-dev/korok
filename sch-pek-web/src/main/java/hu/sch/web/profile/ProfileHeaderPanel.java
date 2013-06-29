package hu.sch.web.profile;

import hu.sch.web.common.HeaderLink;
import hu.sch.web.common.HeaderPanel;
import hu.sch.web.profile.birthday.BirthDayPage;
import hu.sch.web.profile.edit.EditPage;
import hu.sch.web.profile.passwordchange.ChangePasswordPage;
import hu.sch.web.profile.show.ShowPersonPage;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author messo
 */
class ProfileHeaderPanel extends HeaderPanel {

    private static final HeaderLink[] links = new HeaderLink[] {
        new HeaderLink(ShowPersonPage.class, "Profilom"),
        new HeaderLink(EditPage.class, "Szerkesztés"),
        new HeaderLink(ChangePasswordPage.class, "Jelszóváltoztatás"),
        new HeaderLink(BirthDayPage.class, "Szülinaposok"),
    };
    private static final List<HeaderLink> linksList = Arrays.asList(links);

    public ProfileHeaderPanel(String id) {
        super(id);
        createLinks();
    }

    @Override
    protected List<HeaderLink> getHeaderLinks() {
        return linksList;
    }
}
