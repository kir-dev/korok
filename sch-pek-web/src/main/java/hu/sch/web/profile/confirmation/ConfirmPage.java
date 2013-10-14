package hu.sch.web.profile.confirmation;

import hu.sch.domain.config.Configuration;
import hu.sch.domain.user.User;
import hu.sch.services.AccountManager;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.web.error.NotFound;
import hu.sch.web.profile.ProfilePage;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Confirms a user account. If the user hasn't got a password he can set it on
 * this page.
 *
 * @author konvergal
 * @author tomi
 * @author balo
 */
public final class ConfirmPage extends ProfilePage {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmPage.class);
    //
    @Inject
    protected AccountManager accountManager;
    private String password;
    private String passwordConfirm;
    private User user = null;

    public ConfirmPage() {
        throw new RestartResponseException(NotFound.class);
    }

    public ConfirmPage(final PageParameters params) {
        setHeaderLabelText(getString("headerLabel"));

        final String confirmationCode = params.get("code").toString("");
        setStatelessHint(true);

        if (StringUtils.isBlank(confirmationCode)) {
            error(getString("error.missingcode"));
            return;
        }

        user = userManager.findUserByConfirmationCode(confirmationCode);
        if (user == null) {
            error(getString("error.wrongcode"));
            return;
        }

        // user has password -> just confirm, nothing else to do
        if (StringUtils.isNotBlank(user.getPasswordDigest())) {
            confirm(null);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addPasswordFields();
    }

    private void addPasswordFields() {
        final Form<Void> form = new StatelessForm<Void>("passwordForm") {
            @Override
            protected void onSubmit() {
                if (confirm(password)) {
                    //hide the form if the confirmation was successful
                    setVisible(false);
                }
            }
        };

        final TextField passwordTF = new PasswordTextField("password", new PropertyModel<String>(this, "password"));
        passwordTF
                .setLabel(new ResourceModel("passwordTF"))
                .add(StringValidator.minimumLength(6));

        final FormComponentLabel passwordLabel = new SimpleFormComponentLabel("passwordLabel", passwordTF);
        form.add(passwordLabel, passwordTF);

        final TextField passwordConfirmTF = new PasswordTextField("passwordConfirm", new PropertyModel<String>(this, "passwordConfirm"));
        passwordConfirmTF.setLabel(new ResourceModel("passwordConfirmTF"));

        final FormComponentLabel passwordConfirmLabel = new SimpleFormComponentLabel("passwordConfirmLabel", passwordConfirmTF);

        form
                .add(passwordConfirmLabel, passwordConfirmTF)
                .add(new EqualPasswordInputValidator(passwordTF, passwordConfirmTF))
                //if we found the user and he hasn't got a password
                .setVisible(user != null && StringUtils.isBlank(user.getPasswordDigest()));

        add(form);
    }

    private boolean confirm(final String password) {
        try {
            accountManager.confirm(user, password);
            info(String.format(getString("confirm.success"),
                    Configuration.getProfileDomain()));

            return true;
        } catch (PekEJBException ex) {
            error(getString("confirm.failed"));
        }

        return false;
    }
}
