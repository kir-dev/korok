/**
 * Copyright (c) 2009-2010, Peter Major
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
package hu.sch.web.profile.pages.search;

import hu.sch.domain.profile.Person;
import hu.sch.web.kp.pages.search.PersonResultPanel;
import hu.sch.web.profile.pages.template.ProfilePageTemplate;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author konvergal
 */
public final class SearchResultPage extends ProfilePageTemplate {

    //public List<Person> persons = new ArrayList();
    //PersonDataProvider personDataProvider;
    PersonResultPanel personsTable;

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
        StringBuilder backup = null;
        while (tok.hasMoreTokens()) {
            String s = tok.nextToken();
            if (s.equals("\"")) {
                if (quotedExpression != null) {
                    if (quotedExpression.length() > 1) {

                        if (quotedExpression.length() >= 3) {
                            searchWords.add(quotedExpression.toString());
                        } else {
                            hasLessThanThreeCharacterSearchWord = true;
                        }

                    }
                    quotedExpression.setLength(0);
                    quotedExpression = null;
                } else {
                    if (backup == null) {
                        backup = new StringBuilder();
                    }
                    quotedExpression = backup;
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

        if (!searchWords.isEmpty()) {
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

        if (personsTable.getPersonDataProvider().getPersons().isEmpty() && !searchWords.isEmpty()) {
            info("Nincs találat :(");
        }

        if (personsTable.getPersonDataProvider().getPersons().isEmpty()) {
            personsTable.setVisible(false);
        } else {
            personsTable.setVisible(true);
        }
    }

    public SearchResultPage(String searchString) {
        super();
        setHeaderLabelText("Keresés");

        SearchForm searchForm = new SearchForm("searchForm");
        searchForm.searchString = searchString;
        add(searchForm);

        personsTable = new PersonResultPanel("resultsTable", new ArrayList<Person>());
        add(personsTable);

        setPersonsBySearchString(searchString);
    }

    public SearchResultPage(PageParameters params) {
        //TODO:  process page parameters
    }
}
