package hu.sch.web.kp.admin;

import hu.sch.domain.profile.Person;
import hu.sch.domain.profile.StudentStatus;
import hu.sch.domain.profile.UserStatus;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.error.NotFound;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.profile.admin.AdminPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author aldaris
 */
public class CreateNewPerson extends KorokPage {

    private Person person = new Person();

    public CreateNewPerson() {
        if (!isCurrentUserAdmin()) {
            throw new RestartResponseException(NotFound.class);
        }

        Form<Person> form = new Form<Person>("form", new CompoundPropertyModel<Person>(person)) {

            @Override
            protected void onSubmit() {
                person.setStudentStatus(StudentStatus.OTHER);
                person.setStatus(UserStatus.ACTIVE);
                ldapManager.registerPerson(person, null);
                setResponsePage(AdminPage.class, new PageParameters().set("uid", person.getUid()));
            }
        };
        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        RequiredTextField<String> uidTF = new RequiredTextField<String>("uid");
        final Label notifier = new Label("notifier", "");
        AjaxFormComponentUpdatingBehavior afcup = new AjaxFormComponentUpdatingBehavior("onblur") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (person.getUid() != null) {
                    try {
                        ldapManager.getPersonByUid(person.getUid());
                        notifier.setDefaultModelObject("Foglalt uid!");
                    } catch (PersonNotFoundException pnfe) {
                        notifier.setDefaultModelObject("Szabad uid");
                    }
                }
                if (target != null) {
                    target.add(wmc);
                }
            }
        };
        uidTF.add(afcup);
        wmc.add(uidTF);
        wmc.add(notifier);
        wmc.setOutputMarkupId(true);
        form.add(wmc);

        form.add(new RequiredTextField<String>("lastName"));
        form.add(new RequiredTextField<String>("firstName"));
        form.add(new RequiredTextField<String>("mail"));

        add(form);
    }
}
