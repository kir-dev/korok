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

        return Collections.emptyList();
    }
}
