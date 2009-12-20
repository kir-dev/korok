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
import org.apache.wicket.markup.html.panel.Fragment;
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
        if (felh != null) {
            add(new NotNullFragment("displayFragment", "notnull", felh.getId(), felh.getName()));
        } else {
            add(new Fragment("displayFragment", "null", null, null));
        }

    }

    private class NotNullFragment extends Fragment {

        public NotNullFragment(String id, String markupId, Long userId, String userName) {
            super(id, markupId, null, null);
            Link fl = new BookmarkablePageLink("felhLink", ShowUser.class, new PageParameters("id=" + userId));
            fl.setModel(getDefaultModel());
            fl.add(new Label("name", userName));
            add(fl);
        }
    }
}
