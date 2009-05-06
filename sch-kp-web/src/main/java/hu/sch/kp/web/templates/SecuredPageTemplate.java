/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.templates;

import hu.sch.domain.Csoport;
import hu.sch.domain.ErtekelesIdoszak;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.Szemeszter;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.services.LdapPersonManagerLocal;
import hu.sch.kp.services.SystemManagerLocal;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.services.exceptions.NoSuchAttributeException;
import hu.sch.kp.web.SchKpApplication;
import hu.sch.kp.web.authz.UserAuthorization;
import hu.sch.kp.web.pages.admin.EditSettings;
import hu.sch.kp.web.pages.elbiralas.OsszesErtekeles;
import hu.sch.kp.web.pages.ertekeles.Ertekelesek;
import hu.sch.kp.web.pages.group.GroupHierarchy;
import hu.sch.kp.web.pages.logout.Logout;
import hu.sch.kp.web.pages.user.ShowUser;
import hu.sch.kp.web.session.VirSession;
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
    @EJB(name = "LDAPPersonManagerBean")
    protected LdapPersonManagerLocal ldapManager;

    public SecuredPageTemplate() {
        loadFelhasznalo();

        WebMarkupContainer headerLabelContainer =
                new WebMarkupContainer("headerLabelContainer");
        add(headerLabelContainer);
        headerLabelContainer.add(new Label("headerLabel", new Model()));
//        headerLabelContainer.add(new BookmarkablePageLink("detailView", ShowUser.class)).setVisible(false);

        add(new BookmarkablePageLink("showuserlink", ShowUser.class));
        add(new BookmarkablePageLink("grouphierarchylink", GroupHierarchy.class));
        if (hasUserRoleInSomeGroup(TagsagTipus.KORVEZETO)) {
            add(new BookmarkablePageLink("ertekeleseklink", Ertekelesek.class).setVisible(true));
        } else {
            add(new BookmarkablePageLink("ertekeleseklink", Ertekelesek.class).setVisible(false));
        }
        if (isCurrentUserJETI() || isCurrentUserAdmin()) {
            add(new BookmarkablePageLink("elbiralas", OsszesErtekeles.class).setVisible(true));
            add(new BookmarkablePageLink("editsettings", EditSettings.class));
        } else {
            add(new BookmarkablePageLink("elbiralas", OsszesErtekeles.class).setVisible(false));
            add(new BookmarkablePageLink("editsettings", EditSettings.class).setVisible(false));
        }
        add(new BookmarkablePageLink("logoutPageLink", Logout.class));
    }

    protected Felhasznalo loadFelhasznalo() {
        Long virID = getAuthorizationComponent().getUserid(getRequest());
        if (virID != null) {
            Felhasznalo user =
                    userManager.findUserWithCsoporttagsagokById(virID);
            getSession().setUser(user);

            return user;
        }
        return null;
    }

    @Override
    public VirSession getSession() {
        return (VirSession) super.getSession();
    }

    public Szemeszter getSzemeszter() {
        Szemeszter sz = null;
        try {
            sz = systemManager.getSzemeszter();
        } catch (NoSuchAttributeException ex) {
        }
        return sz;
    }

    public Csoport getCsoport() {
        Csoport cs = getSession().getCsoport();
        return cs;
    }

    public ErtekelesIdoszak getIdoszak() {
        return systemManager.getErtekelesIdoszak();
    }

    public Felhasznalo getFelhasznalo() {
        return getSession().getUser();
    }

    public boolean isCurrentUserAdmin() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), "ADMIN");
    }

    public boolean isCurrentUserJETI() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), "JETI");
    }

    public boolean hasUserRoleInGroup(Csoport group, TagsagTipus type) {
        return getAuthorizationComponent().hasRoleInGroup(getRequest(), group, type);
    }

    public boolean hasUserRoleInSomeGroup(TagsagTipus type) {
        return getAuthorizationComponent().hasRoleInSomeGroup(getRequest(), type);
    }

    public void setHeaderLabelText(String text) {
        get("headerLabelContainer").setVisible(true);
        ((WebMarkupContainer) get("headerLabelContainer")).get("headerLabel").setModel(new Model(text));
    }

    public void setHeaderLabelModel(IModel model) {
        get("headerLabelContainer").setVisible(true);
        ((WebMarkupContainer) get("headerLabelContainer")).get("headerLabel").setModel(model);
    }

    protected UserAuthorization getAuthorizationComponent() {
        return ((SchKpApplication) getApplication()).getAuthorizationComponent();
    }
}
