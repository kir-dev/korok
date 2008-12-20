/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.group;

import hu.sch.domain.Csoport;
import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.Felhasznalo;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.session.VirSession;
import hu.sch.kp.web.templates.SecuredPageTemplate;
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

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    Long id;

    public SelectGroup() {
        super();
        add(new FeedbackPanel("feedback"));

        /*        List<Csoport> csoportok = 
        getSession().getUser().getCsoportok();*/

        if (id == null) {
            id = ((VirSession) getSession()).getUser().getId();
        }
        if (id == null) {
            setResponsePage(Index.class);
            return;
        }

        Felhasznalo user = userManager.findUserWithCsoporttagsagokById(id);
        user.sortCsoporttagsagok();


        //ListView groups = new ListView("groupList",csoportok){
        ListView groups = new ListView("groupList", user.getCsoporttagsagok()) {

            @Override
            protected void populateItem(ListItem item) {
                final Csoport cs = ((Csoporttagsag) item.getModelObject()).getCsoport();
                Link l = new Link("selectgrouplink") {

                    @Override
                    public void onClick() {
                        ((VirSession) getSession()).setCsoport(cs);
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
