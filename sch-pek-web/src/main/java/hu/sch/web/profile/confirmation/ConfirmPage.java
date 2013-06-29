package hu.sch.web.profile.confirmation;

import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.profile.ProfilePage;
import hu.sch.web.profile.show.ShowPersonPage;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author konvergal
 */
public final class ConfirmPage extends ProfilePage {

    public ConfirmPage() {
        super();
    }

    public ConfirmPage(PageParameters params) throws NoSuchAlgorithmException {
        String uid = params.get("uid").toString();
        try {
            Person person = ldapManager.getPersonByUid(uid);

            boolean success = false;

            String confirmationString = person.getUid() + person.getMail()
                    + person.getFullName();
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(confirmationString.getBytes(), 0, confirmationString.length());
            String confirmationStringMD5 =
                    new BigInteger(1, m.digest()).toString(16);

            String confirmationCode = params.get("confirmationcode").toString();

            if (confirmationCode.equals(confirmationStringMD5)) {
                person.setStatus("Active");
                ldapManager.update(person);
                success = true;
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
        } catch (PersonNotFoundException e) {
            getSession().error("A felhasználó nem található!");
            setResponsePage(getApplication().getHomePage());
        }
    }
}
