/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.group;

import hu.sch.domain.Csoport;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class ShowGroup extends SecuredPageTemplate {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public ShowGroup(PageParameters parameters) {
        Object p = parameters.get("id");
        Long id = (Long) p;
        Csoport cs = userManager.findGroupById(id);
        add(new Label("groupName", new PropertyModel(cs, "nev")));
    }
}
