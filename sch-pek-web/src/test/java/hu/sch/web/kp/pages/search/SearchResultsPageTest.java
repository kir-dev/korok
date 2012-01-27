/**
 * Copyright (c) 2008-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
        AjaxFallbackDefaultDataTable<Person> table = (AjaxFallbackDefaultDataTable<Person>) tester.getComponentFromLastRenderedPage(
                "hitsPanel:personTable");
        List<Person> result = ldapManager.search("László");
        assertTrue(table.getRowCount() == result.size());
    }
}
