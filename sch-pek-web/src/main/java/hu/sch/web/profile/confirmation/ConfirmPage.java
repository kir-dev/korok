package hu.sch.web.profile.confirmation;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserStatus;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.web.profile.ProfilePage;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author konvergal
 * @author tomi
 */
public final class ConfirmPage extends ProfilePage {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmPage.class);
    private String password;
    private String passwordConfirm;
    private User user = null;

    public ConfirmPage() {
        throw new RestartResponseException(getApplication().getHomePage());
    }

    public ConfirmPage(PageParameters params) {
        String confirmationCode = params.get("code").toString("");

        if (StringUtils.isBlank(confirmationCode)) {
            throw new RestartResponseException(getApplication().getHomePage());
        }

        user = userManager.findUserByConfirmationCode(confirmationCode);
        if (user == null) {
            throw new RestartResponseException(getApplication().getHomePage());
        }

        // user has password -> just confirm, nothing else to do
        if (StringUtils.isNotBlank(user.getPasswordDigest())) {
            confirmAndRedirect(null);
        } else {
            addPasswordFields();
        }

    }

    private void addPasswordFields() {
        Form<Void> form = new Form<Void>("passwordForm") {
            @Override
            protected void onSubmit() {
                confirmAndRedirect(password);
            }
        };

        PasswordTextField passwordTF = new PasswordTextField("password", new PropertyModel<String>(this, "password"));
        passwordTF.add(StringValidator.minimumLength(6));
        FormComponentLabel passwordLabel = new FormComponentLabel("passwordLabel", passwordTF);
        form.add(passwordLabel, passwordTF);

        PasswordTextField passwordConfirmTF = new PasswordTextField("passwordConfirm", new PropertyModel<String>(this, "passwordConfirm"));
        FormComponentLabel passwordConfirmLabel = new FormComponentLabel("passwordConfirmLabel", passwordConfirmTF);
        form.add(passwordConfirmLabel, passwordConfirmTF);

        form.add(new EqualPasswordInputValidator(passwordTF, passwordConfirmTF));

        add(form);
    }

    private void confirmAndRedirect(String password) {
        try {
            userManager.confirm(user, password);
            getSession().info("Sikeres megerősítés. Most már be tudsz jelentkezni.");
            setResponsePage(getApplication().getHomePage());
        } catch (PekEJBException ex) {
            // TODO: proper error message?
            getSession().error("Nem sikerült megerősíteni a fehasználód. Kérlek fordulj a supporthoz!");
            throw new RestartResponseException(getApplication().getHomePage());
        }
    }
}
