package hu.sch.api.user;

import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import hu.sch.util.ConfigurationStub;
import hu.sch.util.config.ImageUploadConfig;
import java.io.File;
import java.io.IOException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author tomi
 */
public class UsersAvatarTest {

    private static final Long USER_ID = 1L;

    private UsersAvatar avatar;
    private User user;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void setupUser() {
        avatar = new UsersAvatar(USER_ID);
        UserManagerLocal um = mock(UserManagerLocal.class);
        when(um.findUserById(USER_ID)).thenReturn(user = new User());
        avatar.setUserManager(um);
    }

    @Test
    public void userDoesNotHaveAnAvatar() {
        assertThat(avatar.getAvatar().getStatus()).isEqualTo(404);
    }

    @Test
    public void setsContentTypeToImagePng() throws IOException {
        prepareImageFile();

        Response resp = avatar.getAvatar();
        assertThat(resp.getMediaType()).isEqualTo(MediaType.valueOf("image/png"));
    }

    private void prepareImageFile() throws IOException {
        File file = tmpFolder.newFile("image.png");
        file.createNewFile();
        user.setPhotoPath("image.png");

        ImageUploadConfig iuc = new ImageUploadConfig(tmpFolder.getRoot().getPath(), 0, 0);
        ConfigurationStub config = new ConfigurationStub();
        config.setImageUploadConfig(iuc);
        avatar.setConfig(config);
    }
}
