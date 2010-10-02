/**
 * Copyright (c) 2008-2010, Peter Major
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
import hu.sch.services.SystemManagerLocal;
import hu.sch.web.authz.AgentBasedAuthorization;
import hu.sch.web.authz.DummyAuthorization;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.error.InternalServerError;
import hu.sch.web.error.PageExpiredError;
import hu.sch.web.idm.pages.RegistrationFinishedPage;
import hu.sch.web.idm.pages.RegistrationPage;
import hu.sch.web.idm.pages.UserNameReminder;
import hu.sch.web.kp.admin.CreateGroup;
import hu.sch.web.kp.admin.CreateNewPerson;
import hu.sch.web.kp.admin.EditSettings;
import hu.sch.web.kp.admin.ShowInactive;
import hu.sch.web.kp.consider.ConsiderPage;
import hu.sch.web.kp.group.ChangeDelegates;
import hu.sch.web.kp.group.ChangePost;
import hu.sch.web.kp.group.EditGroupInfo;
import hu.sch.web.kp.group.GroupHierarchy;
import hu.sch.web.kp.group.GroupHistory;
import hu.sch.web.kp.group.ShowGroup;
import hu.sch.web.kp.logout.Logout;
import hu.sch.web.kp.search.SearchResultsPage;
import hu.sch.web.kp.svie.SvieAccount;
import hu.sch.web.kp.svie.SvieGroupMgmt;
import hu.sch.web.kp.svie.SvieUserMgmt;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.kp.user.UserHistory;
import hu.sch.web.kp.valuation.request.entrant.EntrantRequests;
import hu.sch.web.kp.valuation.NewValuation;
import hu.sch.web.kp.valuation.request.point.PointRequests;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.kp.valuation.ValuationHistory;
import hu.sch.web.kp.valuation.Valuations;
import hu.sch.web.kp.valuation.message.ValuationMessages;
import hu.sch.web.profile.admin.AdminPage;
import hu.sch.web.profile.birthday.BirthDayPage;
import hu.sch.web.profile.confirmation.ConfirmPage;
import hu.sch.web.profile.edit.EditPage;
import hu.sch.web.profile.passwordchange.ChangePasswordPage;
import hu.sch.web.profile.show.ShowPersonPage;
import hu.sch.web.session.VirSession;
import hu.sch.web.wicket.util.EntrantTypeConverter;
import hu.sch.web.wicket.util.PostTypeConverter;
import hu.sch.web.wicket.util.ServerTimerFilter;
import hu.sch.web.wicket.util.ValuationStatusConverter;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.apache.wicket.util.convert.ConverterLocator;
import org.apache.wicket.util.lang.PackageName;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;
import org.wicketstuff.javaee.naming.global.AppJndiNamingStrategy;
import org.wicketstuff.javaee.naming.global.GlobalJndiNamingStrategy;

/**
 * PhoenixApplication, amelyben a Phoenix arra utal, hogy ez az alkalmazás a VIR
 * hamvaiból éledt újjá, és az idő folyamán a cél egy a régi VIR-hez
 * valamennyire hasonlító közösségi portál megtestesítése.
 *
 * @author aldaris
 * @author hege
 * @author messo
 */
public class PhoenixApplication extends WebApplication {

    private static final String EJB_MODULE_NAME = "korok-ejb";
    private static Logger log = Logger.getLogger(PhoenixApplication.class);
    @EJB(name = "SystemManagerBean")
    private SystemManagerLocal systemManager;
    private UserAuthorization authorizationComponent;
    private boolean isNewbieTime;

    @Override
    public Class<? extends Page> getHomePage() {
        return ShowUser.class;
    }

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

