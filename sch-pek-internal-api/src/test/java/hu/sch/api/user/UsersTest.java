package hu.sch.api.user;

import hu.sch.api.exceptions.EntityNotFoundException;
import hu.sch.api.response.PekResponse;
import hu.sch.api.response.PekSuccess;
import hu.sch.domain.user.User;
import hu.sch.services.UserManagerLocal;
import javax.ws.rs.core.Response;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author tomi
 */
public class UsersTest {

    private static final Long USER_ID = 1L;
    private Users usersEndpoint;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        usersEndpoint = new Users(USER_ID);
    }

    @Test
    public void userNotFound() {
        setupUserManagerWith(null);
        thrown.expect(EntityNotFoundException.class);
        usersEndpoint.getUserById();
    }

    @Test
    public void userGetsWrappedInAUserView() {
        setupUserManagerWith(new User());
        UserView user = usersEndpoint.getUserById();
        assertThat(user.getEntity()).isNotNull();
    }

    private void setupUserManagerWith(User user) {
        UserManagerLocal um = mock(UserManagerLocal.class);
        when(um.findUserById(USER_ID)).thenReturn(user);
        usersEndpoint.setUserManager(um);
    }
}
