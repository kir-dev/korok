package hu.sch.api.user;

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
