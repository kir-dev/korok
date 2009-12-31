/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.admin;

import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.error.NotFound;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.profile.pages.admin.AdminPage;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author aldaris
 */
public class CreateNewPerson extends SecuredPageTemplate {

    private Person person = new Person();

    public CreateNewPerson() {
        if (!isCurrentUserAdmin()) {
            throw new RestartResponseException(NotFound.class);
        }

        Form<Person> form = new Form<Person>("form", new CompoundPropertyModel<Person>(person)) {

            @Override
            protected void onSubmit() {
                person.setStudentUserStatus("urn:mace:terena.org:schac:status:sch.hu:student_status:other");
                ldapManager.bindPerson(person);
                setResponsePage(AdminPage.class, new PageParameters("uid=" + person.getUid()));
                return;
            }
        };
        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        TextField<String> uidTF = new TextField<String>("uid");
        final Label notifier = new Label("notifier", "");
        AjaxFormComponentUpdatingBehavior afcup = new AjaxFormComponentUpdatingBehavior("onblur") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                try {
                    ldapManager.getPersonByUid(person.getUid());
                    notifier.setDefaultModelObject("Foglalt uid!");
                } catch (PersonNotFoundException pnfe) {
                    notifier.setDefaultModelObject("Szabad uid");
                }
                if (target != null) {
                    target.addComponent(wmc);
                }
            }
        };
        uidTF.add(afcup);
        wmc.add(uidTF);
        wmc.add(notifier);
        wmc.setOutputMarkupId(true);
        form.add(wmc);

        form.add(new TextField<String>("lastName"));
        form.add(new TextField<String>("firstName"));
        form.add(new TextField<String>("mail"));

        add(form);
    }
}
