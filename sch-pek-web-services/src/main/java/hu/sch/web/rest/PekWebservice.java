package hu.sch.web.rest;

import hu.sch.util.PatternHolder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author balo
 */
public abstract class PekWebservice {

    private static final Logger log = LoggerFactory.getLogger(PekWebservice.class);
    @Context
    HttpServletRequest requestContext;
    @Context
    private UriInfo context;

    protected void doAudit() {
        final StringBuilder auditMessage = new StringBuilder("AUDIT LOG for GET method. ");
        auditMessage.append(" URL: ");
        if (context != null && context.getRequestUri() != null) {
            auditMessage.append(context.getRequestUri().toString());
        } else {
            log.info("URIContext or RequestUri was null.");
            auditMessage.append("UNKNOWN");
        }

        auditMessage.append(" ; client IP: ").append(requestContext.getRemoteAddr());
        log.info(auditMessage.toString());
    }

    protected void triggerErrorResponse(final Response.Status status) {
        throw new WebApplicationException(Response.status(status).build());
    }

    protected void checkNeptun(final String neptun) {
        if (!PatternHolder.NEPTUN_PATTERN.matcher(neptun).matches()) {
            log.error("Webservice called with invalid neptun=" + neptun);
            triggerErrorResponse(Response.Status.BAD_REQUEST);
        }
    }
}
