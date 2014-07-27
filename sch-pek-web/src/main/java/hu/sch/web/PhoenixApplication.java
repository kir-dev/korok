package hu.sch.web;

import hu.sch.domain.enums.EntrantType;
import hu.sch.domain.Membership;
import hu.sch.domain.enums.ValuationStatus;
import hu.sch.services.config.Configuration;
import hu.sch.services.config.Configuration.Environment;
import hu.sch.services.SystemManagerLocal;
import hu.sch.web.authz.SessionBasedAuthorization;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.dev.DevSettingsPage;
import hu.sch.web.error.Forbidden;
import hu.sch.web.error.InternalServerError;
import hu.sch.web.error.NotFound;
import hu.sch.web.error.PageExpiredError;
import hu.sch.web.idm.pages.RegistrationFinishedPage;
import hu.sch.web.idm.pages.RegistrationPage;
import hu.sch.web.idm.pages.CredentialsReminder;
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
import hu.sch.web.profile.confirmation.ReplaceLostPasswordPage;
import hu.sch.web.profile.edit.EditPage;
import hu.sch.web.profile.passwordchange.ChangePasswordPage;
import hu.sch.web.profile.show.ShowPersonPage;
import hu.sch.web.session.VirSession;
import hu.sch.web.wicket.util.EntrantTypeConverter;
import hu.sch.web.wicket.util.PostTypeConverter;
import hu.sch.web.wicket.util.ServerTimerFilter;
import hu.sch.web.wicket.util.ValuationStatusConverter;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.*;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.PageRequestHandlerTracker;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.UrlPathPageParametersEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static Logger log = LoggerFactory.getLogger(PhoenixApplication.class);
    @Inject
    private SystemManagerLocal systemManager;
    @Inject
    private Configuration config;
    private UserAuthorization authorizationComponent;
    private boolean isNewbieTime;
    private boolean cdiInitialized = false;
    private RuntimeConfigurationType env = null;

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
        if (this.env == null) {
            // must initialize wicket-cdi here, because this metods get called
            // before init() during application startup
            initEjbInjects();

            Environment env = config.getEnvironment();
            if (env == Environment.PRODUCTION) {
                // jelenleg csak akkor kell DEPLOYMENT, ha az environment PRODUCTION
                this.env = RuntimeConfigurationType.DEPLOYMENT;
            } else {
                this.env = RuntimeConfigurationType.DEVELOPMENT;
            }
        }
        return this.env;
    }

    @Override
    protected void init() {
        setAuthorizationComponent(new SessionBasedAuthorization());
        setRequestCycleProvider(new PekRequestCycleProvider(config));

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

        isNewbieTime = systemManager.getNewbieTime();

        //tinymce workaround. see: https://github.com/wicketstuff/core/issues/113
        final SecurePackageResourceGuard guard =
                (SecurePackageResourceGuard) getResourceSettings().getPackageResourceGuard();
        guard.addPattern("+*.htm");

        log.warn("Application has been successfully initiated");
    }

    /**
     * This does the magic so we can inject EJBs with
     *
     * @Inject annotation. We are using wicket-cdi to integrate the dependency
     * injection to Wicket.
     */
    private void initEjbInjects() {
        if (cdiInitialized) {
            return;
        }
        cdiInitialized = true;

        BeanManager bm;
        try {
            bm = (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
        } catch (NamingException e) {
            throw new IllegalStateException("Unable to obtain CDI BeanManager", e);
        }

        // configure wicket/cdi
        new CdiConfiguration(bm).configure(this);
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

    public void setAuthorizationComponent(UserAuthorization authorizationComponent) {
        this.authorizationComponent = authorizationComponent;
        this.authorizationComponent.init(this);
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
        mountPageWithPath("/korok/reminder", CredentialsReminder.class);
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
        mountPageWithPath("/profile/replacelostpassword", ReplaceLostPasswordPage.class);

        if (config.getEnvironment() == Environment.DEVELOPMENT) {
            mountPageWithPath("/dev", DevSettingsPage.class);
        }
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
        if (Environment.PRODUCTION.equals(config.getEnvironment())) {
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
