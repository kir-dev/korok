package hu.sch.web.common;

import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import hu.sch.web.PhoenixApplication;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.session.VirSession;
import hu.sch.web.wicket.components.choosers.GoogleAnalyticsScript;
import javax.ejb.EJB;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author messo
 * @since 2.4
 */
public abstract class PekPage extends WebPage {

    private static final Logger logger = LoggerFactory.getLogger(PekPage.class);
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
    private Label titleLabel;
    private Label navbarScript;
    private Label headerLabel;
    @EJB(name = "UserManagerBean")
    protected UserManagerLocal userManager;

    public PekPage() {
        loadUser();
        init();
    }

    private void init() {
        add(titleLabel = new Label("title", getTitle()));
        add(navbarScript = new Label("navbarScript"));
        createNavbarWithSupportId(32);
        navbarScript.setEscapeModelStrings(false); // do not HTML escape JavaScript code

        add(new WebComponent("css").add(
                new AttributeModifier("href", new Model<String>("/css/" + getCss()))));
        add(new WebComponent("favicon").add(
                new AttributeModifier("href", new Model<String>("/images/" + getFavicon()))));

        User user = getUser();
        if (user != null && user.isShowRecommendedPhoto()) {
            // javasoljunk neki egy fotót
            add(new RecommendedPhotoPanel("recommendPhoto", getRemoteUser(), getUser()));
        } else {
            add(new EmptyPanel("recommendPhoto").setVisible(false));
        }

        add(getHeaderPanel("headerPanel"));
        add(headerLabel = new Label("headerLabel", new Model<String>("")));
        add(new FeedbackPanel("pagemessages").setEscapeModelStrings(false));
        add(new GoogleAnalyticsScript("analyticsJs"));
    }

    private void loadUser() {
        Long virId = getAuthorizationComponent().getUserid(getRequest());
        if (virId == null) {
            // nincs virId, ilyenkor userId := 0?
            getSession().setUserId(0L);
            return;
        } else if (!virId.equals(getSession().getUserId())) {
            // TODO: ilyenkor mi van? egyelőre beállítjuk a session ben is
            getSession().setUserId(virId);
        }
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
        return getAuthorizationComponent().hasAbstractRole(getRequest(), "ADMIN");
    }

    protected final boolean isCurrentUserJETI() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), "JETI");
    }

    protected final boolean isCurrentUserSVIE() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), "SVIE");
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

    protected final User getUser() {
        return userManager.findUserById(getSession().getUserId(), true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        if (getApplication().usesDeploymentConfig()) {
            response.render(JavaScriptHeaderItem.forUrl(getString("navbar.url")));
        }
    }
}
