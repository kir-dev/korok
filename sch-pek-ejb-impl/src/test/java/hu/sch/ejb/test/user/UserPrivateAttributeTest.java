package hu.sch.ejb.test.user;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttribute;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.ejb.UserManagerBean;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.UserBuilder;
import java.util.List;
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

    @Test
    public void savePrivateAttributesForUser() {
        setupUserWithVisibleEmailAttribute();

        List<UserAttribute> uas = getEm().createQuery("SELECT ua FROM UserAttribute ua", UserAttribute.class).getResultList();
        assertEquals(1, uas.size());
    }

    @Test
    public void emailIsVisibleForUser() {
        Long id = setupUserWithVisibleEmailAttribute();

        User user = getEm().find(User.class, id);

        assertTrue(user.isAttributeVisible(UserAttributeName.EMAIL));
    }

    @Test
    public void invertPrivateAttributeOfUser() {
        Long id = setupUserWithVisibleEmailAttribute();
        User user = getEm().find(User.class, id);

        UserManagerBean bean = new UserManagerBean(getEm());
        bean.invertAttributeVisibility(user, UserAttributeName.EMAIL);

        getEm().flush();
        getEm().clear();

        user = getEm().find(User.class, id);
        assertFalse(user.isAttributeVisible(UserAttributeName.EMAIL));
    }

    private Long setupUserWithVisibleEmailAttribute() {
        final String email = "test@example.com";
        User user = new UserBuilder().withEmail(email).build();
        user.getPrivateAttributes().add(new UserAttribute(UserAttributeName.EMAIL, true));

        getEm().persist(user);
        getEm().flush();
        getEm().clear();

        return user.getId();
    }
}