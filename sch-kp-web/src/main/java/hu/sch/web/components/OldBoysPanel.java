/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

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
public final class OldBoysPanel extends Panel {

    public OldBoysPanel(String id, List<Membership> inactiveMembers) {
        super(id);
        ListView oldBoys = new ListView("oldBoy", inactiveMembers) {

            @Override
            protected void populateItem(ListItem item) {
                Membership cs = (Membership) item.getModelObject();
                item.setModel(new CompoundPropertyModel(cs));
                item.add(new UserLink("userLink", cs.getUser()));
                item.add(new Label("nickName", cs.getUser().getNickName()));
                item.add(DateLabel.forDatePattern("start", "yyyy.MM.dd."));
                item.add(DateLabel.forDatePattern("end", "yyyy.MM.dd."));
            }
        };
        add(oldBoys);
    }
}
