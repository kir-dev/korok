/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import hu.sch.domain.Membership;
import hu.sch.domain.MembershipType;
import hu.sch.web.kp.pages.group.ShowGroup;
import hu.sch.services.UserManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public class EditEntitlementsForm extends Form {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    private List<ExtendedGroup> lines = new ArrayList<ExtendedGroup>();
    private final boolean activePanel;

    public EditEntitlementsForm(String name, List<Membership> active, boolean isActivePanel) {
        super(name);
        activePanel = isActivePanel;
        for (Membership ms : active) {
            lines.add(new ExtendedGroup(ms));
        }

        WebMarkupContainer table = new WebMarkupContainer("table");
        ListView<ExtendedGroup> members = new ListView<ExtendedGroup>("members", lines) {

            @Override
            protected void populateItem(ListItem<ExtendedGroup> item) {
                ExtendedGroup ext = item.getModelObject();
                Membership ms = ext.getMembership();
                item.setModel(new CompoundPropertyModel<ExtendedGroup>(ext));
                item.add(new UserLink("userLink", ms.getUser()));
                item.add(new Label("nickName", ms.getUser().getNickName()));
                if (activePanel) {
                    item.add(new Label("rights",
                            getConverter(MembershipType[].class).convertToString(ms.getRightsAsString(), getLocale())));
                    item.add(new ChangePostLink("postLink", ext.getMembership()));
                } else {
                    item.add(DateLabel.forDatePattern("membership.start", "yyyy.MM.dd."));
                    item.add(DateLabel.forDatePattern("membership.end", "yyyy.MM.dd."));
                }
                item.add(new CheckBox("check", new PropertyModel<Boolean>(ext, "selected")));
            }
        };
        members.setReuseItems(true);
        table.add(members);
        add(table);
    }

    @Override
    public void onSubmit() {
        try {
            for (ExtendedGroup extendedGroup : lines) {
                Membership ms = extendedGroup.getMembership();
                if (extendedGroup.getSelected()) {
                    if (activePanel) {
                        if (ms.getRights() == 0 || ms.getRights() == 2) {
                            userManager.setMemberToOldBoy(ms);
                        } else {
                            getSession().error("Jogokkal rendelkező tagot nem lehet törölni");
                        }
                    } else {
                        userManager.setOldBoyToActive(ms);
                    }
                }
            }
            getSession().info("A változások sikeresen mentésre kerültek");
        } catch (Exception ex) {
            getSession().error("Hiba történt a feldolgozás közben");
        }
        //TODO: szebbé tenni
        setResponsePage(ShowGroup.class, new PageParameters("id=" + lines.get(0).getMembership().getGroup().getId()));
        return;
    }

    private class ExtendedGroup implements Serializable {

        private Membership membership;
        private boolean selected;

        public ExtendedGroup(Membership membership) {
            this.membership = membership;
        }

        public Membership getMembership() {
            return membership;
        }

        public void setMembership(Membership membership) {
            this.membership = membership;
        }

        public boolean getSelected() {
            return selected;
        }

        public void setSelected(boolean isSelected) {
            this.selected = isSelected;
        }
    }
}
