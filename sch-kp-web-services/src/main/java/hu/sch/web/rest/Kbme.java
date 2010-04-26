package hu.sch.web.rest;

import hu.sch.domain.Group;
import hu.sch.services.UserManagerLocal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

/**
 *
 * @author aldaris
 */
@Path("/kbme")
@Stateless
public class Kbme {

    private static Logger logger = Logger.getLogger(Kbme.class);
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    @Context
    private UriInfo context;
    @Context
    SecurityContext security;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/parent")
    public List<Group> getParentGroups(@QueryParam("id") Long id) {
        doAudit();
        return userManager.getParentGroups(id);
    }

    private final void doAudit() {
        StringBuilder auditMessage = new StringBuilder("AUDIT LOG for GET method. ");
        auditMessage.append("USER: ");
        if (security != null && security.getUserPrincipal() != null) {
            auditMessage.append(security.getUserPrincipal().toString());
        } else {
            logger.info("SecurityContext or UserPrincipal was null.");
            auditMessage.append("UNKNOWN.");
        }
        auditMessage.append(" URL: ");
        if (context != null && context.getRequestUri() != null) {
            auditMessage.append(context.getRequestUri().toString());
        } else {
            logger.info("URIContext or RequestUri was null.");
            auditMessage.append("UNKNOWN");
        }
        logger.info(auditMessage.toString());
        System.out.println(auditMessage.toString());
    }
}
