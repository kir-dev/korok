package hu.sch.api.user;

import hu.sch.api.exceptions.EntityNotFoundException;
import hu.sch.domain.user.User;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author tomi
 */
@Path(UsersBase.PATH +  "/memberships")
public class UsersMembersips extends UsersBase {

    @GET
    public List<MembershipView> getMemberships() {
        User user = fetchUser();
        return MembershipView.fromCollection(user.getMemberships());
    }

    @Override
    protected User fetchUser() {
        User user = userManager.findUserByIdWithMemberships(id);
        if (user == null) {
            throw new EntityNotFoundException(User.class);
        }
        return user;
    }


}
