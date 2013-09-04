package hu.sch.web;

import hu.sch.domain.enums.EntrantType;
import hu.sch.domain.Membership;
import hu.sch.domain.enums.ValuationStatus;
import hu.sch.domain.config.Configuration;
import hu.sch.domain.config.Configuration.Environment;
import hu.sch.services.SystemManagerLocal;
import hu.sch.web.authz.AgentBasedAuthorization;
import hu.sch.web.authz.DummyAuthorization;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.error.Forbidden;
import hu.sch.web.error.InternalServerError;
import hu.sch.web.error.NotFound;
import hu.sch.web.error.PageExpiredError;
import hu.sch.web.idm.pages.RegistrationFinishedPage;
import hu.sch.web.idm.pages.RegistrationPage;
import hu.sch.web.idm.pages.UserNameReminder;
import hu.sch.web.kp.admin.CreateGroup;
import hu.sch.web.kp.admin.CreateNewPerson;
import hu.sch.web.kp.admin.EditSettings;
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
import org.apache.wicket.*;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.PageRequestHandlerTracker;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.mapper.parameter.UrlPathPageParametersEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger log = LoggerFactory.getLogger(PhoenixApplication.class);
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
     * (DEPLOYMENT és DEVELOPMENT), ezért felüldefiniáljuk ezt a metódust és az
     * {@link Environment}-től függően térünk vissza az előbbiek valamelyikével.
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

        setErrorHandling();

        //
        mountPage("/loggedout", Logout.class);
        mountPages();

        //Ha dev módban vagyunk, akkor hozzáteszünk egy új filtert, ami mutatja
        //a render időket a log fájlban.
        if (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
            getRequestCycleSettings().addResponseFilter(new ServerTimerFilter());
            log.info("Successfully enabled ServerTimerFilter");
        }

        Injector.get().inject(this);
        isNewbieTime = systemManager.getNewbieTime();

        //tinymce workaround. see: https://github.com/wicketstuff/core/issues/113
        final SecurePackageResourceGuard guard =
                (SecurePackageResourceGuard) getResourceSettings().getPackageResourceGuard();
        guard.addPattern("+*.htm");

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

    private void mountPages() {
        mountPageWithPath("/korok", ShowUser.class);
        mountPageWithPath("/korok/showuser", ShowUser.class);
        mountPageWithPath("/korok/userhistory", UserHistory.class);
        mountPageWithPath("/korok/search", SearchResultsPage.class);

        mountPageWithPath("/korok/showgroup", ShowGroup.class);
        mountPageWithPath("/korok/grouphierarchy", GroupHierarchy.class);
        mountPageWithPath("/korok/grouphistory", GroupHistory.class);
        mountPageWithPath("/korok/editgroupinfo", EditGroupInfo.class);
        mountPageWithPath("/korok/changepost", ChangePost.class);

        mountPageWithPath("/korok/valuation", Valuations.class);
        mountPageWithPath("/korok/valuationdetails", ValuationDetails.class);
        mountPageWithPath("/korok/valuationhistory", ValuationHistory.class);
        mountPageWithPath("/korok/newvaluation", NewValuation.class);
        mountPageWithPath("/korok/valuationmessages", ValuationMessages.class);

        mountPageWithPath("/korok/pointrequests", PointRequests.class);
        mountPageWithPath("/korok/entrantrequests", EntrantRequests.class);

        mountPageWithPath("/korok/svieaccount", SvieAccount.class);
        mountPageWithPath("/korok/delegates", ChangeDelegates.class);
        mountPageWithPath("/korok/consider", ConsiderPage.class);
        mountPageWithPath("/korok/administration", EditSettings.class);
        mountPageWithPath("/korok/administration/svieusermgmt", SvieUserMgmt.class);
        mountPageWithPath("/korok/administration/sviegroupmgmt", SvieGroupMgmt.class);
        mountPageWithPath("/korok/creategroup", CreateGroup.class);
        mountPageWithPath("/korok/createperson", CreateNewPerson.class);

        //IDM linkek
        mountPageWithPath("/korok/reminder", UserNameReminder.class);
        mountPageWithPath("/korok/register", RegistrationPage.class);
        mountPageWithPath("/korok/registerfinished", RegistrationFinishedPage.class);
        mountPageWithPath("/korok/logout", Logout.class);

        mountPageWithPath("/profile", ShowPersonPage.class);
        mountPageWithPath("/profile/show", ShowPersonPage.class);
        mountPageWithPath("/profile/edit", EditPage.class);
        mountPageWithPath("/profile/changepassword", ChangePasswordPage.class);
        mountPageWithPath("/profile/birthdays", BirthDayPage.class);
        mountPageWithPath("/profile/admin", AdminPage.class);
        mountPageWithPath("/profile/confirm", ConfirmPage.class);
    }

    /**
     * This needed because page mounting changed in wicket 1.5
     *
     * @see <a
     * href="https://cwiki.apache.org/WICKET/migration-to-wicket-15.html#MigrationtoWicket1.5-Pagemounting">
     * MigrationtoWicket1.5-Pagemounting</a>
     */
    private void mountPageWithPath(final String path, final Class<? extends IRequestablePage> pageClass) {
        mount(new MountedMapper(path, pageClass,
                new UrlPathPageParametersEncoder()));
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = new ConverterLocator();
        locator.set(EntrantType.class, new EntrantTypeConverter());
        locator.set(ValuationStatus.class, new ValuationStatusConverter());
        locator.set(Membership.class, new PostTypeConverter());

        return locator;
    }

    private void setErrorHandling() {

        mountPage("/error/Internal", InternalServerError.class);
        mountPage("/error/Forbidden", Forbidden.class);
        mountPage("/error/Expired", PageExpiredError.class);
        mountPage("/NotFound", NotFound.class);

        getApplicationSettings().setPageExpiredErrorPage(PageExpiredError.class);
        getApplicationSettings().setAccessDeniedPage(Forbidden.class);

        //custom error page and email report only in prod
        if (Environment.PRODUCTION.equals(Configuration.getEnvironment())) {
            //these need to get information about the requested page and the exception
            getRequestCycleListeners().add(new PageRequestHandlerTracker());
            getRequestCycleListeners().add(new AbstractRequestCycleListener() {
                @Override
                public IRequestHandler onException(final RequestCycle cycle, final Exception e) {
                    final IPageRequestHandler handler = PageRequestHandlerTracker.getLastHandler(cycle);
                    return new RenderPageRequestHandler(new PageProvider(new InternalServerError(cycle, handler, e)));
                }
            });
        }
    }
}
