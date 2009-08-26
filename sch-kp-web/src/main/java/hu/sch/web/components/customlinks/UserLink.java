/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components.customlinks;

import hu.sch.domain.User;
import hu.sch.web.kp.pages.user.ShowUser;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class UserLink extends Panel {

    public UserLink(String id, User user) {
        super(id, new CompoundPropertyModel(user));
        init();
    }

    private void init() {
        final User felh = (User) getDefaultModelObject();
        Link fl = new BookmarkablePageLink("felhLink", ShowUser.class, new PageParameters("id=" + felh.getId()));
        fl.setModel(getDefaultModel());
        fl.add(new Label("name"));
        add(fl);
    }
}
