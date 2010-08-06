/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web;

import hu.sch.domain.config.Configuration;
import hu.sch.domain.config.Configuration.Environment;
import hu.sch.web.authz.AgentBasedAuthorization;
import hu.sch.web.authz.DummyAuthorization;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.error.InternalServerError;
import hu.sch.web.error.PageExpiredError;
import hu.sch.web.session.VirSession;
import hu.sch.web.wicket.util.ServerTimerFilter;
import org.apache.log4j.Logger;
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
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;
import org.wicketstuff.javaee.naming.global.AppJndiNamingStrategy;
import org.wicketstuff.javaee.naming.global.GlobalJndiNamingStrategy;

/**
 *
 * @author balint
 */
public abstract class AbstractPekApplication extends WebApplication {

    private static final String EJB_MODULE_NAME = "korok-ejb";
    private UserAuthorization authorizationComponent;
    private static Logger log = Logger.getLogger(AbstractPekApplication.class);

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

        onInitialization();

        //Ha dev módban vagyunk, akkor hozzáteszünk egy új filtert, ami mutatja
        //a render időket a log fájlban.
        if (getConfigurationType().equals(DEVELOPMENT)) {
            getRequestCycleSettings().addResponseFilter(new ServerTimerFilter());
            log.info("Successfully enabled ServerTimerFilter");
        }
    }

    public boolean isNewbieTime() {
        // FIXME -- ezt kéne valahonnan máshonnan szerezni
        return true;
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

    protected abstract void onInitialization();
}
