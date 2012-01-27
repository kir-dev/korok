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
import hu.sch.web.kp.group.*;
import hu.sch.web.kp.logout.Logout;
import hu.sch.web.kp.search.SearchResultsPage;
import hu.sch.web.kp.svie.SvieAccount;
import hu.sch.web.kp.svie.SvieGroupMgmt;
import hu.sch.web.kp.svie.SvieUserMgmt;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.kp.user.UserHistory;
import hu.sch.web.kp.valuation.NewValuation;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.kp.valuation.ValuationHistory;
import hu.sch.web.kp.valuation.Valuations;
import hu.sch.web.kp.valuation.message.ValuationMessages;
import hu.sch.web.kp.valuation.request.entrant.EntrantRequests;
import hu.sch.web.kp.valuation.request.point.PointRequests;
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
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.wicket.*;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
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
        String url = ((HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest()).getRequestURL().toString();

        if (url.contains("profile")) {
            return ShowPersonPage.class;
        }

        return ShowUser.class;
    }

    /**
     * Mivel nem elég a jelenleg támogatott kétféle configurationType
     * (DEPLOYMENT és DEVELOPMENT), ezért felüldefiniáljuk ezt a metódust és az {@link Environment}-től
     * függően térünk vissza az előbbiek valamelyikével.
     *
     * @return DEPLOYMENT vagy DEVELOPMENT
     */
    @Override
    public RuntimeConfigurationType getConfigurationType() {
        Environment env = Configuration.getEnvironment();

        if (env == Environment.PRODUCTION) {
            // jelenleg csak akkor kell DEPLOYMENT, ha az environment PRODUCTION
            return RuntimeConfigurationType.DEPLOYMENT;
        } else {
            return RuntimeConfigurationType.DEVELOPMENT;
        }
    }

    @Override
    protected void init() {
        //A környezetfüggő beállítások elvégzése
        Environment env = Configuration.getEnvironment();
        if (env == Environment.TESTING) {
            getComponentInstantiationListeners().add(new JavaEEComponentInjector(this, new GlobalJndiNamingStrategy(EJB_MODULE_NAME)));
            authorizationComponent = new DummyAuthorization();
        } else {
            getComponentInstantiationListeners().add(new JavaEEComponentInjector(this, new AppJndiNamingStrategy(EJB_MODULE_NAME)));
            if (env == Environment.DEVELOPMENT) {
                //Ha DEVELOPMENT környezetben vagyunk, akkor Dummyt használunk
                authorizationComponent = new DummyAuthorization();
            } else { // ha STAGING vagy PRODUCTION, akkor az AgentBased kell nekünk
                authorizationComponent = new AgentBasedAuthorization();
            }
        }

        if (Configuration.getEnvironment().equals(Environment.PRODUCTION)) {
            setRequestCycleProvider(new IRequestCycleProvider() {

                @Override
                public RequestCycle get(RequestCycleContext c) {
                    c.setExceptionMapper(new DefaultExceptionMapper());
                    return new RequestCycle(c);
                }
            });
        }

        getMarkupSettings().setStripWicketTags(true);

        mountPackage("/error", InternalServerError.class);
        mountPage("/loggedout", Logout.class);
        mountKorok();
        mountProfil();

        //alkalmazás beállítások
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredError.class);
        // getPageSettings().setAutomaticMultiWindowSupport(false);

        //Ha dev módban vagyunk, akkor hozzáteszünk egy új filtert, ami mutatja
        //a render időket a log fájlban.
        if (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
            getRequestCycleSettings().addResponseFilter(new ServerTimerFilter());
            log.info("Successfully enabled ServerTimerFilter");
        }

        Injector.get().inject(this);
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

    public UserAuthorization getAuthorizationComponent() {
        return authorizationComponent;
    }

    private void mountKorok() {
        mountPage("/korok", ShowUser.class);
        mountPage("/korok/showuser", ShowUser.class);
        mountPage("/korok/userhistory", UserHistory.class);
        mountPage("/korok/search", SearchResultsPage.class);
        mountPage("/korok/confirm", ConfirmPage.class);

        mountPage("/korok/showgroup", ShowGroup.class);
        mountPage("/korok/grouphierarchy", GroupHierarchy.class);
        mountPage("/korok/grouphistory", GroupHistory.class);
        mountPage("/korok/editgroupinfo", EditGroupInfo.class);
        mountPage("/korok/changepost", ChangePost.class);

        mountPage("/korok/valuation", Valuations.class);
        mountPage("/korok/valuationdetails", ValuationDetails.class);
        mountPage("/korok/valuationhistory", ValuationHistory.class);
        mountPage("/korok/newvaluation", NewValuation.class);
        mountPage("/korok/valuationmessages", ValuationMessages.class);

        mountPage("/korok/pointrequests", PointRequests.class);
        mountPage("/korok/entrantrequests", EntrantRequests.class);

        mountPage("/korok/svieaccount", SvieAccount.class);
        mountPage("/korok/delegates", ChangeDelegates.class);
        mountPage("/korok/consider", ConsiderPage.class);
        mountPage("/korok/administration", EditSettings.class);
        mountPage("/korok/administration/svieusermgmt", SvieUserMgmt.class);
        mountPage("/korok/administration/sviegroupmgmt", SvieGroupMgmt.class);
        mountPage("/korok/showinactive", ShowInactive.class);
        mountPage("/korok/creategroup", CreateGroup.class);
        mountPage("/korok/createperson", CreateNewPerson.class);

        //IDM linkek
        mountPage("/korok/reminder", UserNameReminder.class);
        //mount(new HybridUrlCodingStrategy("/korok/register", RegistrationPage.class));
        mountPage("/korok/register", RegistrationPage.class);
        mountPage("/korok/registerfinished", RegistrationFinishedPage.class);
        mountPage("/korok/logout", Logout.class);
    }

    private void mountProfil() {
        mountPage("/profile", ShowPersonPage.class);
        mountPage("/profile/show", ShowPersonPage.class);
        mountPage("/profile/edit", EditPage.class);
        mountPage("/profile/changepassword", ChangePasswordPage.class);
        mountPage("/profile/birthdays", BirthDayPage.class);
        mountPage("/profile/admin", AdminPage.class);
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
