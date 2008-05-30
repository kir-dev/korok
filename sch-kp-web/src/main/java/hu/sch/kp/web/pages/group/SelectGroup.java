/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.kp.web.pages.group;

import hu.sch.domain.Csoport;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.session.VirSession;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class SelectGroup extends SecuredPageTemplate {
    @EJB(name="UserManagerBean")
    UserManagerLocal userManager;
    
    public SelectGroup() {
        super();
        add(new FeedbackPanel("feedback"));
        
        List<Csoport> csoportok = 
                getSession().getUser().getCsoportok();
        
        ListView groups = new ListView("groupList",csoportok){
            @Override
            protected void populateItem(ListItem item) {
                final Csoport cs = (Csoport)item.getModelObject();
                Link l = new Link("selectgrouplink"){
                    @Override
                    public void onClick() {
                        ((VirSession)getSession()).setCsoport(cs);
                        if (!continueToOriginalDestination()) {
                            setResponsePage(Index.class);
                        } else {
                            return;
                        }
                    }
                };
                l.add(new Label("groupname", new PropertyModel(cs, "nev")));
                item.add(l);
            }    
        };
        add(groups);
    }
    
}
