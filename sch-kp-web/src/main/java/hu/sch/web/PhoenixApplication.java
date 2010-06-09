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
package hu.sch.web;

import hu.sch.domain.EntrantType;
import hu.sch.domain.Membership;
import hu.sch.domain.ValuationStatus;
import hu.sch.domain.config.Configuration;
import hu.sch.domain.config.Configuration.Environment;
import hu.sch.services.TimerServiceLocal;
import hu.sch.web.authz.AgentBasedAuthorization;
import hu.sch.web.authz.DummyAuthorization;
import hu.sch.web.kp.pages.group.EditGroupInfo;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.kp.pages.group.ShowGroup;
import hu.sch.web.kp.pages.group.GroupHistory;
import hu.sch.web.kp.pages.logout.Logout;
import hu.sch.web.kp.pages.user.UserHistory;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.idm.pages.UserNameReminder;
import hu.sch.web.error.InternalServerError;
import hu.sch.web.error.PageExpiredError;
import hu.sch.web.idm.pages.RegistrationFinishedPage;
import hu.sch.web.idm.pages.RegistrationPage;
import hu.sch.web.kp.pages.admin.CreateGroup;
import hu.sch.web.kp.pages.admin.CreateNewPerson;
import hu.sch.web.kp.pages.admin.EditSettings;
import hu.sch.web.kp.pages.admin.ShowInactive;
import hu.sch.web.kp.pages.consider.ConsiderPage;
import hu.sch.web.kp.pages.valuation.Valuations;
import hu.sch.web.kp.pages.valuation.NewValuation;
import hu.sch.web.kp.pages.group.ChangeDelegates;
import hu.sch.web.kp.pages.group.ChangePost;
import hu.sch.web.kp.pages.group.GroupHierarchy;
import hu.sch.web.kp.pages.search.SearchResultsPage;
import hu.sch.web.kp.pages.svie.SvieAccount;
import hu.sch.web.kp.pages.svie.SvieGroupMgmt;
import hu.sch.web.kp.pages.svie.SvieUserMgmt;
import hu.sch.web.profile.pages.confirmation.ConfirmPage;
import hu.sch.web.session.VirSession;
import hu.sch.web.wicket.util.EntrantTypeConverter;
import hu.sch.web.wicket.util.PostTypeConverter;
import hu.sch.web.wicket.util.ValuationStatusConverter;
import hu.sch.web.wicket.util.ServerTimerFilter;
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
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.apache.wicket.util.convert.ConverterLocator;
import org.apache.wicket.util.lang.PackageName;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;

/**
 *
 * @author hege
 */
public class PhoenixApplication extends WebApplication {

    private static Logger log = Logger.getLogger(PhoenixApplication.class);
    private UserAuthorization authorizationComponent;

    /**
     * Mivel nem elég a jelenleg támogatott kétféle configurationType
     * (DEPLOYMENT és DEVELOPMENT), ezért
     * felüldefiniáljuk ezt a metódust és az {@link Environment}-től függően térünk vissza
     * az előbbiek valamelyikével.
     *
     * @return  DEPLOYMENT vagy DEVELOPMENT
     */
    @Override
    public String getConfigurationType() {
        Environment env = Configuration.getEnvironment();

        if (env == Environment.DEVELOPMENT || env == Environment.STAGING) {
            return DEVELOPMENT;
        } else {
            // jelenleg csak akkor kell DEPLOYMENT, ha az environment PRODUCTION
            return DEPLOYMENT;
        }
    }

    @Override
    public Class<ShowUser> getHomePage() {
        return ShowUser.class;
    }

    @Override
    protected void init() {
        addComponentInstantiationListener(new JavaEEComponentInjector(this));

        //autorizációs komponens inicializálása
        if (Configuration.getEnvironment() == Environment.DEVELOPMENT) {
            authorizationComponent = new DummyAuthorization();
        } else { // ha STAGING vagy PRODUCTION, akkor az AgentBased kell nekünk
            authorizationComponent = new AgentBasedAuthorization();
        }

        //körök linkek
        mountBookmarkablePage("/showuser", ShowUser.class);
        mountBookmarkablePage("/userhistory", UserHistory.class);
        mountBookmarkablePage("/search", SearchResultsPage.class);
        mountBookmarkablePage("/confirm", ConfirmPage.class);

        mountBookmarkablePage("/showgroup", ShowGroup.class);
        mountBookmarkablePage("/grouphierarchy", GroupHierarchy.class);
        mountBookmarkablePage("/grouphistory", GroupHistory.class);
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

        //IDM linkek
        mountBookmarkablePage("/reminder", UserNameReminder.class);
        mount(new HybridUrlCodingStrategy("/register", RegistrationPage.class));
        mountBookmarkablePage("/registerfinished", RegistrationFinishedPage.class);
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
                    if (ex instanceof PageExpiredException) {
                        return new PageExpiredError();
                    }
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
        locator.set(Membership.class, new PostTypeConverter());

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
