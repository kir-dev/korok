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
package hu.sch.web.idm.pages;

import hu.sch.domain.profile.Person;
import hu.sch.services.MailManagerLocal;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;

/**
 *
 * @author aldaris
 */
public class UserNameReminder extends SecuredPageTemplate {

    @EJB(name = "MailManagerBean")
    private MailManagerLocal mailManager;
    public String mail;

    public UserNameReminder() {
        setHeaderLabelText("Felhasználói név emlékeztető");
        add(new FeedbackPanel("pagemessages"));

        Form<Void> reminderForm = new Form<Void>("reminderForm") {

            @Override
            protected void onSubmit() {
                List<Person> results = ldapManager.searchMyUid(mail);
                if (results.isEmpty()) {
                    getSession().error(getLocalizer().getString("err.NoSuchEmail", this));
                    setResponsePage(UserNameReminder.class);
                    return;
                } else if (results.size() > 1) {
                    getSession().error(getLocalizer().getString("err.DuplicatedUsers", this));
                    setResponsePage(UserNameReminder.class);
                    return;
                } else {
                    Person person = results.get(0);
                    try {
                        mailManager.sendEmail(mail, "Felhasználói név emlékeztető",
                                "Kedves " + person.getFirstName() + "!\n\nEhhez az e-mail címedhez a következő felhasználói név van regisztrálva a rendszerben: '"
                                + person.getUid() + "'.\n\nÜdv,\nKir-Dev");
                    } catch (Exception e) {
                        getSession().error(getLocalizer().getString("err.MailError", this));
                        setResponsePage(UserNameReminder.class);
                        return;
                    }
                    getSession().info(getLocalizer().getString("info.ReminderSent", this));
                    setResponsePage(UserNameReminder.class);
                    return;
                }
            }
        };
        RequiredTextField<String> mailTF = new RequiredTextField<String>("mail", new PropertyModel<String>(this, "mail"));
        mailTF.add(EmailAddressValidator.getInstance());
        reminderForm.add(mailTF);
        add(reminderForm);
    }
}
