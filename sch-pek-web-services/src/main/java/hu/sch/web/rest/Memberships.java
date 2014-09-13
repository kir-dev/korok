package hu.sch.web.rest;

import hu.sch.domain.Membership;
import hu.sch.domain.user.User;
import hu.sch.services.GroupManagerLocal;
import hu.sch.web.rest.dto.MembershipResult;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author balo
 */
@Path("/memberships")
@Produces(MediaType.APPLICATION_JSON)
public class Memberships extends PekWebservice {

    private static final Logger log = LoggerFactory.getLogger(Memberships.class);
    @Inject
    private GroupManagerLocal groupManager;

    @GET
    @Path("/neptun/{neptun}")
    public Set<MembershipResult> getMembershipsByNeptun(@PathParam("neptun") final String neptun) {
        doAudit();
        checkNeptun(neptun);

        final User user = findUserByNeptun(neptun);
        return buildMembershipResults(user);
    }

    @GET
    @Path("/authsch/{id}")
    public Set<MembershipResult> getMembershipsByAuthSchId(@PathParam("id") final String id) {
        doAudit();
        checkUUID(id);
        final User user = findUserByAuthSchId(id);
        return buildMembershipResults(user);
    }

    private Set<MembershipResult> buildMembershipResults(final User user) {
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
        return result;
    }
}
