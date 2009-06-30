/*
 *  Copyright 2008 konvergal.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package hu.sch.web.profile.pages.search;

import hu.sch.web.profile.pages.template.ProfilePage;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author konvergal
 */
public final class SearchResultPage extends ProfilePage {

    //public List<Person> persons = new ArrayList();
    //PersonDataProvider personDataProvider;
    PersonsSearchResultsTable personsTable;

    public class SearchForm extends Form {

        public String searchString;

        public SearchForm(String componentName) {
            super(componentName);
            TextField sf = new TextField("searchString",
                    new PropertyModel(this, "searchString"));
            sf.add(new SimpleAttributeModifier("autofocus", "autofocus"));
            add(sf);
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();

            setPersonsBySearchString(searchString);
        }
    }

    public void setPersonsBySearchString(String searchString) {
        //persons.clear();
        personsTable.getPersonDataProvider().getPersons().clear();

        if (searchString == null) {
            personsTable.setVisible(false);
            error("Nem adtál meg kifejezést!");
            return;
        }

        ArrayList<String> searchWords = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(searchString, "\" ", true);

        Boolean hasLessThanThreeCharacterSearchWord = false;
        StringBuilder quotedExpression = null;
        while (tok.hasMoreTokens()) {
            String s = tok.nextToken();
            if (s.equals("\"")) {
                if (quotedExpression != null) {
                    String expr = quotedExpression.toString();
                    if (expr.length() > 1) {

                        if (expr.length() >= 3) {
                            searchWords.add(expr);
                        } else {
                            hasLessThanThreeCharacterSearchWord = true;
                        }

                    }
                    quotedExpression = null;
                } else {
                    quotedExpression = new StringBuilder();
                }
            } else {
                if (quotedExpression != null) {
                    quotedExpression.append(s);
                } else if (!s.equals(" ")) {

                    if (s.length() >= 3) {
                        searchWords.add(s);
                    } else {
                        hasLessThanThreeCharacterSearchWord = true;
                    }

                }
            }
        }

        if (searchWords.size() > 0) {
            if (isCurrentUserAdmin()) {
                //persons.addAll(LdapPersonManager.getInstance().searchByAdmin(searchWords));
                personsTable.getPersonDataProvider().setPersons(ldapManager.searchByAdmin(searchWords));
            } else {
                //persons.addAll(LdapPersonManager.getInstance().search(searchWords));
                personsTable.getPersonDataProvider().setPersons(ldapManager.search(searchWords));
            }
        //personDataProvider.setPersons(persons);
        }

        if (hasLessThanThreeCharacterSearchWord) {
            info("A kereső a 3 karakternél rövidebb kifejezéseket nem veszi figyelembe.");
        }

        if ((personsTable.getPersonDataProvider().getPersons().size() == 0) && (searchWords.size() > 0)) {
            info("Nincs találat :(");
        }

        if (personsTable.getPersonDataProvider().getPersons().size() == 0) {
            personsTable.setVisible(false);
        } else {
            personsTable.setVisible(true);
        }
    }

    public SearchResultPage(String searchString) {
        super();
        add(new FeedbackPanel("feedbackPanel"));

        SearchForm searchForm = new SearchForm("searchForm");
        searchForm.searchString = searchString;
        add(searchForm);

        //personDataProvider = new PersonDataProvider(persons);
        personsTable = new PersonsSearchResultsTable("resultsTable");
        add(personsTable);

        setPersonsBySearchString(searchString);
    }

    public SearchResultPage(PageParameters params) {
        //TODO:  process page parameters
    }
}

