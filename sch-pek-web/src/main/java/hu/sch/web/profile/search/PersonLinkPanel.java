package hu.sch.web.profile.search;

import hu.sch.domain.profile.Person;
import hu.sch.web.profile.show.ShowPersonPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author konvergal
 */
public class PersonLinkPanel extends Panel {

    public PersonLinkPanel(String id, Person person) {
        super(id);

        BookmarkablePageLink link = new BookmarkablePageLink("fullNameLink",
                ShowPersonPage.class, new PageParameters().add("uid", person.getUid()));
        link.add(new Label("fullName", person.getFullName()));
        add(link);
    }
}
