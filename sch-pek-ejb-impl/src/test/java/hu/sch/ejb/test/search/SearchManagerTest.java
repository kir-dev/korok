package hu.sch.ejb.test.search;

import hu.sch.domain.user.User;
import hu.sch.ejb.search.SearchManagerBean;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.UserBuilder;
import hu.sch.services.SearchManagerLocal;
import java.util.Date;
import java.util.List;
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
        getEm().persist(u);
        getEm().flush();

        List<User> users = searchManager.searchBirthdayUsers(new Date());

        assertEquals(1, users.size());
    }

    @Test
    public void userWithNullDateOfBirth() {
        User u = new UserBuilder().withDateOfBirth(null).build();
        getEm().persist(u);

        List<User> users = searchManager.searchBirthdayUsers(new Date());
        assertTrue(users.isEmpty());
    }

    @Test
    public void complexSearchForName() {
        new UserBuilder().withFirstName("Teszt").withScreenName("hello").create(getEm());
        List<User> result = searchManager.searchUsers("teszt", 0, 25);

        assertEquals(1, result.size());
    }

    @Test
    public void complexSearchForEmailWhenVisible() {
        final String email = "a@example.com";
        User u = new UserBuilder()
                .withEmail(email)
                .build();
        persist(u);

        List<User> result = searchManager.searchUsers(email, 0, 25);
        assertFalse("User with email could not be found.", result.isEmpty());
    }
}