        if (env == Environment.PRODUCTION) {
            // jelenleg csak akkor kell DEPLOYMENT, ha az environment PRODUCTION
            return DEPLOYMENT;
        } else {
            return DEVELOPMENT;
        }
    }

    @Override
    protected void init() {
        //A környezetfüggő beállítások elvégzése
        Environment env = Configuration.getEnvironment();
        if (env == Environment.TESTING) {
            addComponentInstantiationListener(new JavaEEComponentInjector(this,
                    new GlobalJndiNamingStrategy(EJB_MODULE_NAME)));
            authorizationComponent = new DummyAuthorization();
        } else {
            addComponentInstantiationListener(new JavaEEComponentInjector(this, new AppJndiNamingStrategy(EJB_MODULE_NAME)));
            if (env == Environment.DEVELOPMENT) {
                //Ha DEVELOPMENT környezetben vagyunk, akkor Dummyt használunk
                authorizationComponent = new DummyAuthorization();
            } else { // ha STAGING vagy PRODUCTION, akkor az AgentBased kell nekünk
                authorizationComponent = new AgentBasedAuthorization();
            }
        }

        getMarkupSettings().setStripWicketTags(true);

        mount("/error", PackageName.forClass(InternalServerError.class));
        mountKorok();
        mountProfil();

        //alkalmazás beállítások
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredError.class);
        getPageSettings().setAutomaticMultiWindowSupport(false);

        //Ha dev módban vagyunk, akkor hozzáteszünk egy új filtert, ami mutatja
        //a render időket a log fájlban.
        if (getConfigurationType().equals(DEVELOPMENT)) {
            getRequestCycleSettings().addResponseFilter(new ServerTimerFilter());
            log.info("Successfully enabled ServerTimerFilter");
        }

        InjectorHolder.getInjector().inject(this);
        isNewbieTime = systemManager.getNewbieTime();

        log.warn("Application has been successfully initiated");
    }

    public boolean isNewbieTime() {
        return isNewbieTime;
    }

    public void setNewbieTime(boolean newbieTime) {
        isNewbieTime = newbieTime;
    }

    @Override
    public Session newSession(Request request, Response response) {
        Session session = new VirSession(request);
        if (isNewbieTime()) {
            session.setStyle("newbie");
        }
        return session;
    }

    @Override
    public RequestCycle newRequestCycle(Request request, Response response) {
        if (!Configuration.getEnvironment().equals(Environment.PRODUCTION)) {
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

    public UserAuthorization getAuthorizationComponent() {
        return authorizationComponent;
    }

    private void mountKorok() {
        mountBookmarkablePage("/", ShowUser.class);
        mountBookmarkablePage("/korok", ShowUser.class);
        mountBookmarkablePage("/korok/showuser", ShowUser.class);
        mountBookmarkablePage("/korok/userhistory", UserHistory.class);
        mountBookmarkablePage("/korok/search", SearchResultsPage.class);
        mountBookmarkablePage("/korok/confirm", ConfirmPage.class);

        mountBookmarkablePage("/korok/showgroup", ShowGroup.class);
        mountBookmarkablePage("/korok/grouphierarchy", GroupHierarchy.class);
        mountBookmarkablePage("/korok/grouphistory", GroupHistory.class);
        mountBookmarkablePage("/korok/editgroupinfo", EditGroupInfo.class);
        mountBookmarkablePage("/korok/changepost", ChangePost.class);

        mountBookmarkablePage("/korok/valuation", Valuations.class);
        mountBookmarkablePage("/korok/valuationdetails", ValuationDetails.class);
        mountBookmarkablePage("/korok/valuationhistory", ValuationHistory.class);
        mountBookmarkablePage("/korok/newvaluation", NewValuation.class);
        mountBookmarkablePage("/korok/valuationmessages", ValuationMessages.class);

        mountBookmarkablePage("/korok/pointrequests", PointRequests.class);
        mountBookmarkablePage("/korok/entrantrequests", EntrantRequests.class);

        mountBookmarkablePage("/korok/svieaccount", SvieAccount.class);
        mountBookmarkablePage("/korok/delegates", ChangeDelegates.class);
        mountBookmarkablePage("/korok/consider", ConsiderPage.class);
        mountBookmarkablePage("/korok/administration", EditSettings.class);
        mountBookmarkablePage("/korok/administration/svieusermgmt", SvieUserMgmt.class);
        mountBookmarkablePage("/korok/administration/sviegroupmgmt", SvieGroupMgmt.class);
        mountBookmarkablePage("/korok/showinactive", ShowInactive.class);
        mountBookmarkablePage("/korok/creategroup", CreateGroup.class);
        mountBookmarkablePage("/korok/createperson", CreateNewPerson.class);

        //IDM linkek
        mountBookmarkablePage("/korok/reminder", UserNameReminder.class);
        mount(new HybridUrlCodingStrategy("/korok/register", RegistrationPage.class));
        mountBookmarkablePage("/korok/registerfinished", RegistrationFinishedPage.class);
        mountBookmarkablePage("/korok/logout", Logout.class);
    }

    private void mountProfil() {
        mountBookmarkablePage("/profile", ShowPersonPage.class);
        mountBookmarkablePage("/profile/show", ShowPersonPage.class);
        mountBookmarkablePage("/profile/edit", EditPage.class);
        mountBookmarkablePage("/profile/changepassword", ChangePasswordPage.class);

        mountBookmarkablePage("/profile/birthdays", BirthDayPage.class);

        mountBookmarkablePage("/profile/admin", AdminPage.class);
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = new ConverterLocator();
        locator.set(EntrantType.class, new EntrantTypeConverter());
        locator.set(ValuationStatus.class, new ValuationStatusConverter());
        locator.set(Membership.class, new PostTypeConverter());

        return locator;
    }
}
