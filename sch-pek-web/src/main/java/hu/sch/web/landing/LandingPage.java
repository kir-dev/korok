package hu.sch.web.landing;

import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.profile.show.ShowPersonPage;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.AbstractCheckSelector;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.cycle.RequestCycle;

public class LandingPage extends KorokPage {


    public LandingPage() {
        setHeaderLabelText("Profil és Körök");
        redirectToHomePageIfLoggedIn();

        addSignInLink();
        addVersionInfoLink();
    }

    private void redirectToHomePageIfLoggedIn() {
        if (getAuthorizationComponent().isLoggedIn(getRequest())) {
            setResponsePage(getHomePage());
        }
    }

    @Override
    protected boolean needsLogin() {
        return false;
    }

    private Class<? extends Page> getHomePage() {
        String url = ((HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest()).getRequestURL().toString();

        if (url.contains("profile")) {
            return ShowPersonPage.class;
        }

        return ShowUser.class;
    }

    private void addSignInLink() {
        add(new Link("signInLink") {

            @Override
            public void onClick() {
                setResponsePage(getHomePage());
            }
        });
    }

    private void addVersionInfoLink() {
        WebMarkupContainer container = new WebMarkupContainer("versionInfoLinkContainer");
        add(container);
        container.setVisible(StringUtils.isNotBlank(config.getVersionInfoLink()));

        container.add(new ExternalLink("versionInfoLink", config.getVersionInfoLink()));
    }




}
