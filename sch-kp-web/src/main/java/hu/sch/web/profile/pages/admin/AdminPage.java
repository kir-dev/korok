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
package hu.sch.web.profile.pages.admin;

import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.components.ValidationStyleBehavior;
import hu.sch.web.components.customlinks.DeletePersonLink;
import hu.sch.web.error.NotFound;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.profile.pages.edit.PersonForm;
import hu.sch.web.profile.pages.edit.PersonForm.KeyValuePairInForm;
import hu.sch.web.profile.pages.template.ProfilePage;
import hu.sch.web.profile.pages.show.ShowPersonPage;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author konvergal
 */
public class AdminPage extends ProfilePage {

    public Person person;

    public AdminPage() {
        error();
    }

    public AdminPage(PageParameters params) {
        if (!isCurrentUserAdmin()) {
            error();
        }
        String uid = params.getString("uid");
        if (uid == null) {
            error();
        }

        add(new FeedbackPanel("feedbackPanel"));
        try {
            person = ldapManager.getPersonByUid(uid);
        } catch (PersonNotFoundException e) {
            error();
        }

        add(new Label("uid", new Model<String>(person.getFullName() + " szerkesztése")));

        Panel deletePersonLink = new DeletePersonLink("deletePersonLink", person, ShowUser.class);
        add(deletePersonLink);

        add(new PersonForm("personForm", person) {

            @Override
            public void onInit() {
                super.onInit();

                TextField neptunTF = (TextField) new TextField("neptun").setRequired(true);
                neptunTF.add(new ValidationStyleBehavior());
                add(neptunTF);
                neptunTF.setLabel(new Model<String>("Neptun *"));
                add(new ValidationSimpleFormComponentLabel("neptunInputLabel", neptunTF));

                TextField virIdTF = new TextField("virId");
                add(virIdTF);
                virIdTF.setLabel(new Model<String>("Vir ID"));
                add(new SimpleFormComponentLabel("virIdLabel", virIdTF));

                IModel<List<KeyValuePairInForm>> status = new LoadableDetachableModel<List<KeyValuePairInForm>>() {

                    @Override
                    public List<KeyValuePairInForm> load() {
                        List<KeyValuePairInForm> l = new ArrayList<KeyValuePairInForm>();
                        l.add(new KeyValuePairInForm("Active", "Aktív"));
                        l.add(new KeyValuePairInForm("Inactive", "Inaktív"));
                        return l;
                    }
                };
                DropDownChoice<KeyValuePairInForm> statusDropDownChoice = new DropDownChoice<KeyValuePairInForm>("status", status);
                statusDropDownChoice.setChoiceRenderer(new DropDownChoiceRenderer());
                add(statusDropDownChoice);
                statusDropDownChoice.setLabel(new Model<String>("Státusz *"));
                add(new SimpleFormComponentLabel("statusLabel", statusDropDownChoice));

                IModel<List<KeyValuePairInForm>> studentStatus = new LoadableDetachableModel<List<KeyValuePairInForm>>() {

                    @Override
                    public List<KeyValuePairInForm> load() {
                        List<KeyValuePairInForm> l = new ArrayList<KeyValuePairInForm>();
                        l.add(new KeyValuePairInForm("active", "Aktív"));
                        l.add(new KeyValuePairInForm("other", "Egyéb"));
                        l.add(new KeyValuePairInForm("graduated", "Végzett"));
                        return l;
                    }
                };
                DropDownChoice<KeyValuePairInForm> studentStatusDropDownChoice = new DropDownChoice<KeyValuePairInForm>("studentStatus", studentStatus);
                studentStatusDropDownChoice.setNullValid(true);
                studentStatusDropDownChoice.setChoiceRenderer(new DropDownChoiceRenderer());
                add(studentStatusDropDownChoice);
                studentStatusDropDownChoice.setLabel(new Model<String>("Hallgatói státusz"));
                add(new SimpleFormComponentLabel("studentStatusLabel", studentStatusDropDownChoice));
            }

            @Override
            protected void onSubmit() {
                super.onSubmit();
                info("Isten vagy! :)");
                setResponsePage(ShowPersonPage.class, new PageParameters("uid=" + person.getUid()));
            }
        });
    }

    private void error() {
        throw new RestartResponseException(NotFound.class);
    }

    private static class DropDownChoiceRenderer implements IChoiceRenderer<Object> {

        public Object getDisplayValue(Object object) {
            KeyValuePairInForm status = (KeyValuePairInForm) object;
            return status.getValue();
        }

        public String getIdValue(Object object, int index) {
            return object.toString();
        }
    }
}
