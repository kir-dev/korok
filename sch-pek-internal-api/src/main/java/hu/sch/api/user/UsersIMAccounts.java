package hu.sch.api.user;

import hu.sch.api.exceptions.EntityNotFoundWebException;
import hu.sch.domain.user.IMAccount;
import hu.sch.services.IMAccountManager;
import hu.sch.services.exceptions.EntityNotFoundException;
import java.util.Set;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path(UsersBase.PATH + "/im")
public class UsersIMAccounts extends UsersBase {

    private IMAccountManager iMAccountManager;

    @Inject
    public void setiMAccountManager(IMAccountManager iMAccountManager) {
        this.iMAccountManager = iMAccountManager;
    }

    @GET
    public Set<IMAccount> getIMAccounts() {
        return fetchUser(userManager::findUserByIdWithIMAccounts).getImAccounts();
    }

    @POST
    public IMAccount createIMAccount(@Valid IMAccount account) {
        try {
            return iMAccountManager.createAccount(id, account);
        } catch (EntityNotFoundException ex) {
            throw new EntityNotFoundWebException(ex);
        }
    }

    @DELETE
    @Path("{im_id}")
    public IMAccount deleteIMAccount(@PathParam("im_id") Long imId) {
        try {
            return iMAccountManager.removeIMAccount(imId);
        } catch (EntityNotFoundException ex) {
            throw new EntityNotFoundWebException(ex);
        }
    }

    @PUT
    @Path("{im_id}")
    public IMAccount updateIMAccount(@PathParam("im_id") Long imId, @Valid IMAccount imAcc) {
        try {
            return iMAccountManager.updateIMAccount(imId, imAcc);
        } catch (EntityNotFoundException ex) {
            throw new EntityNotFoundWebException(ex);
        }
    }
}
