package hu.sch.api.user;

import hu.sch.api.response.PekSuccess;
import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import javax.ws.rs.core.Response;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author tomi
 */
public class UsersTest {

    private static final Long USER_ID = 1L;
    private Users usersEndpoint;

    @Before
    public void setup() {
        usersEndpoint = new Users(USER_ID);
    }

    @Test
    public void userNotFound() {
        setupUserManagerWith(null);
        Response resp = usersEndpoint.getUserById();
        assertThat(resp.getStatus()).isEqualTo(404);
    }

    @Test
    public void userGetsWrappedInAPekSuccess() {
        setupUserManagerWith(new User());
        Response resp = usersEndpoint.getUserById();

        assertThat(resp.getStatus()).isEqualTo(200);
        assertThat(resp.getEntity()).isInstanceOf(PekSuccess.class);
    }

    private void setupUserManagerWith(User user) {
        UserManagerLocal um = mock(UserManagerLocal.class);
        when(um.findUserById(USER_ID)).thenReturn(user);
        usersEndpoint.setUserManager(um);
    }
}
