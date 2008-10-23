/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.group;

import hu.sch.domain.Csoport;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class ShowGroup extends SecuredPageTemplate {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public ShowGroup(PageParameters parameters) {
        Object p = parameters.get("id");
        try {
            Long id = Long.parseLong(p.toString());
            Csoport cs = userManager.findGroupById(id);
            setModel(new CompoundPropertyModel(cs));
            add(new Label("nev"));
            add(new Label("alapitasEve"));
            add(new Label("webpage"));
            add(new Label("levelezoLista"));
            add(new MultiLineLabel("leiras"));
            
        } catch (NumberFormatException e) {
            setResponsePage(Index.class);
        }
    }
}
