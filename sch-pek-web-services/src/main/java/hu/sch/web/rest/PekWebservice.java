package hu.sch.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PekWebservice.class);
    @Context
    private UriInfo context;
    protected ObjectMapper mapper = new ObjectMapper();

    protected void doAudit() {
        final StringBuilder auditMessage = new StringBuilder("AUDIT LOG for GET method. ");
        auditMessage.append(" URL: ");
        if (context != null && context.getRequestUri() != null) {
            auditMessage.append(context.getRequestUri().toString());
        } else {
            LOGGER.info("URIContext or RequestUri was null.");
            auditMessage.append("UNKNOWN");
        }
        LOGGER.info(auditMessage.toString());
    }

    protected void triggerErrorResponse(final Response.Status status) {
//        final StringBuilder errorMsg = new StringBuilder("{'error': '").append(msg).append("'}");
        final Response.ResponseBuilder resp = Response.status(status);
//        .entity(errorMsg.toString());

        throw new WebApplicationException(resp.build());
    }
}
