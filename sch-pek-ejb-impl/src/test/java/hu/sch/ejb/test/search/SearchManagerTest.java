package hu.sch.ejb.test.search;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttribute;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.ejb.search.SearchManagerBean;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.UserBuilder;
import hu.sch.services.SearchManagerLocal;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tomi
 */
public class SearchManagerTest extends AbstractDatabaseBackedTest {

    private SearchManagerLocal searchManager;

    @Override
    protected void before() {
        super.before();
        searchManager = new SearchManagerBean(getEm());
    }

    @Test
    public void gettingBirthdayBoysForToday() {
        User u = new UserBuilder().build();
        u.getPrivateAttributes().add(new UserAttribute(UserAttributeName.DATE_OF_BIRTH, true));
        getEm().persist(u);
        getEm().flush();

        List<User> users = searchManager.searchBirthdayUsers(new Date());

        assertEquals(1, users.size());
    }

    @Test
    public void gettingBirthdayBoysFiveDaysAgo() {
        new UserBuilder().withDateOfBirth(DateUtils.addDays(new Date(), -5)).create(getEm());
        new UserBuilder().withDateOfBirth(DateUtils.addDays(new Date(), -5)).create(getEm());

        List<User> users = searchManager.searchBirthdayUsers(DateUtils.addDays(new Date(), -5));

        // birthday attribute is not visibile for neither of the users
        assertTrue(users.isEmpty());
    }

    @Test
    public void userWithNullDateOfBirth() {
        User u = new UserBuilder().withDateOfBirth(null).build();
        u.getPrivateAttributes().add(new UserAttribute(UserAttributeName.DATE_OF_BIRTH, true));
        getEm().persist(u);

        List<User> users = searchManager.searchBirthdayUsers(new Date());
        assertTrue(users.isEmpty());
    }

    @Test
    public void complexSearchForName() {
        new UserBuilder().withFirstName("Teszt").withScreenName("hello").create(getEm());
        List<User> result = searchManager.searchUsers("teszt");

        assertEquals(1, result.size());
    }

    @Test
    public void complexSearchForEmailWhenVisible() {
        final String email = "a@example.com";
        User u = new UserBuilder()
                .withEmail(email)
                .build();
        u.getPrivateAttributes().add(new UserAttribute(UserAttributeName.EMAIL, true));
        persist(u);

        List<User> result = searchManager.searchUsers(email);
        assertFalse("User with email could not be found.", result.isEmpty());
    }

    @Test
    public void complexSearchForEmailWhenNotVisible() {
        final String email = "a@example.com";
        new UserBuilder().withEmail(email).create(getEm());

        List<User> result = searchManager.searchUsers(email);
        assertTrue(result.isEmpty());
    }
}
