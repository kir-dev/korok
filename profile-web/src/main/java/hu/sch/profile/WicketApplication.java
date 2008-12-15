package hu.sch.profile;

import org.apache.wicket.protocol.http.WebApplication;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see hu.sch.Profil.Start#main(String[])
 */
public class WicketApplication extends WebApplication {

    /**
     * Constructor
     */
    public WicketApplication() {
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    public Class getHomePage() {
        return ShowPersonPage.class;
    }

    @Override
    protected void init() {
        super.init();
        
        addComponentInstantiationListener(new JavaEEComponentInjector(this));
        
        mountBookmarkablePage("/show", ShowPersonPage.class);
        mountBookmarkablePage("/edit", EditPage.class);
        mountBookmarkablePage("/search", SearchPage.class);
        mountBookmarkablePage("/logout", LogoutPage.class);
        mountBookmarkablePage("/confirm", ConfirmPage.class);
        mountBookmarkablePage("/groups", ListGroupsPage.class);
        mountBookmarkablePage("/group", ShowGroupPage.class);
        mountBookmarkablePage("/admin", AdminPage.class);
        mountBookmarkablePage("/changepassword", ChangePasswordPage.class);
        mountBookmarkablePage("/birthdays", BirthDayPage.class);
    }
}
