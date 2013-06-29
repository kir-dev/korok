package hu.sch.web.kp.pages.search;

import hu.sch.domain.profile.Person;
import hu.sch.ejb.LdapManagerBean;
import hu.sch.services.LdapManagerLocal;
import hu.sch.web.kp.search.PersonResultPanel;
import hu.sch.web.kp.search.SearchResultsPage;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.test.WebTest;
import java.util.List;
import javax.naming.NamingException;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author aldaris
 */
public class SearchResultsPageTest extends WebTest {

    private static final String SEARCH_TERM = "László";
    private static LdapManagerLocal ldapManager;

    @BeforeClass
    public static void init() throws NamingException {
        ldapManager = lookupEJB(LdapManagerBean.class);
    }

    @Test
    public void testFormbasedPersonSearch() {
        tester.startPage(ShowUser.class);
        FormTester formTester = tester.newFormTester("headerPanel:searchForm");
        formTester.select("searchDdc", 1);
        formTester.setValue("searchField", SEARCH_TERM);
        formTester.submit();

        tester.assertNoErrorMessage();
        tester.assertRenderedPage(SearchResultsPage.class);
    }

    @Test
    public void testDirectPersonSearch() {
        tester.startPage(SearchResultsPage.class, new PageParameters().add("type", "user").add("key", SEARCH_TERM));
        tester.assertRenderedPage(SearchResultsPage.class);
        tester.assertComponent("hitsPanel", PersonResultPanel.class);
        AjaxFallbackDefaultDataTable<Person, String> table = (AjaxFallbackDefaultDataTable<Person, String>) tester.getComponentFromLastRenderedPage("hitsPanel:personTable");
        List<Person> result = ldapManager.search("László");
        assertTrue(table.getRowCount() == result.size());
    }
}
