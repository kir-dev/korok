package hu.sch.web.kp.search;

import hu.sch.services.SearchManagerLocal;
import hu.sch.web.kp.KorokPage;
import javax.inject.Inject;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author aldaris
 */
public class SearchResultsPage extends KorokPage {

    @Inject
    private SearchManagerLocal searchManager;

    public SearchResultsPage() {
        getSession().error("Nem adtál meg keresési feltételt");
        throw new RestartResponseException(getApplication().getHomePage());
    }

    public SearchResultsPage(final PageParameters params) {
        String type = params.get("type").toString();
        String keyword = params.get("key").toString();


        if (type == null || keyword == null || (!type.equals("user") && !type.equals("group")) || keyword.isEmpty()) {
            getSession().error("Hibás keresési feltétel!");
            throw new RestartResponseException(getApplication().getHomePage());
        }
        if (keyword.length() < 3) {
            getSession().error("A keresési feltételnek legalább 3 karateresnek kell lennie!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        setHeaderLabelText("Találatok");
        if (type.equals("group")) {
            GroupResultPanel groups = new GroupResultPanel("hitsPanel", groupManager.findGroupsByName(keyword));
            add(groups);
        } else if (type.equals("user")) {
            PersonResultPanel users = new PersonResultPanel("hitsPanel", searchManager.searchUsers(keyword));
            add(users);
        }
    }
}
