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

package hu.sch.web.kp.pages.search;

import hu.sch.web.kp.templates.KorokPageTemplate;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;

/**
 *
 * @author aldaris
 */
public class SearchResultsPage extends KorokPageTemplate {

    public SearchResultsPage() {
        getSession().error("Nem adtál meg keresési feltételt");
        throw new RestartResponseException(getApplication().getHomePage());
    }

    public SearchResultsPage(final PageParameters params) {
        String type = params.getString("type");
        String keyword = params.getString("key");


        if (type == null || keyword == null || (!type.equals("user") && !type.equals("group")) || keyword.isEmpty()) {
            getSession().error("Hibás keresési feltétel!");
            throw new RestartResponseException(getApplication().getHomePage());
        }
        if (keyword.length() < 3 ) {
            getSession().error("A keresési feltételnek legalább 3 karateresnek kell lennie!");
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
