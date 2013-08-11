package hu.sch.web.error;

import hu.sch.domain.user.User;
import hu.sch.services.SystemManagerLocal;
import hu.sch.web.PhoenixApplication;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.kp.KorokPage;
import java.util.EnumMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;

/**
 *
 * @author aldaris
 */
public final class InternalServerError extends KorokPage {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setHeaderLabelText("Hiba!");
        final SmartLinkLabel mailtoLink = new SmartLinkLabel("support", "https://support.sch.bme.hu");
        add(mailtoLink);
    }

    public InternalServerError(final RequestCycle cycle, final IPageRequestHandler handler,
            final Exception ex) {

        final Class<? extends IRequestablePage> pageClass = handler.getPageClass();
        final PageParameters pageParameters = handler.getPageParameters();
        final Request request = cycle.getRequest();

        final HttpServletRequest servletRequest =
                (HttpServletRequest) request.getContainerRequest();

        final UserAuthorization authComponent =
                ((PhoenixApplication) getApplication()).getAuthorizationComponent();

        final User userAttributes = authComponent.getUserAttributes(request);

        final Map<SystemManagerLocal.EXC_REPORT_KEYS, String> exceptionParams =
                new EnumMap(SystemManagerLocal.EXC_REPORT_KEYS.class);

        if (pageClass != null) {
            exceptionParams.put(SystemManagerLocal.EXC_REPORT_KEYS.PAGE_NAME,
                    pageClass.getName());
        }

        exceptionParams.put(SystemManagerLocal.EXC_REPORT_KEYS.PAGE_PATH,
                request.getClientUrl().toString());

        if (pageParameters != null) {
            exceptionParams.put(SystemManagerLocal.EXC_REPORT_KEYS.PAGE_PARAMS,
                    pageParameters.toString());
        }

        exceptionParams.put(SystemManagerLocal.EXC_REPORT_KEYS.REMOTE_USER,
                authComponent.getRemoteUser(request));

        exceptionParams.put(SystemManagerLocal.EXC_REPORT_KEYS.REMOTE_ADDRESS,
                servletRequest.getRemoteAddr());

        if (userAttributes != null) { //null with dummyauth
            exceptionParams.put(SystemManagerLocal.EXC_REPORT_KEYS.EMAIL,
                    userAttributes.getEmailAddress());
        }

        final Long userId = authComponent.getUserid(request);
        if (userId != null) {
            exceptionParams.put(SystemManagerLocal.EXC_REPORT_KEYS.VIRID,
                    userId.toString());
        }

        exceptionParams.put(SystemManagerLocal.EXC_REPORT_KEYS.EXCEPTION,
                Strings.toString(ex));

        systemManager.sendExceptionReportMail(exceptionParams);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }
}
