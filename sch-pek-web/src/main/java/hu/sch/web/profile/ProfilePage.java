package hu.sch.web.profile;

import hu.sch.web.common.PekPage;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author messo
 */
public abstract class ProfilePage extends PekPage {

    public ProfilePage() {
    }

    @Override
    protected String getTitle() {
        return "VIR Profil";
    }

    @Override
    protected String getCss() {
        return "profile-style.css";
    }

    @Override
    protected String getFavicon() {
        return "favicon-profil.ico";
    }

    @Override
    protected Panel getHeaderPanel(String id) {
        return new ProfileHeaderPanel(id);
    }
}
