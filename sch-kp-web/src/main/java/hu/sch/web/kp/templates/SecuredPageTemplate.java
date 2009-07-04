/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.templates;

import java.io.Serializable;

import hu.sch.domain.Group;
import hu.sch.domain.MembershipType;
import hu.sch.domain.Semester;
import hu.sch.domain.User;
import hu.sch.domain.ValuationPeriod;
import hu.sch.services.exceptions.NoSuchAttributeException;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.kp.pages.admin.EditSettings;
import hu.sch.web.kp.pages.consider.ConsiderPage;
import hu.sch.web.kp.pages.valuation.Valuations;
import hu.sch.web.kp.pages.group.GroupHierarchy;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.kp.session.VirSession;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.web.PhoenixApplication;
import javax.ejb.EJB;
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

    public SecuredPageTemplate() {
        loadUser();

        WebMarkupContainer headerLabelContainer =
                new WebMarkupContainer("headerLabelContainer");
        add(headerLabelContainer);
        headerLabelContainer.add(new Label("headerLabel", new Model<Serializable>()));

        add(new BookmarkablePageLink("showuserlink", ShowUser.class));
        add(new BookmarkablePageLink("grouphierarchylink", GroupHierarchy.class));
        if (hasUserRoleInSomeGroup(MembershipType.KORVEZETO)) {
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
    //add(new BookmarkablePageLink("logoutPageLink", Logout.class));
    }

    protected void loadUser() {
        Long virID = getAuthorizationComponent().getUserid(getRequest());
        if (this.getSession().getUser() == null ||
                this.getSession().getUser().getId() != virID) {

            if (virID != null) {
                User user =
                        userManager.findUserWithCsoporttagsagokById(virID);
                User userAttrs =
                        getAuthorizationComponent().getUserAttributes(getRequest());
                if (userAttrs != null) {
                    userAttrs.setId(virID);
                    userManager.updateUserAttributes(userAttrs);
                }
                getSession().setUser(user);
            } else {
                getSession().setUser(new User());
            }
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
        }
        return sz;
    }

    public Group getGroup() {
        Group cs = getSession().getCsoport();
        return cs;
    }

    public ValuationPeriod getPeriod() {
        return systemManager.getErtekelesIdoszak();
    }

    public User getUser() {
        return getSession().getUser();
    }

    public boolean isCurrentUserAdmin() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), "ADMIN");
    }

    public boolean isCurrentUserJETI() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), "JETI");
    }

    public boolean hasUserRoleInGroup(Group group, MembershipType type) {
        return getAuthorizationComponent().hasRoleInGroup(getRequest(), group, type);
    }

    public boolean hasUserRoleInSomeGroup(MembershipType type) {
        return getAuthorizationComponent().hasRoleInSomeGroup(getRequest(), type);
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
