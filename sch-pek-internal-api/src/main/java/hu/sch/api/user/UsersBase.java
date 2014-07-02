package hu.sch.api.user;

import hu.sch.api.exceptions.EntityNotFoundWebException;
import hu.sch.api.Base;
import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import java.util.function.Function;
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

    /**
     * Fetch user by id.
     *
     * @return user entity
     * @throws EntityNotFoundWebException
     */
    protected User fetchUser() {
        return fetchUser(userManager::findUserById);
    }

    /**
     * Fetch user with the provided fetcher method
     *
     * @param fetcherFunc
     * @return user entity
     * @throws EntityNotFoundWebException
     */
    protected User fetchUser(Function<Long, User> fetcherFunc) {
        User user = fetcherFunc.apply(id);
        if (user == null) {
            throw new EntityNotFoundWebException(User.class);
        }

        return user;
    }
}
