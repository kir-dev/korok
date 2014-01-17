package hu.sch.web.profile.confirmation;

import hu.sch.domain.config.Configuration;
import hu.sch.domain.user.LostPasswordToken;
import hu.sch.services.AccountManager;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.web.error.NotFound;
import hu.sch.web.profile.ProfilePage;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * It provides a form where the user can change his lost password. After he used
 * the lost password feature he can access this page with his
 * {@link LostPasswordToken#token}.
 *
 * @author balo
 */
public class ReplaceLostPasswordPage extends ProfilePage {

    private static final String PAGE_PARAM_TOKEN = "token";
    //
    @Inject
    protected AccountManager accountManager;
    private final String lostPasswordCode;
    private boolean showPasswordPanel = false;

    public ReplaceLostPasswordPage() {
        throw new RestartResponseException(NotFound.class);
    }

    public ReplaceLostPasswordPage(final PageParameters params) {
        setStatelessHint(true);
        setHeaderLabelText(getString("headerLabel"));

        lostPasswordCode = params.get(PAGE_PARAM_TOKEN).toString();

        if (StringUtils.isBlank(lostPasswordCode)) {
            error(getString("error.missingcode"));
        } else {
            try {
                accountManager.getUserByLostPasswordToken(lostPasswordCode);
                showPasswordPanel = true;
            } catch (PekEJBException ex) {
                parametrizedErrorMessage(ex, getSupportUrl());
            }
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new NewPasswordFormPanel("pwFormPanel") {

            @Override
            public void onPanelSubmit() {
                if (replacePassword(getPassword())) {
                    //hide the form if the confirmation was successful
                    setVisible(false);
                }
            }
        }.setVisible(showPasswordPanel));
    }

    private boolean replacePassword(final String password) {
        try {
            accountManager.replaceLostPassword(lostPasswordCode, password);
            info(String.format(getString("replacepassword.success"),
                    Configuration.getInstance().getProfileDomain()));

            return true;
        } catch (PekEJBException ex) {
            parametrizedErrorMessage(ex);
        }

        return false;
    }

}
