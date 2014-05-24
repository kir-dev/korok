package hu.sch.api.user;

import hu.sch.api.Base;
import hu.sch.api.response.PekError;
import hu.sch.api.response.PekResponse;
import hu.sch.api.response.PekSuccess;
import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import hu.sch.util.exceptions.PekErrorCode;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author tomi
 */
@Path("users/{id}")
public class Users extends Base {

    @PathParam("id")
    private Long id;
    private UserManagerLocal userManager;

    public Users() {
    }

    public Users(Long id) {
        this.id = id;
    }

    @Inject
    public void setUserManager(UserManagerLocal userManager) {
        this.userManager = userManager;
    }

    @GET
    public Response getUserById() {
        User user = userManager.findUserById(id);
        return respondWithEntityOrNotFound(new UserView(user));
    }
}
