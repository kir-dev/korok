package hu.sch.web.profile.pages.confirmation;

/*
 *  Copyright 2008 konvergal.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.error.ErrorPage;
import hu.sch.web.profile.pages.template.ProfilePage;
import hu.sch.web.profile.pages.show.ShowPersonPage;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 *
 * @author konvergal
 */
public final class ConfirmPage extends ProfilePage {

    public ConfirmPage() {
        super();
    }

    public ConfirmPage(PageParameters params) throws NoSuchAlgorithmException {
        String uid = params.getString("uid");
        add(new FeedbackPanel("feedbackPanel"));
        try {
            Person person = ldapManager.getPersonByUid(uid);

            boolean success = false;

            String confirmationString = person.getUid() + person.getMail() +
                    person.getFullName();
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(confirmationString.getBytes(), 0, confirmationString.length());
            String confirmationStringMD5 =
                    new BigInteger(1, m.digest()).toString(16);

            String confirmationCode = params.getString("confirmationcode");

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
            setResponsePage(ErrorPage.class);
            return;

        }
    }
}
