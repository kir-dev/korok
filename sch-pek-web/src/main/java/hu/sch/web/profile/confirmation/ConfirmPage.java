package hu.sch.web.profile.confirmation;

import hu.sch.domain.user.User;
import hu.sch.services.AccountManager;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.web.error.NotFound;
import hu.sch.web.profile.ProfilePage;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Confirms a user account. If the user hasn't got a password he can set it on
 * this page.
 *
 * @author konvergal
 * @author tomi
 * @author balo
 */
public final class ConfirmPage extends ProfilePage {

    //
    private static final String PAGE_PARAM_CODE = "code";
    //
    @Inject
    protected AccountManager accountManager;
    private User user = null;
    private boolean showPasswordPanel = false;

    public ConfirmPage() {
        throw new RestartResponseException(NotFound.class);
    }

    public ConfirmPage(final PageParameters params) {
        setStatelessHint(true);
        setHeaderLabelText(getString("headerLabel"));

        final String confirmationCode = params.get(PAGE_PARAM_CODE).toString("");

        if (StringUtils.isBlank(confirmationCode)) {
            error(getString("error.missingcode"));
        } else {
            prepareConfirmation(confirmationCode);
        }
    }

    private void prepareConfirmation(final String confirmationCode) {
        user = userManager.findUserByConfirmationCode(confirmationCode);
        if (user == null) {
            error(getString("error.wrongcode"));
            return;
        }

        if (StringUtils.isBlank(user.getPasswordDigest())) {
            //user has to set his password
            showPasswordPanel = true;
        } else {
            // user has password -> just confirm, nothing else to do
            confirm(null);
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new NewPasswordFormPanel("pwFormPanel") {

            @Override
            public void onPanelSubmit() {
                if (confirm(getPassword())) {
                    //hide the form if the confirmation was successful
                    setVisible(false);
                }
            }
        }.setVisible(showPasswordPanel));
    }

    private boolean confirm(final String password) {
        try {
            accountManager.confirm(user, password);
            info(String.format(getString("confirm.success"),
                    config.getProfileDomain()));

            return true;
        } catch (PekEJBException ex) {
            error(getString("confirm.failed"));
        }

        return false;
    }

    @Override
    protected boolean needsLogin() {
        return false;
    }
}
