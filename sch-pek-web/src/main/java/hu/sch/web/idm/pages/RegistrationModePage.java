package hu.sch.web.idm.pages;

import hu.sch.web.kp.KorokPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrationModePage extends KorokPage {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationModePage.class);

    public RegistrationModePage() {
        setHeaderLabelText("Regisztráció");
        redirectToHomePageIfLoggedIn();
        redirectToHomePageIfNotAuthenticated();

        addNewUserLink();
        addLoginLink();
        addForgotLink("forgotPassLink", CredentialReminderType.PASSWORD);
        addForgotLink("forgotUserLink", CredentialReminderType.USERNAME);
    }

    @Override
    protected boolean needsLogin() {
        return false;
    }

    // visist page directly, did not go through oauth
    private void redirectToHomePageIfNotAuthenticated() {
        if (getSession().getAccessToken() == null) {
            logger.warn("Attempt to register without passing the oauth flow");
            throw new RestartResponseException(getApplication().getHomePage());
        }
    }

    private void redirectToHomePageIfLoggedIn() {
        if (getAuthorizationComponent().isLoggedIn(getRequest())) {
            getSession().error(getString("err.alreadySignedIn"));
            throw new RestartResponseException(getApplication().getHomePage());
        }
    }

    private void addNewUserLink() {
        Link link = new Link("newUserLink") {

            @Override
            public void onClick() {
                setResponsePage(RegistrationPage.class);
            }
        };

        add(link);
    }

    private void addForgotLink(String id, final CredentialReminderType credType) {
        add(new Link(id) {

            @Override
            public void onClick() {
                setResponsePage(CredentialsReminder.class,
                        new PageParameters().add(CredentialsReminder.PAGE_PARAM, credType.lowercase()));
            }
        });
    }

    private void addLoginLink() {
        add(new Link("loginLink") {

            @Override
            public void onClick() {
                setResponsePage(LinkExistingUserPage.class);
            }
        });
    }

}
