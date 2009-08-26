/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import hu.sch.domain.Membership;
import hu.sch.web.components.customlinks.ChangePostLink;
import hu.sch.web.kp.pages.group.ShowGroup;
import hu.sch.web.session.VirSession;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author aldaris
 */
public final class AdminMembershipsPanel extends Panel {

    private static Logger log = Logger.getLogger(AdminMembershipsPanel.class);

    public AdminMembershipsPanel(String id, final List<Membership> activeMembers) {
        super(id);
        add(new EditEntitlementsForm("form", activeMembers) {

            @Override
            public void onPopulateItem(ListItem<ExtendedGroup> item, Membership ms) {
                item.add(new Label("rights",
                        getConverter(List.class).convertToString(ms.getPosts(), getLocale())));
                item.add(new ChangePostLink("postLink", item.getModelObject().getMembership()));
            }

            @Override
            public void onSubmit() {
                try {
                    long myId = ((VirSession) getSession()).getUserId();
                    for (ExtendedGroup extendedGroup : getLines()) {
                        Membership ms = extendedGroup.getMembership();
                        if (extendedGroup.getSelected()) {
                            if (!ms.getUser().getId().equals(myId)) {
                                userManager.setMemberToOldBoy(ms);
                            }
                        }
                    }
                    getSession().info("A változások sikeresen mentésre kerültek");
                } catch (Exception ex) {
                    getSession().error("Hiba történt a feldolgozás közben");
                    log.warn("Hiba történt az öregtaggá avatás közben", ex);
                }
                setResponsePage(ShowGroup.class, new PageParameters("id=" + activeMembers.get(0).getGroup().getId()));
            }
        });
    }
}
