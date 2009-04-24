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
public final class AdminMembershipsPanel extends Panel {

    public AdminMembershipsPanel(String id, List<Csoporttagsag> activeMembers) {
        super(id);
        add(new EditEntitlementsForm("form", activeMembers));
    }
}
