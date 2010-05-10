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

package hu.sch.web.kp.templates;

import java.io.Serializable;
import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.User;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.PostManagerLocal;
import hu.sch.services.exceptions.NoSuchAttributeException;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.kp.pages.admin.EditSettings;
import hu.sch.web.kp.pages.consider.ConsiderPage;
import hu.sch.web.kp.pages.valuation.Valuations;
import hu.sch.web.kp.pages.group.GroupHierarchy;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.session.VirSession;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.web.PhoenixApplication;
import hu.sch.web.kp.pages.search.SearchResultsPage;
import hu.sch.web.kp.pages.svie.SvieAccount;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public abstract class SecuredPageTemplate extends WebPage {

    @EJB(name = "SystemManagerBean")
    protected SystemManagerLocal systemManager;
    @EJB(name = "UserManagerBean")
    protected UserManagerLocal userManager;
    @EJB(name = "LdapManagerBean")
    protected LdapManagerLocal ldapManager;
    @EJB(name = "PostManagerBean")
    protected PostManagerLocal postManager;
    private static final Logger log = Logger.getLogger(SecuredPageTemplate.class);
    private static final String adminRoleName = "ADMIN";
    private static final String jetiRoleName = "JETI";
    private static final String svieRoleName = "SVIE";
    private String searchTerm;
    private String searchType = "felhasználó";
    private Label navbarScript;

    public SecuredPageTemplate() {
        loadUser();

        navbarScript = new Label("navbarScript");
        createNavbarWithSupportId(32);
        navbarScript.setEscapeModelStrings(false); // do not HTML escape JavaScript code
        add(navbarScript);

        WebMarkupContainer headerLabelContainer = new WebMarkupContainer("headerLabelContainer");
        add(headerLabelContainer);
        headerLabelContainer.add(new Label("headerLabel", new Model<Serializable>()));

        createSearchBar();

        add(new BookmarkablePageLink<ShowUser>("showuserlink", ShowUser.class));
        add(new BookmarkablePageLink<GroupHierarchy>("grouphierarchylink", GroupHierarchy.class));
        if (isUserGroupLeaderInSomeGroup()) {
            add(new BookmarkablePageLink<Valuations>("ertekeleseklink", Valuations.class).setVisible(true));
        } else {
            add(new BookmarkablePageLink<Valuations>("ertekeleseklink", Valuations.class).setVisible(false));
        }

        if (isCurrentUserJETI()) {
            add(new BookmarkablePageLink<ConsiderPage>("elbiralas", ConsiderPage.class));
        } else {
            add(new BookmarkablePageLink<ConsiderPage>("elbiralas", ConsiderPage.class).setVisible(false));
        }

        if (isCurrentUserJETI() || isCurrentUserSVIE() || isCurrentUserAdmin()) {
            add(new BookmarkablePageLink<EditSettings>("editsettings", EditSettings.class));
        } else {
            add(new BookmarkablePageLink<EditSettings>("editsettings", EditSettings.class).setVisible(false));
        }

        add(new BookmarkablePageLink<SvieAccount>("svieaccount", SvieAccount.class));
        //add(new BookmarkablePageLink("logoutPageLink", Logout.class));
    }

    private void createSearchBar() {
        StatelessForm<Void> searchForm = new StatelessForm<Void>("searchForm") {

            @Override
            protected void onSubmit() {
                if (searchType == null || searchTerm == null) {
                    getSession().error("Hibás keresési feltétel!");
                    throw new RestartResponseException(getApplication().getHomePage());
                }
                if (searchTerm.length() < 3) {
                    getSession().error("Túl rövid keresési feltétel!");
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

    private void loadUser() {
        Long virId = getAuthorizationComponent().getUserid(getRequest());
        if (getSession().getUserId() != virId) {

            if (virId != null) {
                User userAttrs =
                        getAuthorizationComponent().getUserAttributes(getRequest());
                if (userAttrs != null) {
                    userAttrs.setId(virId);
                    userManager.updateUserAttributes(userAttrs);
                }
                getSession().setUserId(virId);
            }
        } else {
            getSession().setUserId(0L);
        }
    }

    protected final User getUser() {
        Long virId = getAuthorizationComponent().getUserid(getRequest());
        if (getSession().getUserId() != virId) {
            loadUser();
        }
        return userManager.findUserWithCsoporttagsagokById(getSession().getUserId());
    }

    @Override
    public VirSession getSession() {
        return (VirSession) super.getSession();
    }

    protected final Semester getSemester() {
        Semester sz = null;
        try {
            sz = systemManager.getSzemeszter();
        } catch (NoSuchAttributeException ex) {
            log.warn("Attribute for semester isn't set in the database.", ex);
        }
        return sz;
    }

    protected final boolean isCurrentUserAdmin() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), adminRoleName);
    }

    protected final boolean isCurrentUserJETI() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), jetiRoleName);
    }

    protected final boolean isCurrentUserSVIE() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), svieRoleName);
    }

    protected final boolean isUserGroupLeader(Group group) {
        return getAuthorizationComponent().isGroupLeaderInGroup(getRequest(), group);
    }

    protected final boolean isUserGroupLeaderInSomeGroup() {
        return getAuthorizationComponent().isGroupLeaderInSomeGroup(getRequest());
    }

    protected final boolean hasUserDelegatedPostInGroup(Group group) {
        User user = getUser();
        if (user == null) {
            return false;
        }
        return postManager.hasUserDelegatedPostInGroup(group, user);
    }

    public void setHeaderLabelText(String text) {
        ((WebMarkupContainer) get("headerLabelContainer")).get("headerLabel").setDefaultModel(new Model<Serializable>(text));
    }

    protected void createNavbarWithSupportId(int supportId) {
        navbarScript.setDefaultModel(new Model<String>("var navbarConf = { "
                + "logoutLink: 'https://idp.sch.bme.hu/opensso/UI/Logout', "
                + "theme: 'blue', "
                + "width: 900, "
                + "support: " + supportId + ", "
                + "helpMenuItems: ["
                + "{"
                + "title: 'FAQ',"
                + "url: 'https://kir-dev.sch.bme.hu/kozossegi-pontozas/'"
                + "}"
                + "]"
                + "}; "
                + "printNavbar(navbarConf);"));
    }

    private final UserAuthorization getAuthorizationComponent() {
        return ((PhoenixApplication) getApplication()).getAuthorizationComponent();
    }
}
