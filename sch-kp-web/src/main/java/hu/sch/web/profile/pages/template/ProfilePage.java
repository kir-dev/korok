package hu.sch.web.profile.pages.template;

import java.io.Serializable;

import hu.sch.web.profile.pages.show.ShowPersonPage;
import hu.sch.web.profile.pages.search.SearchPage;
import hu.sch.services.LdapManagerLocal;
import hu.sch.web.profile.pages.birthday.BirthDayPage;
import hu.sch.web.profile.pages.edit.EditPage;
import hu.sch.web.profile.pages.passwordchange.ChangePasswordPage;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author Adam Lantos
 */
public abstract class ProfilePage extends WebPage {

    @EJB(name = "LdapManagerBean")
    protected LdapManagerLocal ldapManager;

    public ProfilePage() {

        add(new BookmarkablePageLink("profilePageLink", ShowPersonPage.class));
        add(new BookmarkablePageLink("searchPageLink", SearchPage.class));
        add(new BookmarkablePageLink("editPageLink", EditPage.class));
        //add(new BookmarkablePageLink("logoutPageLink", LogoutPage.class));
        add(new BookmarkablePageLink("changePasswordPageLink", ChangePasswordPage.class));
        add(new BookmarkablePageLink("birthDayPageLink", BirthDayPage.class));
//        add(new BookmarkablePageLink("listGroupsLink", ListGroupsPage.class));
        WebMarkupContainer headerLabelContainer = new WebMarkupContainer("headerLabelContainer");
        add(headerLabelContainer);
        headerLabelContainer.add(new Label("headerLabel", new Model<Serializable>()));
        headerLabelContainer.setVisible(false);
    }

    protected String getUid() {
        //return ((WebRequest) getRequest()).getHttpServletRequest().getRemoteUser();
        return "aldaris";
    }

    public boolean isCurrentUserAdmin() {
        //return ((WebRequest) getRequest()).getHttpServletRequest().isUserInRole("ADMIN");
        return true;
    }

    public void setHeaderLabelText(String text) {
        get("headerLabelContainer").setVisible(true);
        ((WebMarkupContainer) get("headerLabelContainer")).get("headerLabel").setDefaultModel(new Model<Serializable>(text));
    }

    public void setHeaderLabelModel(IModel<?> model) {
        get("headerLabelContainer").setVisible(true);
        ((WebMarkupContainer) get("headerLabelContainer")).get("headerLabel").setDefaultModel(model);
    }
}
