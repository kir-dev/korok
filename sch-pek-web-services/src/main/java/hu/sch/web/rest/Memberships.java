package hu.sch.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.sch.domain.Membership;
import hu.sch.domain.user.User;
import hu.sch.services.GroupManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.util.PatternHolder;
import hu.sch.web.rest.dto.MembershipResult;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
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
@Path("/memberships")
@ManagedBean(value = "MembershipsRestBean")
public class Memberships extends PekWebservice {

    private static final Logger LOGGER = LoggerFactory.getLogger(Memberships.class);
    @EJB
    private UserManagerLocal userManager;
    @EJB
    private GroupManagerLocal groupManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequestScoped
    @Path("/neptun/{neptun}")
    public String getMembershipsByNeptun(@PathParam("neptun") final String neptun) {

        doAudit();

        if (!PatternHolder.NEPTUN_PATTERN.matcher(neptun).matches()) {
            LOGGER.error("Webservice called with invalid neptun=" + neptun);
            triggerErrorResponse(Response.Status.BAD_REQUEST);
        }

        String resultJson = "";

        try {
            final User user = userManager.findUserByNeptun(neptun, true);

            if (user == null) {
                LOGGER.info("Memberships not found with neptun code=" + neptun);
                triggerErrorResponse(Response.Status.NOT_FOUND);
            }

            final Set<MembershipResult> result = new HashSet<>();
            for (Membership ms : user.getMemberships()) {
                //we need only active memberships
                if (ms.getEnd() == null) {
                    final User groupLeader = groupManager.findLeaderForGroup(ms.getGroupId());

                    result.add(new MembershipResult(ms.getGroupId(),
                            ms.getGroup().getName(),
                            user.getId().equals(groupLeader.getId())));
                }
            }

            resultJson = mapper.writeValueAsString(result);

        } catch (JsonProcessingException ex) {
            LOGGER.error("Couldn't process list to json; given values: neptun=" + neptun, ex);
            triggerErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return resultJson;
    }
}
