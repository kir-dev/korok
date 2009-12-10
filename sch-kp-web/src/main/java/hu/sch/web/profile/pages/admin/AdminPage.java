/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.profile.pages.admin;

import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.components.ConfirmationBoxRenderer;
import hu.sch.web.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.components.ValidationStyleBehavior;
import hu.sch.web.error.ErrorPage;
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
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
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

        Link<Void> deletePersonLink = new Link<Void>("deletePersonLink") {

            @Override
            public void onClick() {
                try {
                    ldapManager.deletePersonByUid(person.getUid());
                } catch (PersonNotFoundException e) {
                }

                getSession().info("A felhasználó (" + person.getUid() + ", " + person.getFullName() + ", " + person.getMail() + ") sikeresen törölve lett.");
                setResponsePage(ShowPersonPage.class, new PageParameters("uid=" + person.getUid()));
            }
        };
        deletePersonLink.add(
                new ConfirmationBoxRenderer("return confirm(\"Biztos, hogy törölni akarod a felasználót? \\n Uid: " +
                person.getUid() + "\\n Név: " + person.getFullName() + "\\n Mail: " +
                person.getMail() + "\");"));
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
        getSession().error("A kért oldal nem található!");
        throw new RestartResponseException(ErrorPage.class);
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
