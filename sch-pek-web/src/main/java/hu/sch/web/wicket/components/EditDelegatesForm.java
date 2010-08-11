/**
 * Copyright (c) 2008-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
