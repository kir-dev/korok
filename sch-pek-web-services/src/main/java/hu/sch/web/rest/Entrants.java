package hu.sch.web.rest;

import hu.sch.domain.Semester;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

/**
 *
 * @author balo
 */
@Path("/entrants")
@ManagedBean
public class Entrants {

    private static Logger logger = Logger.getLogger(Kbme.class);
    @EJB
    UserManagerLocal userManager;
    @EJB
    ValuationManagerLocal valuationManager;
    @Context
    private UriInfo context;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/get/{semester}/{neptun}")
    public List<UserEntrant> getEntrants(
            @PathParam("neptun") final String neptun,
            @PathParam("semester") final String semesterId) {

        doAudit();

        return Collections.emptyList();
    }

    private void doAudit() {
        final StringBuilder auditMessage = new StringBuilder("AUDIT LOG for GET method. ");
        auditMessage.append(" URL: ");
        if (context != null && context.getRequestUri() != null) {
            auditMessage.append(context.getRequestUri().toString());
        } else {
            logger.info("URIContext or RequestUri was null.");
            auditMessage.append("UNKNOWN");
        }
        logger.info(auditMessage.toString());
    }
}
