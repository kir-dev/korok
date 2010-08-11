/**
 * Copyright (c) 2008-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package hu.sch.web.profile.passwordchange;

import hu.sch.services.exceptions.InvalidPasswordException;
import hu.sch.web.profile.ProfilePage;
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
        Form form = new Form("changePasswordForm",
                new CompoundPropertyModel(this)) {

            @Override
            protected void onSubmit() {
                try {
                    ldapManager.changePassword(getRemoteUser(),
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
