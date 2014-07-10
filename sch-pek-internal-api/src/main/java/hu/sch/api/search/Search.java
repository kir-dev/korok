package hu.sch.api.search;

import hu.sch.api.Base;
import hu.sch.api.exceptions.RequestFormatException;
import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import hu.sch.services.SearchManagerLocal;
import java.util.List;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("search")
public class Search extends Base {

    private static final Logger logger = LoggerFactory.getLogger(Search.class);
    private SearchManagerLocal searchManager;

    @POST
    public SearchResult doSearch(@Valid final SearchTerm term) {
        if (term == null)  {
            throw new RequestFormatException("Request body cannot be empty.");
        }

        long countOfUsers = searchManager.countUsers(term.getTerm());
        long countOfGroup = searchManager.countGroup(term.getTerm());

        switch(term.getMode()) {
            case USER:
                List<User> users = searchManager.searchUsers(term.getTerm(), term.getPage(), term.getResultsPerPage());
                return SearchResult.fromUsers(countOfUsers, countOfGroup, users);
            case GROUP:
                List<Group> groups = searchManager.searchGroups(term.getTerm(), term.getPage(), term.getResultsPerPage());
                return SearchResult.fromGroups(countOfUsers, countOfGroup, groups);
            default:
                logger.error("Not supported search mode: {}", term.getMode().name());
                throw new RequestFormatException("Not supported search mode: " + term.getMode().name());
        }
    }

    @Inject
    public void setSearchManager(SearchManagerLocal searchManager) {
        this.searchManager = searchManager;
    }

}
