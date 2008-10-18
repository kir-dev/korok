/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.user;

import hu.sch.domain.BelepoIgeny;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.PontIgeny;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.session.VirSession;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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

        Felhasznalo user = userManager.findUserById(id);
        setModel(new CompoundPropertyModel(user));
        add(new Label("nev"));
        
        List<PontIgeny> pontIgenyek = userManager.getPontIgenyekForUser(user);
        ListView plv = new ListView("pontigeny", pontIgenyek) {
            @Override
            protected void populateItem(ListItem item) {
                item.setModel(new CompoundPropertyModel(item.getModelObject()));
                item.add(new Label("ertekeles.szemeszter"));
                item.add(new Label("ertekeles.csoport.nev"));
                item.add(new Label("pont"));
            }
        };
        add(plv);
        
        List<BelepoIgeny> belepoIgenyek = userManager.getBelepoIgenyekForUser(user);
        ListView blv = new ListView("belepoigeny", belepoIgenyek) {
            @Override
            protected void populateItem(ListItem item) {
                item.setModel(new CompoundPropertyModel(item.getModelObject()));
                item.add(new Label("ertekeles.szemeszter"));
                item.add(new Label("ertekeles.csoport.nev"));
                item.add(new Label("belepotipus"));
                item.add(new Label("szovegesErtekeles"));
            }
        };
        add(blv);
        

    }

    public ShowUser(PageParameters parameters) {
        try {
            id = parameters.getLong("id");
        } catch (Throwable t) {
            t.printStackTrace();
        }

        initComponents();
    }
}
