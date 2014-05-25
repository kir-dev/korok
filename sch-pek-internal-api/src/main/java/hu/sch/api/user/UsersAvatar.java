package hu.sch.api.user;

import hu.sch.domain.user.User;
import hu.sch.services.config.Configuration;
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
    public Response getAvatar() {
        User user = fetchUser();
        if (!user.hasPhoto()) {
            return respondWithNotFound("User does not have an avatar.");
        }

        String photoPath = user.getPhotoFullPath(config.getImageUploadConfig().getBasePath());
        File image = new File(photoPath);
        if (!image.exists()) {
            return respondWithNotFound("Avatar file cannot be found on the disk.");
        }

        return Response.ok(image).type(new MediaType("image", "png")).build();
    }
}
