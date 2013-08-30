package hu.sch.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import hu.sch.domain.Membership;
import hu.sch.domain.User;
import hu.sch.domain.profile.Person;
import hu.sch.domain.util.PatternHolder;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.rest.dto.MembershipResult;
import java.util.HashSet;
import java.util.List;
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
import org.apache.log4j.Logger;

/**
 *
 * @author balo
 */
@Path("/memberships")
@ManagedBean(value = "MembershipsRestBean")
public class Memberships extends PekWebservice {

    private static final Logger LOGGER = Logger.getLogger(Memberships.class);
    @EJB
    private UserManagerLocal userManager;
    @EJB
    private LdapManagerLocal ldapManager;

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
            final Person person = ldapManager.getPersonByNeptun(neptun);
            final Long virId = person.getVirId();
            if (virId == null || virId <= 0) { //she doesn't have a community profile
                throw new PersonNotFoundException();
            }

            final User virUser = userManager.findUserWithMembershipsById(virId);
            final List<Membership> memberships = virUser.getMemberships();

            final Set<MembershipResult> result = new HashSet<MembershipResult>();
            for (Membership ms : memberships) {
                //we need only active memberships
                if (ms.getEnd() == null) {
                    final User groupLeader = userManager.getGroupLeaderForGroup(ms.getGroupId());

                    result.add(new MembershipResult(ms.getGroupId(),
                            ms.getGroup().getName(),
                            virId.equals(groupLeader.getId())));
                }
            }

            resultJson = mapper.writeValueAsString(result);

        } catch (PersonNotFoundException ex) {
            LOGGER.info("Memberships not found with neptun code=" + neptun, ex);
            triggerErrorResponse(Response.Status.NOT_FOUND);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Couldn't process list to json; given values: neptun=" + neptun, ex);
            triggerErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return resultJson;
    }
}
