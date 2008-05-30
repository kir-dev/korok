/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.index;

import hu.sch.kp.services.SystemManagerLocal;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.session.VirSession;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;

/**
 *
 * @author hege
 */
public class Index extends SecuredPageTemplate {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    
    @EJB(name = "SystemManagerBean")
    SystemManagerLocal systemManager;

    public Index(PageParameters params) {
        super();
        try {
            Long userId = params.getLong("userId");
            if (userId == null || userId.equals(0)) {
                error(getLocalizer().getString("nincsUserId", this));
                return;
            }
            ((VirSession) getSession()).setUser(userManager.findUserById(userId));
        } catch (Exception e) {
        }
        
    }
}
