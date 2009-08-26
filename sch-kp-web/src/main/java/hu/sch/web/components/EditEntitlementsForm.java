/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import hu.sch.web.components.customlinks.UserLink;
import hu.sch.domain.Membership;
import hu.sch.services.UserManagerLocal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
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
public abstract class EditEntitlementsForm extends Form {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    private List<ExtendedGroup> lines = new ArrayList<ExtendedGroup>();

    public EditEntitlementsForm(String name, List<Membership> active) {
        super(name);
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
                item.add(new Label("membership.user.nickName"));
                item.add(new CheckBox("check", new PropertyModel<Boolean>(ext, "selected")));
                onPopulateItem(item, ms);
            }
        };
        members.setReuseItems(true);
        table.add(members);
        add(table);
    }

    public List<ExtendedGroup> getLines() {
        return lines;
    }

    protected abstract void onPopulateItem(ListItem<ExtendedGroup> item, Membership ms);

    @Override
    protected abstract void onSubmit();

    protected class ExtendedGroup implements Serializable {

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
