/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

import hu.sch.domain.Csoporttagsag;
import java.util.List;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author aldaris
 */
public final class AdminOldBoysPanel extends Panel {

    public AdminOldBoysPanel(String id, List<Csoporttagsag> inactiveMembers) {
        super(id);
        add(new EditEntitlementsForm("oldForm", inactiveMembers, false));
    }
}
