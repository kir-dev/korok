package hu.sch.api.user;

import hu.sch.api.exceptions.AvatarNotFoundException;
import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import hu.sch.util.ConfigurationStub;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author tomi
 */
public class UsersAvatarTest {

    private static final Long USER_ID = 1L;

    private UsersAvatar avatar;
    private User user;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setupUser() {
        avatar = new UsersAvatar(USER_ID);
        UserManagerLocal um = mock(UserManagerLocal.class);
        when(um.findUserById(USER_ID)).thenReturn(user = new User());
        avatar.setUserManager(um);
    }

    @Test
    public void userDoesNotHaveAnAvatar() {
        thrown.expect(AvatarNotFoundException.class);
        avatar.getAvatar();
    }

    @Test
    public void urlOfTheAvatarIsReturned() {
        ConfigurationStub cfg = new ConfigurationStub();
        cfg.setDomain("example.com");
        avatar.setConfig(cfg);
        user.setPhotoPath("photo.png");

        AvatarView resp = avatar.getAvatar();
        assertThat(resp.getPath()).isEqualTo("//example.com/photo.png");
    }
}
