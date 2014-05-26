package hu.sch.api.user;

import hu.sch.api.response.PekResponse;
import hu.sch.api.response.PekSuccess;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author tomi
 */
@Path(UsersBase.PATH)
public class Users extends UsersBase {

    public Users() {
    }

    public Users(Long id) {
        this.id = id;
    }

    @GET
    public UserView getUserById() {
        return new UserView(fetchUser());
    }
}
