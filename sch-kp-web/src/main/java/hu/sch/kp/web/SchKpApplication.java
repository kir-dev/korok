/*
 * SchKpApplication.java
 * 
 * Created on Aug 24, 2007, 5:34:20 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hu.sch.kp.web;

import hu.sch.kp.web.util.BelepoTipusConverter;
import hu.sch.domain.BelepoTipus;
import hu.sch.domain.ErtekelesStatusz;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.web.pages.group.EditGroupInfo;
import hu.sch.kp.web.pages.user.ShowUser;
import hu.sch.kp.web.pages.group.ShowGroup;
import hu.sch.kp.web.pages.group.GroupHierarchy;
import hu.sch.kp.web.pages.group.GroupHistory;
import hu.sch.kp.web.pages.logout.Logout;
import hu.sch.kp.web.pages.user.UserHistory;
import hu.sch.kp.web.session.VirSession;
import hu.sch.kp.web.util.ErtekelesStatuszConverter;
import hu.sch.kp.web.util.TagsagTipusConverter;
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
public class SchKpApplication extends WebApplication {

    public Class getHomePage() {
        return GroupHierarchy.class;
    }

    @Override
    protected void init() {
        addComponentInstantiationListener(new JavaEEComponentInjector(this));

//        mount("/index", PackageName.forClass(Index.class));
//        mount("/user", PackageName.forClass(ShowUser.class));
//        mount("/group", PackageName.forClass(ShowGroup.class));
//        mount("/Valuation", PackageName.forClass(Ertekelesek.class));
//        mount("/PointRequests", PackageName.forClass(PontIgeny.class));
//        mount("/AccessRequests", PackageName.forClass(BelepoIgeny.class));
        //mount("/admin", PackageName.forClass(EditSemesterPage.class));
//        mount("/Consider", PackageName.forClass(OsszesErtekeles.class));
//        mountBookmarkablePage("/EditSettings", EditSettings.class);
        //mountBookmarkablePage("/Valuation", Ertekelesek.class);
        /*        mountBookmarkablePage("/grouphierarchy", GroupHierarchy.class);
        mountBookmarkablePage("/group", ShowGroup.class);
        mountBookmarkablePage("/user123", ShowUser.class);*/
        mountBookmarkablePage("/logout", Logout.class);
        mountBookmarkablePage("/userhistory", UserHistory.class);
        mountBookmarkablePage("/grouphistory", GroupHistory.class);
        mountBookmarkablePage("/editgroupinfo", EditGroupInfo.class);
        mountBookmarkablePage("/showuser", ShowUser.class);
        mountBookmarkablePage("/showgroup", ShowGroup.class);

        getMarkupSettings().setStripWicketTags(true);
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new VirSession(request);
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = new ConverterLocator();
        locator.set(BelepoTipus.class, new BelepoTipusConverter());
        locator.set(ErtekelesStatusz.class, new ErtekelesStatuszConverter());
        locator.set(TagsagTipus.class, new TagsagTipusConverter());

        return locator;
    }
}
