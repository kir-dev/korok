/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import hu.sch.domain.Membership;
import hu.sch.web.kp.pages.group.ShowGroup;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author aldaris
 */
public final class AdminOldBoysPanel extends Panel {

    private static Logger log = Logger.getLogger(AdminOldBoysPanel.class);

    public AdminOldBoysPanel(String id, final List<Membership> inactiveMembers) {
        super(id);
        add(new EditEntitlementsForm("oldForm", inactiveMembers) {

            @Override
            public void onPopulateItem(ListItem<ExtendedGroup> item, Membership ms) {
                item.add(DateLabel.forDatePattern("membership.start", "yyyy.MM.dd."));
                item.add(DateLabel.forDatePattern("membership.end", "yyyy.MM.dd."));
            }

            @Override
            public void onSubmit() {
                try {
                    for (ExtendedGroup extendedGroup : getLines()) {
                        Membership ms = extendedGroup.getMembership();
                        if (extendedGroup.getSelected()) {
                            userManager.setOldBoyToActive(ms);
                        }
                    }
                    getSession().info("A változások sikeresen mentésre kerültek");
                } catch (Exception ex) {
                    getSession().error("Hiba történt a feldolgozás közben");
                    log.warn("Hiba történt az öregtag visszaállításakor", ex);
                }
                setResponsePage(ShowGroup.class, new PageParameters("id=" + inactiveMembers.get(0).getGroup().getId()));
            }
        });
    }
}
