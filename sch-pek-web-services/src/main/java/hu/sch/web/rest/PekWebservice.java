package hu.sch.web.rest;

import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import hu.sch.util.PatternHolder;
import java.util.UUID;
import javax.inject.Inject;
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
    @Inject
    private UserManagerLocal userManager;

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

    protected void checkUid(final String uid) {
        if (uid.length() < 2 || uid.length() > 20
                || !PatternHolder.UID_PATTERN.matcher(uid).matches()) {

            log.error("Webservice called with invalid uid={}", uid);
            triggerErrorResponse(Response.Status.BAD_REQUEST);
        }
    }

    protected void checkUUID(final String uuid) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException ex) {
            log.error("wrong uuid in url ({})", uuid);
            triggerErrorResponse(Response.Status.BAD_REQUEST);
        }
    }

    protected User findUserByNeptun(final String neptun) {
        final User user = userManager.findUserByNeptun(neptun, true);

        if (user == null) {
            log.info("User not found with neptun code={}", neptun);
            triggerErrorResponse(Response.Status.NOT_FOUND);
        }

        return user;
    }

    protected User findUserByUid(final String uid) {
        final User user = userManager.findUserByScreenName(uid);

        if (user == null) {
            log.info("User not found with login name={}", uid);
            triggerErrorResponse(Response.Status.NOT_FOUND);
        }

        return user;
    }

    protected User findUserByAuthSchId(final String id) {
        User user = userManager.findUserByAuthSchId(id, true);
        if (user == null) {
            triggerErrorResponse(Response.Status.NOT_FOUND);
        }
        return user;
    }
}
