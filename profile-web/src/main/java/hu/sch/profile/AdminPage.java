/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.profile;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
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

    public AdminPage() {
        setResponsePage(new ErrorPage("A kért oldal nem található!"));
        return;
    }

    public AdminPage(PageParameters params) {
        if (!isCurrentUserAdmin()) {
            setResponsePage(new ErrorPage("A kért oldal nem található!"));
            return;
        }

        String uid = params.getString("uid");
        if (uid == null) {
            setResponsePage(new ErrorPage("A kért oldal nem található!"));
            return;
        }
        
        try {
            person = LDAPPersonManager.getInstance().getPersonByUid(uid);
        } catch (PersonNotFoundException e) {
            setResponsePage(new ErrorPage("A felhasználó nem található!"));
            return;
        }

        add(new Label("uid", new Model(person.getFullName() + " szerkesztése")));
        add(new FeedbackPanel("feedbackPanel"));


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
                setResponsePage(new ShowPersonPage(new PageParameters("uid=" + person.getUid()), "Isten vagy! :)"));
            }
        });
    }
}
