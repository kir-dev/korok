package hu.sch.web.profile.admin;

import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.error.NotFound;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.profile.ProfilePage;
import hu.sch.web.profile.edit.PersonForm;
import hu.sch.web.profile.edit.PersonForm.KeyValuePairInForm;
import hu.sch.web.profile.show.ShowPersonPage;
import hu.sch.web.wicket.behaviors.ValidationStyleBehavior;
import hu.sch.web.wicket.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.wicket.components.customlinks.DeletePersonLink;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

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
        String uid = params.get("uid").toString();
        if (uid == null) {
            error();
        }

        try {
            person = ldapManager.getPersonByUid(uid);
        } catch (PersonNotFoundException e) {
            error();
        }

        setHeaderLabelText(person.getFullName() + " szerkesztése");

        Panel deletePersonLink = new DeletePersonLink("deletePersonLink", person, ShowUser.class);
        add(deletePersonLink);

        add(new PersonForm("personForm", person) {

            @Override
            protected void onInit() {
                super.onInit();
                createSvieFields();

                final TextField neptunTF = (TextField) new TextField("neptun").setRequired(false);
                neptunTF.add(new ValidationStyleBehavior());
                add(neptunTF);
                neptunTF.setLabel(new Model<String>("Neptun"));
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
                        l.add(new KeyValuePairInForm("newbie", "Gólya"));
                        l.add(new KeyValuePairInForm("other", "Egyéb"));
                        l.add(new KeyValuePairInForm("graduated", "Végzett"));
                        return l;
                    }
                };
                final DropDownChoice<KeyValuePairInForm> studentStatusDropDownChoice = new DropDownChoice<KeyValuePairInForm>("studentStatus", studentStatus);
                studentStatusDropDownChoice.setNullValid(true);
                studentStatusDropDownChoice.setChoiceRenderer(new DropDownChoiceRenderer());
                add(studentStatusDropDownChoice);
                studentStatusDropDownChoice.setLabel(new Model<String>("Hallgatói státusz"));
                add(new SimpleFormComponentLabel("studentStatusLabel", studentStatusDropDownChoice));

                // a neptun kód ne legyen kötelező, ha a hallgatói státusz egyéb
                add(new AbstractFormValidator() {

                    @Override
                    public FormComponent<?>[] getDependentFormComponents() {
                        return new FormComponent[]{neptunTF, studentStatusDropDownChoice};
                    }

                    @Override
                    public void validate(Form<?> form) {
                        if (neptunTF.getValue().isEmpty()
                                && studentStatusDropDownChoice.getValue().equals("active")) {
                            error(neptunTF, "admin.create.person.err.neptunNelkuliAktiv");
                        }
                    }
                });
            }

            @Override
            protected void onSubmit() {
                super.onSubmit();
                info("Isten vagy! :)");
                setResponsePage(ShowPersonPage.class, new PageParameters().add("uid", person.getUid()));
            }
        });
    }

    private void error() {
        throw new RestartResponseException(NotFound.class);
    }

    private static class DropDownChoiceRenderer implements IChoiceRenderer<Object> {

        @Override
        public Object getDisplayValue(Object object) {
            KeyValuePairInForm status = (KeyValuePairInForm) object;
            return status.getValue();
        }

        @Override
        public String getIdValue(Object object, int index) {
            return object.toString();
        }
    }
}
