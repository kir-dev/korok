package hu.sch.api.user;

import hu.sch.api.exceptions.AvatarNotFoundException;
import hu.sch.api.exceptions.PekWebException;
import hu.sch.domain.user.User;
import hu.sch.util.config.Configuration;
import java.io.File;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author tomi
 */
@Path(UsersBase.PATH + "/avatar")
public class UsersAvatar extends UsersBase {

    private Configuration config;

    public UsersAvatar() {
    }

    public UsersAvatar(Long id) {
        this.id = id;
    }

    @Inject
    public void setConfig(Configuration config) {
        this.config = config;
    }

    @GET
    public AvatarView getAvatar() {
        User user = fetchUser();
        if (user.getPhotoPath() == null) {
            throw new AvatarNotFoundException();
        }

        return new AvatarView(user, config.getDomain());
    }
}
