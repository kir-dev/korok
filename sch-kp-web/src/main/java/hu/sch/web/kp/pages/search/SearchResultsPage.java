package hu.sch.web.kp.pages.search;

import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;

/**
 *
 * @author aldaris
 */
public class SearchResultsPage extends SecuredPageTemplate {

    public SearchResultsPage(final PageParameters params) {
        String type = params.getString("type");
        String keyword = params.getString("key");


        if (type == null || keyword == null || (!type.equals("user") && !type.equals("group")) || keyword.isEmpty()) {
            getSession().error("Hibás keresési feltétel!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        setHeaderLabelText("Találatok");
        if (type.equals("group")) {
            GroupResultPanel groups = new GroupResultPanel("hitsPanel", userManager.findGroupByName("%" + keyword + "%"));
            add(groups);
        } else if (type.equals("user")) {
            //TODO
            List<String> terms = new ArrayList<String>();
            terms.add(keyword);
            PersonResultPanel users = new PersonResultPanel("hitsPanel", ldapManager.search(terms));
            add(users);
        }

    }
}
