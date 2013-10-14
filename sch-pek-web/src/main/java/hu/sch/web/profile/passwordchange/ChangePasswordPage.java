package hu.sch.web.profile.passwordchange;

import hu.sch.services.AccountManager;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.web.profile.ProfilePage;
import javax.inject.Inject;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

/**
 *
 * @author Adam Lantos
 */
public class ChangePasswordPage extends ProfilePage {

    @Inject
    protected AccountManager accountManager;
    //these fields used by the CompoundPropertyModel
    private String oldPassword;
    private String newPassword;
    private String newPasswordConfirmation;

    public ChangePasswordPage() {
        setHeaderLabelText("Jelszóváltoztatás");
        Form form = new Form("changePasswordForm", new CompoundPropertyModel(this)) {
            @Override
            protected void onSubmit() {
                try {
                    accountManager.changePassword(getRemoteUser(), oldPassword, newPassword);
                    getSession().info("Sikeres jelszóváltoztatás");
                } catch (PekEJBException ex) {
                    getSession().error("Hibás jelszó!");
                }
            }
        };

        final PasswordTextField oldPw = new PasswordTextField("oldPassword");
        oldPw.setRequired(true);
        oldPw.setResetPassword(true);
        form.add(oldPw);

        final PasswordTextField newPw = new PasswordTextField("newPassword");
        newPw.setRequired(true);
        newPw.setResetPassword(true);
        newPw.add(StringValidator.minimumLength(6));
        form.add(newPw);

        final PasswordTextField newPw2 = new PasswordTextField("newPasswordConfirmation");
        newPw2.setRequired(true);
        newPw2.setResetPassword(true);
        newPw2.add(StringValidator.minimumLength(6));
        form.add(newPw2);
        form.add(new EqualPasswordInputValidator(newPw, newPw2));

        add(form);
    }

    public String getNewPassword() {
        return null;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirmation() {
        return null;
    }

    public void setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }

    public String getOldPassword() {
        return null;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
