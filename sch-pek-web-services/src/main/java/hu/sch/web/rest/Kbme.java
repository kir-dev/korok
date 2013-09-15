package hu.sch.web.rest;

import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.user.User;
import hu.sch.domain.rest.PointInfo;
import hu.sch.services.GroupManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import java.util.List;
import javax.inject.Inject;
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
public class Kbme extends PekWebservice {

    private static final Logger log = LoggerFactory.getLogger(Kbme.class);
    @Inject
    GroupManagerLocal groupManager;
    @Inject
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
}
