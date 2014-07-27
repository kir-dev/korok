package hu.sch.web.rest;

import hu.sch.domain.Membership;
import hu.sch.domain.user.User;
import hu.sch.services.MembershipManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.web.rest.dto.MembershipView;
import hu.sch.web.rest.dto.ProfileView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("sync")
@Produces(MediaType.APPLICATION_JSON)
public class AuthSchSync {

    @Inject
    private UserManagerLocal userManager;

    @Inject
    private MembershipManagerLocal membershipManager;

    @Path("{id}")
    @GET
    public ProfileView getByInternalId(@PathParam("id") String id) {
        return new ProfileView(getUser(id));
    }

    @Path("{id}/memberships")
    @GET
    public MembershipView getMemberships(@PathParam("id") String id) {
        List<Membership> msList = membershipManager.findAllMembershipForUser(getUser(id));
        return new MembershipView(msList);
    }

    private User getUser(String id) {
        User user = null;
        switch (SyncIdChecker.check(id)) {
            case NEPTUN:
                user = userManager.findUserByNeptun(id);
                break;
            case AUTH_SCH_ID:
                user = userManager.findUserByAuthSchId(id);
                break;
            default:
                triggerError(400, buildErrorBody("Not valid id. It should be a UUID or a NEPTUN code."));

        }

        if (user == null) {
            triggerError(404, buildErrorBody("Could not find user with %s id.", id));
        }

        return user;
    }

    private void triggerError(int code, Object obj) {
        Response resp = Response
                .status(code)
                .type(MediaType.APPLICATION_JSON)
                .entity(obj)
                .build();

        throw new WebApplicationException(resp);
    }

    private Map<String, Object> buildErrorBody(String messageFormat, Object... objs) {
        return buildErrorBody(String.format(messageFormat, objs));
    }


    private Map<String, Object> buildErrorBody(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", message);

        return body;
    }
}
