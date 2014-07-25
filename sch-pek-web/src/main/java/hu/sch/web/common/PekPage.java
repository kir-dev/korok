package hu.sch.web.common;

import hu.sch.domain.user.User;
import hu.sch.services.Role;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.config.Configuration;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.web.PhoenixApplication;
import hu.sch.web.authz.OAuthSignInFlow;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.session.VirSession;
import hu.sch.web.wicket.components.choosers.GoogleAnalyticsScript;
import javax.inject.Inject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author messo
 * @since 2.4
 */
public abstract class PekPage extends WebPage {

    private static final String NAVBAR_SCRIPT =
            "var navbarConf = { "
            + "logoutLink: '/logout', "
            + "theme: 'blue', "
            + "width: 900, "
            + "support: %d, "
            + "helpMenuItems: ["
            + "{"
            + "title: 'FAQ',"
            + "url: 'https://kir-dev.sch.bme.hu/kozossegi-pontozas/'"
            + "}"
            + "]"
            + "}; "
            + "printNavbar(navbarConf);";
    protected final int DEFAULT_SUPPORT_ID;
    private Label titleLabel;
    private Label navbarScript;
    private Label headerLabel;
    @Inject
    protected UserManagerLocal userManager;
    @Inject
    protected Configuration config;
    private User user;

    public PekPage() {
        DEFAULT_SUPPORT_ID = config.getSupportDefaultId();

        // TODO: ignore pages that does not need authentication
        if (!getAuthorizationComponent().isLoggedIn(getRequest())) {
            new OAuthSignInFlow(config.getOAuthCredentials()).start();
        }

        init();
    }

    private void init() {
        add(titleLabel = new Label("title", getTitle()));
        add(navbarScript = new Label("navbarScript"));
        createNavbarWithSupportId(DEFAULT_SUPPORT_ID);
        navbarScript.setEscapeModelStrings(false); // do not HTML escape JavaScript code

        add(new WebComponent("css").add(
                new AttributeModifier("href", new Model<String>("/css/" + getCss()))));
        add(new WebComponent("favicon").add(
                new AttributeModifier("href", new Model<String>("/images/" + getFavicon()))));

        User user = getCurrentUser();
        if (user != null && user.isShowRecommendedPhoto()) {
            // javasoljunk neki egy fotót
            add(new RecommendedPhotoPanel("recommendPhoto", getRemoteUser(), getCurrentUser()));
        } else {
            add(new EmptyPanel("recommendPhoto").setVisible(false));
        }

        add(getHeaderPanel("headerPanel"));
        add(headerLabel = new Label("headerLabel", new Model<String>("")));
        add(new FeedbackPanel("pagemessages").setEscapeModelStrings(false));
        add(new GoogleAnalyticsScript("analyticsJs"));
    }

    /**
     * Beállítjuk az adott lapon a &lt;title/&gt;-t, a "VIR Körök - " előtaggal
     *
     * @param title a cím, amit a "VIR Körök - " után szerepel
     * @since 2.4
     */
    protected void setTitleText(String title) {
        titleLabel.setDefaultModelObject(getTitle() + " - " + title);
    }

    protected abstract String getTitle();

    protected void setHeaderLabelText(String text) {
        headerLabel.setDefaultModelObject(text);
    }

    protected abstract String getCss();

    protected abstract String getFavicon();

    protected abstract Panel getHeaderPanel(String id);

    protected final void createNavbarWithSupportId(int supportId) {
        navbarScript.setDefaultModel(
                new Model<String>(NAVBAR_SCRIPT.replace("%d", Integer.toString(supportId))));
        navbarScript.setVisible(getApplication().usesDeploymentConfig());
    }

    protected final boolean isCurrentUserAdmin() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), Role.ADMIN);
    }

    protected final boolean isCurrentUserJETI() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), Role.JETI);
    }

    protected final boolean isCurrentUserSVIE() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), Role.SVIE);
    }

    protected UserAuthorization getAuthorizationComponent() {
        return ((PhoenixApplication) getApplication()).getAuthorizationComponent();
    }

    @Override
    public VirSession getSession() {
        return (VirSession) super.getSession();
    }

    protected String getRemoteUser() {
        return getAuthorizationComponent().getRemoteUser(getRequest());
    }

    protected final User getCurrentUser() {
        if (user == null) {
            user = getAuthorizationComponent().getCurrentUser(getRequest());
        }
        return user;
    }

    protected final Long getCurrentUserId() {
        return getAuthorizationComponent().getCurrentUserId(getRequest());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        if (getApplication().usesDeploymentConfig()) {
            response.render(JavaScriptHeaderItem.forUrl(getString("navbar.url")));
        }
    }

    /**
     * Call {@link #error(java.io.Serializable)} method with the messageKey and
     * parameters of the exception.
     *
     * @param ex
     * @param moreParams you can add more parameters to be replaced
     */
    protected void parametrizedErrorMessage(final PekEJBException ex, Object... moreParams) {
        error(String.format(
                getString(ex.getErrorCode().getMessageKey()),
                ArrayUtils.addAll(ex.getParameters(), moreParams)));
    }

    /**
     * Returns the full basic 'Profil és Körök' support url.
     *
     * @return the full url with the protocol and the support target id
     */
    protected String getSupportUrl() {
        return config.getSupportBaseUrl()+ DEFAULT_SUPPORT_ID;
    }
}
