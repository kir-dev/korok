package hu.sch.web.wicket.components;

import hu.sch.domain.user.User;
import hu.sch.services.config.Configuration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;
import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
public class ProfileImageResource extends DynamicImageResource {

    private static final Logger logger = LoggerFactory.getLogger(ProfileImageResource.class);
    private final User user;
    @Inject
    private Configuration config;

    public ProfileImageResource(User user) {
        this.user = user;
        setFormat(extractImageFormat());
    }

    @Override
    protected byte[] getImageData(Attributes atrbts) {
        Path imagePath = Paths.get(user.getPhotoFullPath(config.getImageUploadConfig().getBasePath()));
        if (imagePath.toFile().exists()) {
            try {
                return Files.readAllBytes(imagePath);
            } catch (IOException ex) {
                logger.warn("Could not open image file from " + user.getPhotoPath() + " location.", ex);
            }
        }

        return new byte[0];
    }

    private String extractImageFormat() {
        return FilenameUtils.getExtension(user.getPhotoPath());

    }
}
