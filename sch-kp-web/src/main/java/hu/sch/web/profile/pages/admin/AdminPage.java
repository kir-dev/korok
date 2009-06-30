/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.profile.pages.admin;

import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.components.ValidationStyleBehavior;
import hu.sch.web.error.ErrorPage;
import hu.sch.web.profile.pages.edit.KeyValuePairInForm;
import hu.sch.web.profile.pages.edit.PersonForm;
import hu.sch.web.profile.pages.template.ProfilePage;
import hu.sch.web.profile.pages.show.ShowPersonPage;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
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

    class DropDownChoiceRenderer implements IChoiceRenderer {

        public Object getDisplayValue(Object object) {
            KeyValuePairInForm status = (KeyValuePairInForm) object;
            return status.getValue();
        }

        public String getIdValue(Object object, int index) {
            return object.toString();
        }
    }

    private void error() {
        getSession().error("A kért oldal nem található!");
        setResponsePage(ErrorPage.class);
    }

    public AdminPage() {
        error();
    }

    public AdminPage(PageParameters params) {
        if (!isCurrentUserAdmin()) {
            error();
            return;
        }
        add(new FeedbackPanel("feedbackPanel"));
        String uid = params.getString("uid");
        if (uid == null) {
            error();
            return;
        }

        try {
            person = ldapManager.getPersonByUid(uid);
        } catch (PersonNotFoundException e) {
            error();
            return;
        }

        add(new Label("uid", new Model(person.getFullName() + " szerkesztése")));

        Link deletePersonLink = new Link("deletePersonLink") {

            @Override
            public void onClick() {
                try {
                    ldapManager.deletePersonByUid(person.getUid());
                } catch (PersonNotFoundException e) {
                }

                getSession().info("A felhasználó (" + person.getUid() + ", " + person.getFullName() + ", " + person.getMail() + ") sikeresen törölve lett.");
                setResponsePage(new ShowPersonPage());
            }
        };
        deletePersonLink.add(new SimpleAttributeModifier("onclick", "return confirm(\"Biztos, hogy törölni akarod a felasználót? \\n Uid: " + person.getUid() + "\\n Név: " + person.getFullName() + "\\n Mail: " + person.getMail() + "\");"));
        add(deletePersonLink);


        add(new PersonForm("personForm", person) {

            @Override
            public void initFormComponents() {
                super.initFormComponents();

                TextField neptunTF = (TextField) new TextField("neptun").setRequired(true);
                neptunTF.add(new ValidationStyleBehavior());
                add(neptunTF);
                neptunTF.setLabel(new Model("Neptun *"));
                add(new ValidationSimpleFormComponentLabel("neptunInputLabel", neptunTF));

                TextField virIdTF = new TextField("virId");
                add(virIdTF);
                virIdTF.setLabel(new Model("Vir ID"));
                add(new SimpleFormComponentLabel("virIdLabel", virIdTF));

                IModel status = new LoadableDetachableModel() {

                    public Object load() {
                        List l = new ArrayList();
                        l.add(new KeyValuePairInForm("Active", "Aktív"));
                        l.add(new KeyValuePairInForm("Inactive", "Inaktív"));
                        return l;
                    }
                };
                DropDownChoice statusDropDownChoice = new DropDownChoice("status", status);
                IChoiceRenderer statusRenderer = new DropDownChoiceRenderer();
                statusDropDownChoice.setChoiceRenderer(statusRenderer);
                add(statusDropDownChoice);
                statusDropDownChoice.setLabel(new Model("Státusz *"));
                add(new SimpleFormComponentLabel("statusLabel", statusDropDownChoice));

                IModel studentStatus = new LoadableDetachableModel() {

                    public Object load() {
                        List l = new ArrayList();
                        l.add(new KeyValuePairInForm("active", "Aktív"));
                        l.add(new KeyValuePairInForm("other", "Egyéb"));
                        l.add(new KeyValuePairInForm("graduated", "Végzett"));
                        return l;
                    }
                };
                DropDownChoice studentStatusDropDownChoice = new DropDownChoice("studentStatus", studentStatus);
                studentStatusDropDownChoice.setNullValid(true);
                IChoiceRenderer studentStatusRenderer = new DropDownChoiceRenderer();
                studentStatusDropDownChoice.setChoiceRenderer(studentStatusRenderer);
                add(studentStatusDropDownChoice);
                studentStatusDropDownChoice.setLabel(new Model("Hallgatói státusz"));
                add(new SimpleFormComponentLabel("studentStatusLabel", studentStatusDropDownChoice));
            }

            @Override
            protected void onSubmit() {
                super.onSubmit();
                info("Isten vagy! :)");
                setResponsePage(new ShowPersonPage(new PageParameters("uid=" + person.getUid())));
            }
        });
    }
}
