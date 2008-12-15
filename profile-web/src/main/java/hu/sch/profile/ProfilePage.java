package hu.sch.profile;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebRequest;

/**
 *
 * @author Adam Lantos
 */
public abstract class ProfilePage extends WebPage {
    
    public ProfilePage() {
        add(new BookmarkablePageLink("profilePageLink", ShowPersonPage.class));
        add(new BookmarkablePageLink("searchPageLink", SearchPage.class));
        add(new BookmarkablePageLink("editPageLink", EditPage.class));
        add(new BookmarkablePageLink("logoutPageLink", LogoutPage.class));
        add(new BookmarkablePageLink("changePasswordPageLink", ChangePasswordPage.class));
        add(new BookmarkablePageLink("birthDayPageLink", BirthDayPage.class));
//        add(new BookmarkablePageLink("listGroupsLink", ListGroupsPage.class));
        
        WebMarkupContainer headerLabelContainer = new WebMarkupContainer("headerLabelContainer");
        add(headerLabelContainer);
        headerLabelContainer.add(new Label("headerLabel", new Model()));
        headerLabelContainer.setVisible(false);
    }
    
    protected String getUid() {
//        return  ((WebRequest)getRequest()).getHttpServletRequest().getRemoteUser();
        return "konvergal";
    }
    
    public boolean isCurrentUserAdmin() {
//        return ((WebRequest)getRequest()).getHttpServletRequest().isUserInRole("ADMIN");
        return true;
    }
    
    public void setHeaderLabelText(String text) {
        get("headerLabelContainer").setVisible(true);
        ((WebMarkupContainer)get("headerLabelContainer")).get("headerLabel").setModel(new Model(text));
    }

    public void setHeaderLabelModel(IModel model) {
        get("headerLabelContainer").setVisible(true);
        ((WebMarkupContainer)get("headerLabelContainer")).get("headerLabel").setModel(model);
    }
}
