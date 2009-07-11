/*
 *  Copyright 2008 Adam Lantos.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package hu.sch.web.profile.pages.passwordchange;

import hu.sch.services.exceptions.InvalidPasswordException;
import hu.sch.web.profile.pages.template.ProfilePage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator;

/**
 *
 * @author Adam Lantos
 */
public class ChangePasswordPage extends ProfilePage {

    private String oldPassword;
    private String newPassword;
    //FIXME: currently not used
    @SuppressWarnings("unused")
    private String newPasswordConfirmation;
    private PasswordTextField oldPw;
    private PasswordTextField newPw;
    private PasswordTextField newPw2;

    public ChangePasswordPage() {
        setHeaderLabelText("Jelszóváltoztatás");
        add(new FeedbackPanel("feedbackPanel"));
        Form form = new Form("changePasswordForm",
                new CompoundPropertyModel(this)) {

            @Override
            protected void onSubmit() {
                try {
                    ldapManager.changePassword(getUid(),
                            oldPassword, newPassword);
                    getSession().info("Sikeres jelszóváltoztatás");
                } catch (InvalidPasswordException ex) {
                    getSession().error("Hibás jelszó");
                }
            }
        };
        oldPw = new PasswordTextField("oldPassword");
        oldPw.setRequired(true);
        oldPw.setResetPassword(true);
        form.add(oldPw);
        newPw = new PasswordTextField("newPassword");
        newPw.setRequired(true);
        newPw.setResetPassword(true);
        newPw.add(StringValidator.minimumLength(6));
        form.add(newPw);
        newPw2 = new PasswordTextField("newPasswordConfirmation");
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
