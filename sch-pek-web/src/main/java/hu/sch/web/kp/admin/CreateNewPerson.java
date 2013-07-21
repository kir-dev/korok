package hu.sch.web.kp.admin;

import hu.sch.domain.user.StudentStatus;
import hu.sch.domain.user.User;
import hu.sch.domain.user.UserStatus;
import hu.sch.services.exceptions.CreateFailedException;
import hu.sch.web.error.NotFound;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.profile.admin.AdminPage;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Admin page to create new users.
 *
 * TODO: rename class to follow XXXPage naming convention.
 *
 * @author aldaris
 */
public class CreateNewPerson extends KorokPage {

    private static Logger logger = LoggerFactory.getLogger(CreateNewPerson.class);
    private User user = new User();

    public CreateNewPerson() {
        if (!isCurrentUserAdmin()) {
            throw new RestartResponseException(NotFound.class);
        }

        Form<User> form = new Form<User>("form", new CompoundPropertyModel<>(user)) {
            @Override
            protected void onSubmit() {
                user.setStudentStatus(StudentStatus.OTHER);
                try {
                    userManager.createUser(user, RandomStringUtils.randomAlphanumeric(10), UserStatus.ACTIVE);
                } catch (CreateFailedException ex) {
                    logger.error("Could not save user", ex);
                    throw new RestartResponseException(CreateNewPerson.class);
                }
                setResponsePage(AdminPage.class, new PageParameters().set("uid", user.getScreenName()));
            }
        };
        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        RequiredTextField<String> uidTF = new RequiredTextField<String>("screenName");
        final Label notifier = new Label("notifier", "");
        AjaxFormComponentUpdatingBehavior afcup = new AjaxFormComponentUpdatingBehavior("onblur") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (user.getScreenName() != null) {
                    if (userManager.findUserByScreenName(user.getScreenName()) != null) {
                        notifier.setDefaultModelObject("Foglalt uid!");
                    }
                    notifier.setDefaultModelObject("Szabad uid");
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
        form.add(new RequiredTextField<String>("emailAddress"));

        add(form);
    }
}
