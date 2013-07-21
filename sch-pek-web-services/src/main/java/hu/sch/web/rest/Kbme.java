package hu.sch.web.rest;

import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.user.User;
import hu.sch.domain.rest.PointInfo;
import hu.sch.services.GroupManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
@Path("/kbme")
@ManagedBean
public class Kbme {

    private static final Logger logger = LoggerFactory.getLogger(Kbme.class);
    @EJB
    GroupManagerLocal groupManager;
    @EJB
    ValuationManagerLocal valuationManager;
    @Context
    private UriInfo context;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/parent")
    public Group getParentGroups(@QueryParam("id") Long id) {
//         doAudit();
//         return userManager.getParentGroups(id);
        throw new UnsupportedOperationException();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/child")
    public List<Group> getChildGroups(@QueryParam("id") Long id) {
        doAudit();
        return groupManager.getSubGroups(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/leader")
    public User getLeader(@QueryParam("id") Long id) {
        doAudit();
        return groupManager.findLeaderForGroup(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/points")
    public List<PointInfo> getPointsForUser(@QueryParam("uid") String uid, @QueryParam("sid") String sid) {
        doAudit();
        return valuationManager.getPointInfoForUid(uid, new Semester(sid));
    }

    private void doAudit() {
        StringBuilder auditMessage = new StringBuilder("AUDIT LOG for GET method. ");
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
