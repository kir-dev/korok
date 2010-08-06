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

package hu.sch.web.profile.search;

import hu.sch.web.wicket.behaviors.FocusOnLoadBehavior;
import hu.sch.web.profile.ProfilePageTemplate;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author konvergal
 */
public class SearchPage extends ProfilePageTemplate {

    public class SearchForm extends Form {

        public String searchString;

        public SearchForm(String componentName) {
            super(componentName);
            TextField<String> sf = new TextField<String>("searchString",
                    new PropertyModel<String>(this, "searchString"));
            sf.add(new FocusOnLoadBehavior());
            add(sf);
        }

        @Override
        protected void onSubmit() {
            setResponsePage(new SearchResultPage(searchString));
        }
    }

    public SearchPage() {
        super();

        setHeaderLabelText("Keresés");
        add(new SearchForm("searchForm"));

        /*        DataView personsDataView = new DataView("personsDataView", new ListDataProvider(persons)) {

        @Override
        protected void populateItem(Item item) {
        Person person = (Person) item.getModelObject();
        item.add(new Label("nickName", person.getNickName()));
        BookmarkablePageLink bpl = new BookmarkablePageLink("profilePageLink", HomePage.class, new PageParameters("uid=" + person.getUid()));
        bpl.add(new Label("fullName", person.getFullName()));
        item.add(new Label("mail", person.getMail()));
        item.add(new Label("roomNumber", person.getRoomNumber()));
        item.add(bpl);
        }
        };
        add(personsDataView);*/
    }

    public SearchPage(PageParameters params) {
        //TODO:  process page parameters
    }
}

