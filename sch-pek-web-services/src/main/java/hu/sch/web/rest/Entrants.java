package hu.sch.web.rest;

import hu.sch.domain.Semester;
import hu.sch.domain.rest.ApprovedEntrant;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.UserNotFoundException;
import hu.sch.util.PatternHolder;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author balo
 */
@Path("/entrants")
public class Entrants extends PekWebservice {

    private static final Logger log = LoggerFactory.getLogger(Entrants.class);
    @Inject
    private ValuationManagerLocal valuationManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequestScoped
    @Path("/get/{semester}/{neptun}")
    public List<ApprovedEntrant> getEntrants(
            @PathParam("neptun") final String neptun,
            @PathParam("semester") final String semesterId,
            @Context UriInfo context) {

        doAudit();

        if (!PatternHolder.SEMESTER_PATTERN.matcher(semesterId).matches()) {
            log.error("Webservice called with invalid semesterid=" + semesterId);
            triggerErrorResponse(Response.Status.BAD_REQUEST);
        }

        checkNeptun(neptun);

        final List<ApprovedEntrant> entrants = new LinkedList<>();
        try {
            entrants.addAll(valuationManager.getApprovedEntrants(neptun, new Semester(semesterId)));
        } catch (UserNotFoundException ex) {
            log.info("User not found with neptun code=" + neptun);
            triggerErrorResponse(Response.Status.NOT_FOUND);
        }

        return entrants;
    }
}
