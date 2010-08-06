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
import org.apache.wicket.util.lang.PackageName;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;
import org.wicketstuff.javaee.naming.global.AppJndiNamingStrategy;
import org.wicketstuff.javaee.naming.global.GlobalJndiNamingStrategy;

/**
 *
 * @author messo
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

        //alkalmazás beállítások
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredError.class);
        getPageSettings().setAutomaticMultiWindowSupport(false);

        //Ha dev módban vagyunk, akkor hozzáteszünk egy új filtert, ami mutatja
        //a render időket a log fájlban.
        if (getConfigurationType().equals(DEVELOPMENT)) {
            getRequestCycleSettings().addResponseFilter(new ServerTimerFilter());
            log.info("Successfully enabled ServerTimerFilter");
        }

        log.warn("Application has been successfully initiated");
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
