/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.group;

import hu.sch.domain.Csoport;
import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.web.components.ActiveMembershipsPanel;
import hu.sch.kp.web.components.AdminMembershipsPanel;
import hu.sch.kp.web.components.FelhasznaloLink;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.pages.user.ShowUser;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class ShowGroup extends SecuredPageTemplate {

//    @EJB(name = "LDAPPersonManagerBean")
//    LdapPersonManagerLocal ldapManager;
    public ShowGroup(PageParameters parameters) {
        Object p = parameters.get("id");
        Long id = null;

        try {
            id = Long.parseLong(p.toString());
        } catch (NumberFormatException e) {
            setResponsePage(Index.class);
        }

        Csoport cs = userManager.findGroupWithCsoporttagsagokById(id);
        Felhasznalo user = userManager.findUserWithCsoporttagsagokById(getSession().getUser().getId());
        if (cs == null) {
            info("Ilyen kör nem létezik");
            setResponsePage(GroupHierarchy.class);
            return;
        }
        add(new FeedbackPanel("pagemessages"));
        if (cs.getNev().contains("Informatikus-hallgatók")) {
            setHeaderLabelText("MAVE adatlapja");
        } else {
            setHeaderLabelText(cs.getNev() + " adatlapja");
        }
        add(new BookmarkablePageLink("detailView", GroupHistory.class,
                new PageParameters("id=" + cs.getId().toString())));
        if (user != null && hasUserRoleInGroup(cs, TagsagTipus.KORVEZETO)) {
            add(new BookmarkablePageLink("editPage", EditGroupInfo.class,
                    new PageParameters("id=" + cs.getId().toString())).setVisible(true));
        } else {
            add(new BookmarkablePageLink("editPage", ShowUser.class).setVisible(false));
        }

        setModel(new CompoundPropertyModel(cs));
        add(new Label("nev"));
        add(new Label("alapitasEve"));
        add(new SmartLinkLabel("webpage"));
        add(new SmartLinkLabel("levelezoLista"));
        add(new MultiLineLabel("leiras"));
        cs.sortCsoporttagsagok();
        List<Csoporttagsag> activeMembers = cs.getActiveMembers();
        List<Csoporttagsag> inactiveMembers = cs.getInactiveMembers();

        activeMembers.removeAll(inactiveMembers);
        AdminMembershipsPanel adminPanel = new AdminMembershipsPanel("admin", activeMembers);
        ActiveMembershipsPanel activePanel = new ActiveMembershipsPanel("user", activeMembers);
        add(adminPanel);
        add(activePanel);
        if (user != null && hasUserRoleInGroup(cs, TagsagTipus.KORVEZETO)) {
            activePanel.setVisible(false);
        } else {
            adminPanel.setVisible(false);
        }

        ListView oregtagsagok = new ListView("oregtagsag", inactiveMembers) {

            @Override
            protected void populateItem(ListItem item) {
                Csoporttagsag cs = (Csoporttagsag) item.getModelObject();
                item.setModel(new CompoundPropertyModel(cs));
                item.add(new FelhasznaloLink("felhlink", cs.getFelhasznalo()));
                item.add(new Label("becenev", cs.getFelhasznalo().getBecenev()));
                item.add(DateLabel.forDatePattern("kezdet", "yyyy.MM.dd."));
                item.add(DateLabel.forDatePattern("veg", "yyyy.MM.dd."));
            }
        };
        add(oregtagsagok);
    }
}
