/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.group;

import hu.sch.kp.web.templates.SecuredPageTemplate;
import org.apache.wicket.PageParameters;

/**
 *
 * @author hege
 */
public class ShowGroup extends SecuredPageTemplate {
    Long id;
    
    public ShowGroup(PageParameters parameters) {
        try {
            Object p = parameters.get("id");
            if (p != null) {
                id = (Long) p;
            }
        } catch (Throwable t) {
        }
    }
}
