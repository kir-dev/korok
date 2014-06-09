package hu.sch.api.user;

import hu.sch.api.exceptions.EntityNotFoundWebException;
import hu.sch.domain.user.IMAccount;
import hu.sch.services.exceptions.EntityNotFoundException;
import java.util.Set;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path(UsersBase.PATH + "/im")
public class UsersIMAccounts extends UsersBase {

    @GET
    public Set<IMAccount> getIMAcconts() {
        return fetchUser(userManager::findUserByIdWithIMAccounts).getImAccounts();
    }

    @DELETE
    @Path("{im_id}")
    public IMAccount deleteIMAccount(@PathParam("im_id") Long imId) {
        try {
            return userManager.removeIMAccount(id, imId);
        } catch (EntityNotFoundException ex) {
            throw new EntityNotFoundWebException(ex);
        }
    }
}
