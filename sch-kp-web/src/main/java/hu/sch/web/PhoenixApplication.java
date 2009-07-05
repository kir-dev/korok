/*
 * SchKpApplication.java
 * 
 * Created on Aug 24, 2007, 5:34:20 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hu.sch.web;

import hu.sch.domain.EntrantType;
import hu.sch.domain.MembershipType;
import hu.sch.domain.ValuationStatus;
import hu.sch.web.kp.pages.group.EditGroupInfo;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.kp.pages.group.ShowGroup;
import hu.sch.web.kp.pages.group.GroupHierarchy;
import hu.sch.web.kp.pages.group.GroupHistory;
import hu.sch.web.kp.pages.logout.Logout;
import hu.sch.web.kp.pages.user.UserHistory;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.error.InternalServerError;
import hu.sch.web.error.PageExpiredError;
import hu.sch.web.kp.pages.admin.EditSettings;
import hu.sch.web.kp.pages.consider.ConsiderPage;
import hu.sch.web.kp.pages.valuation.Valuations;
import hu.sch.web.kp.pages.valuation.NewValuation;
import hu.sch.web.kp.pages.group.AddGroupMember;
import hu.sch.web.kp.pages.group.ChangePost;
import hu.sch.web.kp.session.VirSession;
import hu.sch.web.kp.util.EntrantTypeConverter;
import hu.sch.web.kp.util.ValuationStatusConverter;
import hu.sch.web.kp.util.MembershipTypeConverter;
import hu.sch.web.profile.pages.show.ShowPersonPage;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.convert.ConverterLocator;
import org.apache.wicket.util.lang.PackageName;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;

/**
 *
 * @author hege
 */
public class PhoenixApplication extends WebApplication {

    private static final String AUTHZ_COMPONENT_PARAM = "authorizationComponent";
    private UserAuthorization authorizationComponent;

    @Override
    public Class<GroupHierarchy> getHomePage() {
        return GroupHierarchy.class;
    }

    @Override
    protected void init() {
        addComponentInstantiationListener(new JavaEEComponentInjector(this));
        //körök linkek
        mountBookmarkablePage("/showuser", ShowUser.class);
        mountBookmarkablePage("/userhistory", UserHistory.class);

        mountBookmarkablePage("/showgroup", ShowGroup.class);
        mountBookmarkablePage("/grouphistory", GroupHistory.class);
        mountBookmarkablePage("/addgroupmember", AddGroupMember.class);
        mountBookmarkablePage("/editgroupinfo", EditGroupInfo.class);
        mountBookmarkablePage("/changepost", ChangePost.class);

        mountBookmarkablePage("/valuation", Valuations.class);
        mountBookmarkablePage("/newvaluation", NewValuation.class);

        mountBookmarkablePage("/consider", ConsiderPage.class);
        mountBookmarkablePage("/editsettings", EditSettings.class);
        mountBookmarkablePage("/logout", Logout.class);
        mount("/error", PackageName.forClass(InternalServerError.class));

        mountBookmarkablePage("/profile", ShowPersonPage.class);

        getApplicationSettings().setInternalErrorPage(InternalServerError.class);
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredError.class);
        getMarkupSettings().setStripWicketTags(true);
        getPageSettings().setAutomaticMultiWindowSupport(false);

        String classname = getInitParameter(AUTHZ_COMPONENT_PARAM);
        try {
            authorizationComponent = Class.forName(classname).
                    asSubclass(UserAuthorization.class).newInstance();
            authorizationComponent.init(this);
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot instantiate authorization component" +
                    classname, ex);
        }
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new VirSession(request);
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = new ConverterLocator();
        locator.set(EntrantType.class, new EntrantTypeConverter());
        locator.set(ValuationStatus.class, new ValuationStatusConverter());
        locator.set(MembershipType.class, new MembershipTypeConverter());
        locator.set(MembershipType[].class, new MembershipTypeConverter());

        return locator;
    }

    public UserAuthorization getAuthorizationComponent() {
        return authorizationComponent;
    }
}
