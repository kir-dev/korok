package hu.sch.web.rest;

import hu.sch.domain.ApprovedEntrant;
import hu.sch.domain.Semester;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import java.util.Collections;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

/**
 *
 * @author balo
 */
@Path("/entrants")
@ManagedBean
public class Entrants {

    private static final Logger LOGGER = Logger.getLogger(Entrants.class);
    @EJB
    private ValuationManagerLocal valuationManager;
    @Context
    private UriInfo context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{semester}/{neptun}")
    public List<ApprovedEntrant> getEntrants(
            @PathParam("neptun") final String neptun,
            @PathParam("semester") final String semesterId) {

        doAudit();

        if (!semesterId.matches("[0-9]{9}")) {
            LOGGER.error("Webservice called with invalid semesterid=" + semesterId);

            triggerErrorResponse(Response.Status.BAD_REQUEST,
                    "Semester must be 9 digit characters, ex.: 200820091; given=" + semesterId);
        }

        if (!neptun.matches("[a-zA-Z0-9]{6}")) {
            LOGGER.error("Webservice called with invalid neptun=" + neptun);

            triggerErrorResponse(Response.Status.BAD_REQUEST,
                    "Neptun must be match [a-zA-Z0-9]{6}, ex.: abc123; given=" + neptun);
        }

        try {
            final List<ApprovedEntrant> userEntrants =
                    valuationManager.getApprovedEntrants(neptun, new Semester(semesterId));


        } catch (PersonNotFoundException ex) {
            final String logMsg =
                    new StringBuilder("Person not found with neptun code=").append(neptun).toString();

            LOGGER.info(logMsg, ex);
            triggerErrorResponse(Response.Status.NOT_FOUND, logMsg);
        }

        return Collections.emptyList();
    }

    private void triggerErrorResponse(final Response.Status status, final String msg) {
        final StringBuilder errorMsg = new StringBuilder("{'error': '").append(msg).append("'}");

        final ResponseBuilder resp = Response.status(status).entity(errorMsg.toString());

        throw new WebApplicationException(resp.build());
    }

    private void doAudit() {
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
}