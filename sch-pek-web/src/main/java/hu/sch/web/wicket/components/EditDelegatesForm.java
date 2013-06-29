package hu.sch.web.wicket.components;

import hu.sch.web.wicket.components.customlinks.UserLink;
import hu.sch.domain.User;
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
public abstract class EditDelegatesForm extends Form {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    private List<ExtendedUser> lines = new ArrayList<ExtendedUser>();

    public EditDelegatesForm(String name, List<User> users) {
        super(name);
        for (User user : users) {
            lines.add(new ExtendedUser(user));
        }

        WebMarkupContainer table = new WebMarkupContainer("table");
        ListView<ExtendedUser> members = new ListView<ExtendedUser>("members", lines) {

            @Override
            protected void populateItem(ListItem<ExtendedUser> item) {
                ExtendedUser ext = item.getModelObject();
                User user = ext.getUser();
                item.setModel(new CompoundPropertyModel<ExtendedUser>(ext));
                item.add(new UserLink("userLink", user));
                item.add(new Label("user.nickName"));
                CheckBox checkBox = new CheckBox("check", new PropertyModel<Boolean>(ext, "selected"));
                if (user.getDelegated()) {
                    checkBox.getModel().setObject(true);
                }
                item.add(checkBox);
                onPopulateItem(item, user);
            }
        };
        members.setReuseItems(true);
        table.add(members);
        add(table);
    }

    public List<ExtendedUser> getLines() {
        return lines;
    }

    protected abstract void onPopulateItem(ListItem<ExtendedUser> item, User user);

    @Override
    protected abstract void onSubmit();

    protected static class ExtendedUser implements Serializable {

        private User user;
        private boolean selected;

        public ExtendedUser(User newuser) {
            this.user = newuser;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User newUser) {
            this.user = newUser;
        }

        public boolean getSelected() {
            return selected;
        }

        public void setSelected(boolean isSelected) {
            this.selected = isSelected;
        }
    }
}
