/**
 * Copyright (c) 2009, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hu.sch.web.profile.pages.confirmation;

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

            String confirmationString = person.getUid() + person.getMail()
                    + person.getFullName();
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
