package hu.sch.web.wicket.components.customlinks;

import hu.sch.web.kp.search.SearchResultsPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author aldaris
 */
public class SearchLink extends Panel {

    public static final String USER_TYPE = "user";
    public static final String GROUP_TYPE = "group";

    public SearchLink(final String id, final String type, final String key) {
        super(id);

        if (key != null) {
            Link<SearchResultsPage> searchLink =
                    new BookmarkablePageLink<SearchResultsPage>("searchLink",
                    SearchResultsPage.class, new PageParameters().add("type", type).add("key", key));
            searchLink.add(new Label("searchLinkLabel", key));
            add(searchLink);
        }
    }
}
