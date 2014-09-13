package hu.sch.web.kp.logout;

import hu.sch.web.kp.KorokPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.flow.RedirectToUrlException;

/**
 *
 * @author aldaris
 */
public class Logout extends KorokPage {

    public Logout() {
        setHeaderLabelText("Kijelentkezés");

        add(new Link("pek-logout-link") {

            @Override
            public void onClick() {
                logout();
                setResponsePage(getApplication().getHomePage());
            }
        });

        add(new Link("auth-sch-logout-link") {

            @Override
            public void onClick() {
                logout();
                throw new RedirectToUrlException(getAuthSchLogoutUrl());
            }
        });
    }

    private void logout() {
        getSession().invalidate();
        info("Sikeres kijelentkezés. :)");

    }

    private String getAuthSchLogoutUrl() {
        String pekUrl = "http://" + config.getKorokDomain();
        return "https://auth.sch.bme.hu/site/logout?redirect=".concat(pekUrl);
    }

    @Override
    protected boolean needsLogin() {
        return false;
    }
}
