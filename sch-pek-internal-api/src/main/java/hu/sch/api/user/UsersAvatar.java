package hu.sch.api.user;

import hu.sch.api.exceptions.AvatarNotFoundException;
import hu.sch.api.exceptions.PekWebException;
import hu.sch.api.exceptions.RequestFormatException;
import hu.sch.api.response.PekError;
import hu.sch.api.response.PekResponse;
import hu.sch.api.response.PekSuccess;
import hu.sch.domain.user.ProfileImage;
import hu.sch.domain.user.User;
import hu.sch.util.config.Configuration;
import hu.sch.services.exceptions.PekErrorCode;
import hu.sch.services.exceptions.PekException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
@Path(UsersBase.PATH + "/avatar")
public class UsersAvatar extends UsersBase {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UsersAvatar.class);

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

    @PUT
    @Consumes("image/*")
    public AvatarView uploadAvatar(@Context HttpServletRequest request, InputStream image) {
        byte[] imageBytes;
        try {
            imageBytes = IOUtils.toByteArray(image);
        } catch (IOException ex) {
            logger.warn("Could not read image from request body.", ex);
            // TODO: create a standard error reporting process github/#110
            throw new PekWebException(new PekError(PekErrorCode.FILE_OPEN_FAILED, ex.getMessage()), 500);
        }

        // empty image
        if (imageBytes.length == 0) {
            throw new RequestFormatException("No image was present.");
        }

        String mimeType = request.getContentType();
        ProfileImage profileImage = new ProfileImage(mimeType, imageBytes, imageBytes.length);

        User user = fetchUser();

        // TODO: authorization?
        user = userManager.updateUser(user, profileImage);

        return new AvatarView(user, config.getDomain());
    }

    @DELETE
    public PekResponse deleteAvatar() {
        // TODO: authorization?
        User user = fetchUser();
        userManager.removeProfileImage(user);
        return new PekSuccess(null);
    }
}
