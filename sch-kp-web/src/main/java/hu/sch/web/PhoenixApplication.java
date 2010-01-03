/**
 * Copyright (c) 2009, Peter Major
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
package hu.sch.web;

import hu.sch.domain.EntrantType;
import hu.sch.domain.ValuationStatus;
import hu.sch.domain.config.Configuration;
import hu.sch.services.TimerServiceLocal;
import hu.sch.web.kp.pages.group.EditGroupInfo;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.kp.pages.group.ShowGroup;
import hu.sch.web.kp.pages.group.GroupHistory;
import hu.sch.web.kp.pages.logout.Logout;
import hu.sch.web.kp.pages.user.UserHistory;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.common.pages.UserNameReminder;
import hu.sch.web.error.InternalServerError;
import hu.sch.web.error.PageExpiredError;
import hu.sch.web.kp.pages.admin.CreateGroup;
import hu.sch.web.kp.pages.admin.CreateNewPerson;
import hu.sch.web.kp.pages.admin.EditSettings;
import hu.sch.web.kp.pages.admin.ShowInactive;
import hu.sch.web.kp.pages.consider.ConsiderPage;
import hu.sch.web.kp.pages.valuation.Valuations;
import hu.sch.web.kp.pages.valuation.NewValuation;
import hu.sch.web.kp.pages.group.AddGroupMember;
import hu.sch.web.kp.pages.group.ChangeDelegates;
import hu.sch.web.kp.pages.group.ChangePost;
import hu.sch.web.kp.pages.group.GroupHierarchy;
import hu.sch.web.kp.pages.svie.SvieAccount;
import hu.sch.web.kp.pages.svie.SvieGroupMgmt;
import hu.sch.web.kp.pages.svie.SvieUserMgmt;
import hu.sch.web.session.VirSession;
import hu.sch.web.kp.util.EntrantTypeConverter;
import hu.sch.web.kp.util.PostTypeConverter;
import hu.sch.web.kp.util.ValuationStatusConverter;
import hu.sch.web.kp.util.ServerTimerFilter;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.convert.ConverterLocator;
import org.apache.wicket.util.lang.PackageName;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;

/**
 *
 * @author hege
 */
public class PhoenixApplication extends WebApplication {

    private static final String AUTHZ_COMPONENT_PARAM = "authorizationComponent";
    private static Logger log = Logger.getLogger(PhoenixApplication.class);
    private UserAuthorization authorizationComponent;

    @Override
    public Class<ShowUser> getHomePage() {
        return ShowUser.class;
    }

    @Override
    protected void init() {
        addComponentInstantiationListener(new JavaEEComponentInjector(this));

        //autorizációs komponens inicializálása
        String classname = getInitParameter(AUTHZ_COMPONENT_PARAM);
        try {
            authorizationComponent = Class.forName(classname).
                    asSubclass(UserAuthorization.class).newInstance();
            authorizationComponent.init(this);
        } catch (Exception ex) {
            log.fatal("Failed to initialize authorization", ex);
            throw new IllegalStateException("Cannot instantiate authorization component"
                    + classname, ex);
        }

        //Beállítások beolvasása az alkalmazás properties fájljából
        Configuration.init();

        //körök linkek
        mountBookmarkablePage("/showuser", ShowUser.class);
        mountBookmarkablePage("/userhistory", UserHistory.class);
        mountBookmarkablePage("/reminder", UserNameReminder.class);

        mountBookmarkablePage("/showgroup", ShowGroup.class);
        mountBookmarkablePage("/grouphierarchy", GroupHierarchy.class);
        mountBookmarkablePage("/grouphistory", GroupHistory.class);
        mountBookmarkablePage("/addgroupmember", AddGroupMember.class);
        mountBookmarkablePage("/editgroupinfo", EditGroupInfo.class);
        mountBookmarkablePage("/changepost", ChangePost.class);

        mountBookmarkablePage("/valuation", Valuations.class);
        mountBookmarkablePage("/newvaluation", NewValuation.class);

        mountBookmarkablePage("/svieaccount", SvieAccount.class);
        mountBookmarkablePage("/delegates", ChangeDelegates.class);
        mountBookmarkablePage("/consider", ConsiderPage.class);
        mountBookmarkablePage("/administration", EditSettings.class);
        mountBookmarkablePage("/administration/svieusermgmt", SvieUserMgmt.class);
        mountBookmarkablePage("/administration/sviegroupmgmt", SvieGroupMgmt.class);
        mountBookmarkablePage("/showinactive", ShowInactive.class);
        mountBookmarkablePage("/creategroup", CreateGroup.class);
        mountBookmarkablePage("/createperson", CreateNewPerson.class);

        mountBookmarkablePage("/logout", Logout.class);


        mount("/error", PackageName.forClass(InternalServerError.class));

//        mountBookmarkablePage("/profile", ShowPersonPage.class);
//        mountBookmarkablePage("profile/edit", EditPage.class);

        //alkalmazás beállítások
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredError.class);
        getMarkupSettings().setStripWicketTags(true);
        getPageSettings().setAutomaticMultiWindowSupport(false);

        //Ha dev módban vagyunk, akkor hozzáteszünk egy új filtert, ami mutatja
        //a render időket a log fájlban.
        if (getConfigurationType().equals(DEVELOPMENT)) {
            getRequestCycleSettings().addResponseFilter(new ServerTimerFilter());
            log.info("Successfully enabled ServerTimerFilter");
        }

        //TimerService-ek inicializálása
        TimerServiceLocal timerService = lookupTimerServiceBean();
        timerService.scheduleTimers();

        log.warn("Application has been successfully initiated");
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new VirSession(request);
    }

    @Override
    public RequestCycle newRequestCycle(Request request, Response response) {
        if (getConfigurationType().equals(DEVELOPMENT)) {
            return super.newRequestCycle(request, response);
        } else {
            return new WebRequestCycle(this, (WebRequest) request, (WebResponse) response) {

                @Override
                public Page onRuntimeException(Page page, RuntimeException ex) {
                    return new InternalServerError(page, ex);
                }
            };
        }
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = new ConverterLocator();
        locator.set(EntrantType.class, new EntrantTypeConverter());
        locator.set(ValuationStatus.class, new ValuationStatusConverter());
        locator.set(List.class, new PostTypeConverter());

        return locator;
    }

    public UserAuthorization getAuthorizationComponent() {
        return authorizationComponent;
    }

    private TimerServiceLocal lookupTimerServiceBean() {
        try {
            Context c = new InitialContext();
            return (TimerServiceLocal) c.lookup("java:comp/env/TimerServiceLocal");
        } catch (NamingException ne) {
            log.error("Error while lookup for TimerService", ne);
            throw new RuntimeException(ne);
        }
    }
}
