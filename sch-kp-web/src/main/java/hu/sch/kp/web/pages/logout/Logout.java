/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.logout;

import hu.sch.kp.web.templates.SecuredPageTemplate;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 *
 * @author aldaris
 */
public class Logout extends SecuredPageTemplate {

    public Logout() {
        setHeaderLabelText("Kijelentkezés");
        add(new FeedbackPanel("pagemessages"));

        info("Sikeres kijelentkezés. :)");
    }
}