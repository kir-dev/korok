package hu.sch.web.profile.confirmation;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserStatus;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.services.exceptions.UpdateFailedException;
import hu.sch.web.profile.ProfilePage;
import hu.sch.web.profile.show.ShowPersonPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author konvergal
 * @author tomi
 */
public final class ConfirmPage extends ProfilePage {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmPage.class);

    public ConfirmPage() {
        super();
    }

    public ConfirmPage(PageParameters params) {
        String uid = params.get("uid").toString();
        User user = userManager.findUserByScreenName(uid);
        if (user == null) {
            getSession().error("A felhasználó nem található!");
            setResponsePage(getApplication().getHomePage());
            return;
        }

        boolean success = false;
        String confirmationCode = params.get("confirmationcode").toString();

        if (confirmationCode.equals(user.getConfirmationCode())) {
            try {
                user.setUserStatus(UserStatus.ACTIVE);
                userManager.updateUser(user);
                success = true;
            } catch (PekEJBException ex) {
                // TODO: proper error reporting to user
                getSession().error("Hiba az ellenőrzéskor.");
                setResponsePage(getApplication().getHomePage());
            }
        }

        Link link = new BookmarkablePageLink("linktoProfile", ShowPersonPage.class);
        add(link);
        if (success) {
            setHeaderLabelText("Megerősítés");
            info("Sikeres megerősítés. :)");
        } else {
            setHeaderLabelText("Hiba!");
            error("Sikertelen megerősítés.");
            link.setVisible(false);
        }
    }
}
