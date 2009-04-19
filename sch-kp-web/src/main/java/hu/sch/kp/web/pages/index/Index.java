/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.index;

import hu.sch.kp.web.templates.SecuredPageTemplate;
import org.apache.wicket.PageParameters;

/**
 *
 * @author hege
 */
public class Index extends SecuredPageTemplate {

    public Index(PageParameters params) {
        super();
        try {
            Long userId = params.getLong("userId");
            if (userId == null || userId.equals(0L)) {
                error(getLocalizer().getString("nincsUserId", this));
                return;
            }
            getSession().setUser(userManager.findUserById(userId));
        } catch (Exception e) {
        }
        
    }
}
