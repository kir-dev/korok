package hu.sch.web.rest;

import hu.sch.domain.Semester;
import hu.sch.domain.rest.ApprovedEntrant;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.UserNotFoundException;
import hu.sch.util.PatternHolder;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author balo
 */
@Path("/entrants/get/{semester}")
@Produces(MediaType.APPLICATION_JSON)
public class Entrants extends PekWebservice {

    private static final Logger log = LoggerFactory.getLogger(Entrants.class);
    @Inject
    private ValuationManagerLocal valuationManager;

    @PathParam("semester")
    private String semesterId;

    @GET
    @Path("{neptun}")
    public List<ApprovedEntrant> getEntrants(@PathParam("neptun") final String neptun) {
        doAudit();
        checkSemester(semesterId);
        checkNeptun(neptun);

        return buildEntrants(neptun);
    }


    @GET
    @Path("authsch/{id}")
    public List<ApprovedEntrant> getEntrantsByAuthSchId(@PathParam("id") String id) {
        doAudit();
        checkSemester(semesterId);
        checkUUID(id);

        return buildEntrants(id);
    }


    // id can either be a neptun code or an auth.sch id
    private List<ApprovedEntrant> buildEntrants(final String id) {
        final List<ApprovedEntrant> entrants = new LinkedList<>();
        try {
            entrants.addAll(valuationManager.getApprovedEntrants(id, new Semester(semesterId)));
        } catch (UserNotFoundException ex) {
            log.info("User not found with neptun code=" + id);
            triggerErrorResponse(Response.Status.NOT_FOUND);
        }

        return entrants;
    }

    public void checkSemester(String sem) {
        if (!PatternHolder.SEMESTER_PATTERN.matcher(semesterId).matches()) {
            log.error("Webservice called with invalid semesterid=" + semesterId);
            triggerErrorResponse(Response.Status.BAD_REQUEST);
        }
    }
}
