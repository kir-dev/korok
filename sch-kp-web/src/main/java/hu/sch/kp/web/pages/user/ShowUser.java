/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.user;

import hu.sch.domain.Felhasznalo;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.session.VirSession;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 *
 * @author hege
 */
public class ShowUser extends SecuredPageTemplate {

    @EJB(name = "ejb/UserManagerLocal") UserManagerLocal userManager;
    Long id;

    public ShowUser() {
        initComponents();
    }
    
    public void initComponents() {
        if (id == null) {
            id = ((VirSession) getSession()).getUser().getId();
        }
        if (id == null) {
            error(new ResourceModel("message.noUserId"));

            return;
        }

        /*Felhasznalo user = userManager.findUserById(id);
        setModel(new CompoundPropertyModel(user));*/
        add(new TextField("name"));
        add(new TextField("loginName"));
        add(new TextField("emailAddress"));

    }

    public ShowUser(PageParameters parameters) {
        try {
            Object p = parameters.get("id");
            if (p != null) {
                id = (Long) p;
            }
        } catch (Throwable t) {
        }

        initComponents();
    }
}
