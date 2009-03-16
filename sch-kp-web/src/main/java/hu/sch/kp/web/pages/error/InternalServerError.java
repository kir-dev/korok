/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.error;

import hu.sch.kp.web.templates.SecuredPageTemplate;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;

/**
 *
 * @author aldaris
 */
public final class InternalServerError extends SecuredPageTemplate {

    public InternalServerError() {
        super();
        setHeaderLabelText("Hiba!");
        SmartLinkLabel mailtoLink = new SmartLinkLabel("mail", "kir-dev@sch.bme.hu");
        add(mailtoLink);
    }
}

