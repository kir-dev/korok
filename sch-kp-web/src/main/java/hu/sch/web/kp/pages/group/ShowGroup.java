/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.MembershipType;
import hu.sch.domain.User;
import hu.sch.web.components.ActiveMembershipsPanel;
import hu.sch.web.components.AdminMembershipsPanel;
import hu.sch.web.components.AdminOldBoysPanel;
import hu.sch.web.components.OldBoysPanel;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class ShowGroup extends SecuredPageTemplate {

    public ShowGroup(PageParameters parameters) {
        Object p = parameters.get("id");
        Long id = null;

        try {
            id = Long.parseLong(p.toString());
        } catch (NumberFormatException e) {
            error("Hibás paraméter");
            throw new RestartResponseException(GroupHierarchy.class);
        }

        Group g = userManager.findGroupWithCsoporttagsagokById(id);
        User user = userManager.findUserWithCsoporttagsagokById(getSession().getUserId());
        if (g == null) {
            getSession().info("Ilyen kör nem létezik");
            throw new RestartResponseException(GroupHierarchy.class);
        }
        add(new FeedbackPanel("pagemessages"));
        if (g.getName().contains("Informatikus-hallgatók")) {
            setHeaderLabelText("MAVE adatlapja");
        } else {
            setHeaderLabelText(g.getName());
        }
        add(new BookmarkablePageLink("detailView", GroupHistory.class,
                new PageParameters("id=" + g.getId().toString())));
        if (user != null && hasUserRoleInGroup(g, MembershipType.KORVEZETO)) {
            add(new BookmarkablePageLink("editPage", EditGroupInfo.class,
                    new PageParameters("id=" + g.getId().toString())).setVisible(true));
        } else {
            add(new BookmarkablePageLink("editPage", ShowUser.class).setVisible(false));
        }

        setDefaultModel(new CompoundPropertyModel(g));
        add(new Label("name"));
        add(new Label("founded"));
        add(new SmartLinkLabel("webPage"));
        add(new SmartLinkLabel("mailingList"));
        add(new MultiLineLabel("introduction"));
        g.sortMemberships();
        List<Membership> activeMembers = g.getActiveMemberships();
        List<Membership> inactiveMembers = g.getInactiveMemberships();

        activeMembers.removeAll(inactiveMembers);
        AdminMembershipsPanel adminPanel = new AdminMembershipsPanel("admin", activeMembers);
        ActiveMembershipsPanel activePanel = new ActiveMembershipsPanel("user", activeMembers);
        AdminOldBoysPanel adminOldPanel = new AdminOldBoysPanel("oldAdmin", inactiveMembers);
        OldBoysPanel inactivePanel = new OldBoysPanel("oldUser", inactiveMembers);
        add(adminPanel);
        add(adminOldPanel);
        add(activePanel);
        add(inactivePanel);
        if (user != null && hasUserRoleInGroup(g, MembershipType.KORVEZETO)) {
            activePanel.setVisible(false);
            inactivePanel.setVisible(false);
        } else {
            adminPanel.setVisible(false);
            adminOldPanel.setVisible(false);
        }
    }
}
