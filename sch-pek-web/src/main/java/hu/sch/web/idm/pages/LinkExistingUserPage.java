package hu.sch.web.idm.pages;

import hu.sch.domain.user.User;
import hu.sch.services.AccountManager;
import hu.sch.services.AuthSchUserIntegration;
import hu.sch.services.dto.OAuthUserInfo;
import hu.sch.web.kp.KorokPage;
import javax.inject.Inject;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

public class LinkExistingUserPage extends KorokPage {

    @Inject
    private AccountManager accountManager;

    @Inject
    private AuthSchUserIntegration userIntegration;

    private Credentials credentials;
    private Form<Credentials> loginForm;

    public LinkExistingUserPage() {
        credentials = new Credentials();
        loginForm = createForm();
        add(loginForm);
    }

    @Override
    protected boolean needsLogin() {
        return false;
    }

    private Form<Credentials> createForm() {
        Form<Credentials> loginForm = new Form<Credentials>("loginForm", new CompoundPropertyModel<>(credentials)) {

            @Override
            protected void onSubmit() {
                onAuthenticate();
            }

        };
        RequiredTextField usernameTF = new RequiredTextField("username");
        usernameTF.setLabel(Model.of("felhasználónév"));

        PasswordTextField passwordTF = new PasswordTextField("password");
        passwordTF.setLabel(Model.of("jelszó"));

        loginForm.add(usernameTF);
        loginForm.add(passwordTF);

        return loginForm;
    }

    private void onAuthenticate() {
        if (accountManager.authenticate(credentials.getUsername(), credentials.getPassword())) {
            updateAndSignIn();

            getSession().info("Sikeresen összekapcsoltuk az VIR fiókodat.");
            setResponsePage(getApplication().getHomePage());
        } else {
            loginForm.error("A felhasználóneved vagy jelszavad helytelen.");
        }
    }

    private void updateAndSignIn() {
        OAuthUserInfo userInfo = getSession().getOAuthUserInfo();

        User user = userManager.findUserByScreenName(credentials.getUsername());
        userIntegration.updateUser(user.getId(), userInfo);

        getSession().setUserId(user.getId());
        getSession().setOAuthUserInfo(null);
    }
}
