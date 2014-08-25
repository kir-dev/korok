package hu.sch.web.idm.pages;

import hu.sch.domain.user.User;
import hu.sch.services.AuthSchUserIntegration;
import hu.sch.services.RegistrationManagerLocal;
import hu.sch.services.dto.RegisteringUser;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.session.VirSession;
import hu.sch.web.wicket.behaviors.FocusOnLoadBehavior;
import javax.inject.Inject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 * @author tomi
 */
public class RegistrationPage extends KorokPage {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationPage.class);

    private RegisteringUser user;
    private Label screenNameTakenLbl;
    private final Form<RegisteringUser> regForm;

    @Inject
    private RegistrationManagerLocal registrationManager;

    @Inject
    private AuthSchUserIntegration userIntegaration;

    public RegistrationPage() {
        setHeaderLabelText("Regisztráció");

        user = new RegisteringUser(getSession().getOAuthUserInfo());
        regForm = createForm();
        add(regForm);
    }

    @Override
    protected boolean needsLogin() {
        return false;
    }

    private void onRegFromSubmit() {
        try {
            User registeredUser = registrationManager.doRegistration(user);
            updateSession(registeredUser);
            userIntegaration.pingBack(getSession().getAccessToken());

            getSession().info(getString("reg.successful"));
            setResponsePage(getApplication().getHomePage());
            logger.info("User (id: {}, screen name: {}) was successfully registered.", registeredUser.getId(), registeredUser.getScreenName());
        } catch (PekEJBException ex) {
            reportError(ex);
        }
    }

    private Form<RegisteringUser> createForm() {
        Form<RegisteringUser> form = new Form<RegisteringUser>("regForm", new CompoundPropertyModel<>(user)) {

            @Override
            protected void onSubmit() {
                onRegFromSubmit();
            }
        };

        addTextField(form, "screenName", true)
                .add(new OnChangeAjaxBehavior() {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        if (registrationManager.isUidTaken(user.getScreenName())) {
                            screenNameTakenLbl.add(new AttributeModifier("class", "screen-name-taken"));
                            screenNameTakenLbl.setDefaultModelObject(getString("screenname.taken"));
                        } else {
                            screenNameTakenLbl.add(new AttributeModifier("class", "screen-name-ok"));
                            screenNameTakenLbl.setDefaultModelObject(getString("screenname.ok"));
                        }

                        target.add(screenNameTakenLbl);
                    }
                })
                .add(new FocusOnLoadBehavior());

        addTextField(form, "mail", true).add(EmailAddressValidator.getInstance());
        addTextField(form, "firstName", true);
        addTextField(form, "lastName", true);
        addTextField(form, "dormitory", false);
        addTextField(form, "roomNumber", false);

        form.add(screenNameTakenLbl = new Label("screenNameTakenLbl", Model.of("")));
        screenNameTakenLbl.setOutputMarkupId(true);

        return form;
    }

    private TextField<String> addTextField(Form<?> form, String id, boolean required) {
        TextField<String> textField = new TextField<>(id);
        textField.setRequired(required);
        textField.setLabel(Model.of(getString(id)));
        form.add(textField, new FormComponentLabel(id + "Lbl", textField));

        return textField;
    }

    private void updateSession(User user) {
        VirSession session = getSession();
        session.setOAuthUserInfo(null);
        session.setUserId(user.getId());
    }

    private void reportError(PekEJBException ex) {
        if (ex.getParameters().length > 0) {
            Object p = ex.getParameters()[0];

            if (p != null) {
                regForm.error(getString("err." + p.toString()));
            }
        }
    }
}
