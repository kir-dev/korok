package hu.sch.web.rest;

import hu.sch.domain.user.User;
import hu.sch.services.MembershipManagerLocal;
import hu.sch.web.rest.dto.EntitlementProducer;
import hu.sch.web.rest.dto.ProfileResult;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author balo
 */
@Path("/profile")
public class Profile extends PekWebservice {

    @Inject
    private MembershipManagerLocal membershipManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequestScoped
    @Path("/neptun/{neptun}")
    public ProfileResult getProfileByNeptun(
            @PathParam("neptun") final String neptun,
            @Context UriInfo context) {

        doAudit();
        checkNeptun(neptun);

        final User user = findUserByNeptun(neptun);
        final ProfileResult result = new ProfileResult(user, createEntitlement(user));

        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequestScoped
    @Path("/uid/{uid}")
    public ProfileResult getProfileByUid(
            @PathParam("uid") final String uid,
            @Context UriInfo context) {

        doAudit();
        checkUid(uid.toLowerCase()); //toLowerCase needs because the UID_PATTERN is case sensitive

        final User user = findUserByUid(uid);
        final ProfileResult result = new ProfileResult(user, createEntitlement(user));

        return result;
    }

    private String createEntitlement(User user) {
        return new EntitlementProducer(user, membershipManager).createEntitlement();
    }

}
