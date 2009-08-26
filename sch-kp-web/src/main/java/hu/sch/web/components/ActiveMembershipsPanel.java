/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import hu.sch.web.components.customlinks.UserLink;
import hu.sch.domain.Membership;
import java.util.List;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author aldaris
 */
public final class ActiveMembershipsPanel extends Panel {

    public ActiveMembershipsPanel(String id, List<Membership> activeMembers) {
        super(id);
        ListView<Membership> membershipsList = new ListView<Membership>("memberships", activeMembers) {

            @Override
            protected void populateItem(ListItem<Membership> item) {
                Membership ms = item.getModelObject();
                item.setModel(new CompoundPropertyModel<Membership>(ms));
                item.add(new UserLink("userLink", ms.getUser()));
                item.add(new Label("user.nickName"));
                item.add(new Label("rights",
                        getConverter(List.class).convertToString(ms.getPosts(), getLocale())));
                item.add(DateLabel.forDatePattern("start", "yyyy.MM.dd."));
            }
        };
        add(membershipsList);
    }
}
