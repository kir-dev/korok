package hu.sch.web.wicket.components.customlinks;

import hu.sch.domain.user.User;
import hu.sch.web.kp.user.ShowUser;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Egyszerű panel, ami egy {@link BookmarkablePageLink}et tartalmaz, ami a
 * felhasználót leíró oldalra mutat.
 *
 * @author hege
 * @author messo
 * @see ShowUser
 */
public class UserLink extends Panel {

    public UserLink(String id, User user) {
        super(id);

        Link<ShowUser> l = new BookmarkablePageLink<ShowUser>("link", ShowUser.class,
                new PageParameters().add("id", user.getId()));
        l.add(new Label("name", user.getFullName()));
        add(l);
    }
}
