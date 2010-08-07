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
package hu.sch.web.kp;

import hu.sch.web.kp.admin.EditSettings;
import hu.sch.web.kp.consider.ConsiderPage;
import hu.sch.web.kp.group.GroupHierarchy;
import hu.sch.web.kp.search.SearchResultsPage;
import hu.sch.web.kp.svie.SvieAccount;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.kp.valuation.Valuations;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author messo
 */
class HeaderPanel extends Panel {

    private String searchTerm;
    private String searchType = "felhasználó";

    public HeaderPanel(String id, boolean showValuationsLink, boolean showConsiderPageLink, boolean showEditSettingsLink) {
        super(id);

        createSearchBar();

        add(new BookmarkablePageLink<ShowUser>("showuserlink", ShowUser.class));
        add(new BookmarkablePageLink<GroupHierarchy>("grouphierarchylink", GroupHierarchy.class));
        if (showValuationsLink) {
            add(new BookmarkablePageLink<Valuations>("ertekeleseklink", Valuations.class).setVisible(true));
        } else {
            add(new BookmarkablePageLink<Valuations>("ertekeleseklink", Valuations.class).setVisible(false));
        }

        if (showConsiderPageLink) {
            add(new BookmarkablePageLink<ConsiderPage>("elbiralas", ConsiderPage.class));
        } else {
            add(new BookmarkablePageLink<ConsiderPage>("elbiralas", ConsiderPage.class).setVisible(false));
        }

        if (showEditSettingsLink) {
            add(new BookmarkablePageLink<EditSettings>("editsettings", EditSettings.class));
        } else {
            add(new BookmarkablePageLink<EditSettings>("editsettings", EditSettings.class).setVisible(false));
        }

        add(new BookmarkablePageLink<SvieAccount>("svieaccount", SvieAccount.class));
    }

    private void createSearchBar() {
        StatelessForm<Void> searchForm = new StatelessForm<Void>("searchForm") {

            @Override
            protected void onSubmit() {
                if (searchType == null || searchTerm == null) {
                    super.getSession().error("Hibás keresési feltétel!");
                    throw new RestartResponseException(getApplication().getHomePage());
                }
                if (searchTerm.length() < 3) {
                    super.getSession().error("Túl rövid keresési feltétel!");
                    throw new RestartResponseException(getApplication().getHomePage());
                }
                PageParameters params = new PageParameters();
                params.put("type", ((searchType.equals("felhasználó")) ? "user" : "group"));
                params.put("key", searchTerm);
                setResponsePage(SearchResultsPage.class, params);
            }
        };
        DropDownChoice<String> searchTypeDdc = new DropDownChoice<String>("searchDdc",
                new PropertyModel<String>(this, "searchType"),
                new LoadableDetachableModel<List<? extends String>>() {

                    @Override
                    protected List<? extends String> load() {
                        List<String> ret = new ArrayList<String>();
                        ret.add("felhasználó");
                        ret.add("kör");
                        return ret;
                    }
                });
        searchTypeDdc.setNullValid(false);
        searchForm.add(searchTypeDdc);
        searchForm.add(new TextField<String>("searchField", new PropertyModel<String>(this, "searchTerm")));
        add(searchForm);
    }
}
