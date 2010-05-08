package hu.sch.web.wicket.components.customlinks;

import hu.sch.web.kp.pages.search.SearchResultsPage;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author aldaris
 */
public class SearchLink extends Panel {

    public static final String USER_TYPE = "user";
    public static final String GROUP_TYPE = "group";

    public SearchLink(String id, String type, String key) {
        super(id);
        Link<SearchResultsPage> searchLink =
                new BookmarkablePageLink<SearchResultsPage>("searchLink",
                SearchResultsPage.class, new PageParameters("type=" + type + ",key=" + key));
        searchLink.add(new Label("searchLinkLabel", key));
        add(searchLink);
    }
}
