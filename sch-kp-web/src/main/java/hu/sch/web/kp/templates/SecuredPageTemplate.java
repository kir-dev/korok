/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.templates;

import java.io.Serializable;
import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.User;
import hu.sch.services.LdapManagerLocal;
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
import hu.sch.web.kp.pages.svie.SvieAccount;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author hege
 */
public class SecuredPageTemplate extends WebPage {

    @EJB(name = "SystemManagerBean")
    protected SystemManagerLocal systemManager;
    @EJB(name = "UserManagerBean")
    protected UserManagerLocal userManager;
    @EJB(name = "LdapManagerBean")
    protected LdapManagerLocal ldapManager;
    private static final Logger log = Logger.getLogger(SecuredPageTemplate.class);
    private static final String adminRoleName = "ADMIN";
    private static final String jetiRoleName = "JETI";

    public SecuredPageTemplate() {
        loadUser();

        ldapManager.initialization();
        WebMarkupContainer headerLabelContainer =
                new WebMarkupContainer("headerLabelContainer");
        add(headerLabelContainer);
        headerLabelContainer.add(new Label("headerLabel", new Model<Serializable>()));

        add(new BookmarkablePageLink("showuserlink", ShowUser.class));
        add(new BookmarkablePageLink("grouphierarchylink", GroupHierarchy.class));
        if (isUserGroupLeaderInSomeGroup()) {
            add(new BookmarkablePageLink("ertekeleseklink", Valuations.class).setVisible(true));
        } else {
            add(new BookmarkablePageLink("ertekeleseklink", Valuations.class).setVisible(false));
        }
        if (isCurrentUserJETI()) {
            add(new BookmarkablePageLink("elbiralas", ConsiderPage.class));
            add(new BookmarkablePageLink("editsettings", EditSettings.class));
        } else {
            add(new BookmarkablePageLink("elbiralas", ConsiderPage.class).setVisible(false));
            add(new BookmarkablePageLink("editsettings", EditSettings.class).setVisible(false));
        }
        add(new BookmarkablePageLink("svieaccount", SvieAccount.class));
        //add(new BookmarkablePageLink("logoutPageLink", Logout.class));
    }

    protected void loadUser() {
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
            } else {
                getSession().setUserId(0L);
            }
        }
    }

    protected User getUser() {
        Long virId = getAuthorizationComponent().getUserid(getRequest());
        if (getSession().getUserId() != virId) {
            loadUser();
        }
        return userManager.findUserWithCsoporttagsagokById(getSession().getUserId());
    }

    protected Group getGroup() {
        Long groupId = getSession().getGroupId();
        if (groupId != null) {
            return userManager.findGroupById(getSession().getGroupId());
        } else {
            return null;
        }
    }

    @Override
    public VirSession getSession() {
        return (VirSession) super.getSession();
    }

    public Semester getSemester() {
        Semester sz = null;
        try {
            sz = systemManager.getSzemeszter();
        } catch (NoSuchAttributeException ex) {
            log.warn("Attribute for semester isn't setted in the database.", ex);
        }
        return sz;
    }

    public boolean isCurrentUserAdmin() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), adminRoleName);
    }

    public boolean isCurrentUserJETI() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), jetiRoleName);
    }

    public boolean isUserGroupLeader(Group group) {
        return getAuthorizationComponent().isGroupLeaderInGroup(getRequest(), group);
    }

    public boolean isUserGroupLeaderInSomeGroup() {
        return getAuthorizationComponent().isGroupLeaderInSomeGroup(getRequest());
    }

    public void setHeaderLabelText(String text) {
        ((WebMarkupContainer) get("headerLabelContainer")).get("headerLabel").setDefaultModel(new Model<Serializable>(text));
    }

    public void setHeaderLabelModel(IModel<?> model) {
        ((WebMarkupContainer) get("headerLabelContainer")).get("headerLabel").setDefaultModel(model);
    }

    protected UserAuthorization getAuthorizationComponent() {
        return ((PhoenixApplication) getApplication()).getAuthorizationComponent();
    }
}
