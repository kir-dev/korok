/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

import hu.sch.domain.Csoporttagsag;
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

    public OldBoysPanel(String id, List<Csoporttagsag> inactiveMembers) {
        super(id);
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
