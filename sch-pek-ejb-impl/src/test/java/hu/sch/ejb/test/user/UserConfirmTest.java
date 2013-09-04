package hu.sch.ejb.test.user;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserStatus;
import hu.sch.ejb.UserManagerBean;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.UserBuilder;
import hu.sch.services.exceptions.PekEJBException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author tomi
 */
public class UserConfirmTest extends AbstractDatabaseBackedTest {

    private UserManagerBean bean;

    @Override
    protected void before() {
        bean = new UserManagerBean(getEm());
    }

    @Test
    public void confirmUserWhoHasPassword() throws PekEJBException {
        User user = new UserBuilder().build();

        user.setPasswordDigest("test-digest");
        user.setConfirmationCode("confirmation-code");
        getEm().persist(user);
        getEm().flush();

        bean.confirm(user, null);

        User user2 = getEm().find(User.class, user.getId());

        assertNull(user2.getConfirmationCode());
        assertEquals("test-digest", user2.getPasswordDigest());
        assertEquals(UserStatus.ACTIVE, user.getUserStatus());
    }

    @Test
    public void confirmUserWhoDoesNotHavePassword() throws PekEJBException {
        User user = new UserBuilder().build();
        user.setConfirmationCode("confirmation-code");
        getEm().persist(user);
        getEm().flush();

        bean.confirm(user, "password");

        User user2 = getEm().find(User.class, user.getId());

        assertNull(user2.getConfirmationCode());
        assertEquals(UserStatus.ACTIVE, user.getUserStatus());
        assertNotNull(user2.getPasswordDigest());
        assertNotNull(user2.getSalt());
    }
}
