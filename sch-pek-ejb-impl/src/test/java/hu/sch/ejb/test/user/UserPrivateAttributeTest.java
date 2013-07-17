package hu.sch.ejb.test.user;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttribute;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.UserBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tomi
 */
public class UserPrivateAttributeTest extends AbstractDatabaseBackedTest {

    @Test
    public void savePrivateAttributes() {
        User user = new UserBuilder().build();

        user.getPrivateAttributes().add(new UserAttribute(UserAttributeName.SCREEN_NAME, true));
        getEm().persist(user);

        User user2 = getEm().find(User.class, user.getId());
        assertFalse(user2.getPrivateAttributes().isEmpty());
    }

}