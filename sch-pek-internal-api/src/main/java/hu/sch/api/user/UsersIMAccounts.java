package hu.sch.api.user;

import hu.sch.domain.user.IMAccount;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path(UsersBase.PATH + "/im")
public class UsersIMAccounts extends UsersBase {

    @GET
    public Set<IMAccount> getIMAcconts() {
        return fetchUser(userManager::findUserByIdWithIMAccounts).getImAccounts();
    }
}
