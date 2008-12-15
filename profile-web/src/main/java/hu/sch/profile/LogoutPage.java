package hu.sch.profile;

import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 *
 * @author Adam Lantos
 */
public class LogoutPage extends ProfilePage {

    public LogoutPage() {
        add(new FeedbackPanel("feedbackPanel"));
        if (getUid() == null) {
            info("Sikeres kijelentkezés. :)");
        } else {
            error("Sikertelen kijelentkezés. :(");
        }
    }
}
