package hu.sch.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.sch.domain.Semester;
import hu.sch.domain.rest.ApprovedEntrant;
import hu.sch.domain.util.PatternHolder;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

/**
 *
 * @author balo
 */
@Path("/entrants")
@ManagedBean(value = "EntrantsRestBean")
public class Entrants extends PekWebservice {

    private static final Logger LOGGER = Logger.getLogger(Entrants.class);
    @EJB
    private ValuationManagerLocal valuationManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{semester}/{neptun}")
    public String getEntrants(
            @PathParam("neptun") final String neptun,
            @PathParam("semester") final String semesterId) {

        doAudit();

        if (!PatternHolder.SEMESTER_PATTERN.matcher(semesterId).matches()) {
            LOGGER.error("Webservice called with invalid semesterid=" + semesterId);
            triggerErrorResponse(Response.Status.BAD_REQUEST);
        }

        if (!PatternHolder.NEPTUN_PATTERN.matcher(neptun).matches()) {
            LOGGER.error("Webservice called with invalid neptun=" + neptun);
            triggerErrorResponse(Response.Status.BAD_REQUEST);
        }

        final List<ApprovedEntrant> entrants = new LinkedList<ApprovedEntrant>();
        try {
            entrants.addAll(valuationManager.getApprovedEntrants(neptun, new Semester(semesterId)));
        } catch (PersonNotFoundException ex) {
            LOGGER.info("Person not found with neptun code=" + neptun);
            triggerErrorResponse(Response.Status.NOT_FOUND);
        }

        String resultInJson = "";
        try {
            resultInJson = mapper.writeValueAsString(entrants);
        } catch (JsonProcessingException ex) {
            final String errorMsg = "Couldn't process list to json; given values: neptun="
                    + neptun + ";semester=" + semesterId;

            LOGGER.error(errorMsg, ex);
            triggerErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return resultInJson;
    }
}
