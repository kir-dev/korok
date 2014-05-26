package hu.sch.api.user;

import hu.sch.api.exceptions.EntityNotFoundException;
import hu.sch.api.Base;
import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import javax.inject.Inject;
import javax.ws.rs.PathParam;

/**
 *
 * @author tomi
 */
public abstract class UsersBase extends Base {

    /**
     * Value: "users/{id}"
     */
    public static final String PATH = "users/{id}";
    @PathParam("id")
    protected Long id;
    protected UserManagerLocal userManager;

    @Inject
    public void setUserManager(UserManagerLocal userManager) {
        this.userManager = userManager;
    }

    protected User fetchUser() {
        User user= userManager.findUserById(id);
        if (user == null) {
            throw new EntityNotFoundException(User.class);
        }

        return user;
    }
}
