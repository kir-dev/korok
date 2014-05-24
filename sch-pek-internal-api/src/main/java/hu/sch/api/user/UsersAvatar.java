package hu.sch.api.user;

import hu.sch.api.Base;
import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.config.Configuration;
import hu.sch.util.net.MediaType;
import java.io.File;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author tomi
 */
@Path("/users/{id}/avatar")
public class UsersAvatar extends Base {

    @PathParam("id")
    private Long id;
    private UserManagerLocal userManager;
    private Configuration config;

    public UsersAvatar() {
    }

    public UsersAvatar(Long id) {
        this.id = id;
    }

    @Inject
    public void setUserManager(UserManagerLocal userManager) {
        this.userManager = userManager;
    }

    @Inject
    public void setConfig(Configuration config) {
        this.config = config;
    }

    @GET
    public Response getAvatar() {
        User user = userManager.findUserById(id);
        if (user == null) {
            return respondWithNotFound("User cannot be found.");
        }
        if (!user.hasPhoto()) {
            return respondWithNotFound("User does not have an avatar.");
        }

        String photoPath = user.getPhotoFullPath(config.getImageUploadConfig().getBasePath());
        File image = new File(photoPath);
        if (!image.exists()) {
            return respondWithNotFound("Avatar file cannot be found on the disk.");
        }

        return Response.ok(image).type(MediaType.IMAGE_PNG.getContentType()).build();
    }
}
