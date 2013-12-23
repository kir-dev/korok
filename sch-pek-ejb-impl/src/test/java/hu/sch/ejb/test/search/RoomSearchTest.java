package hu.sch.ejb.test.search;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttribute;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.ejb.search.SearchManagerBean;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.UserBuilder;
import hu.sch.services.SearchManagerLocal;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tomi
 */
public class RoomSearchTest extends AbstractDatabaseBackedTest {

    private SearchManagerLocal service;

    @Override
    protected void before() {
        service = new SearchManagerBean(getEm());
        createUser();
    }


    @Test
    public void searchingForRoom() {
        List<User> users = service.searchUsers("1612");
        assertFalse(users.isEmpty());
    }

    @Test
    public void searchingForRoomWithCapitalLetterInIt() {
        createUser("Koli", "B124");
        assertEquals(1, service.searchUsers("B124").size());
    }

    @Test
    public void searchingForDormitory() {
        assertFalse(service.searchUsers("Schönherz").isEmpty());
    }

    @Test
    public void searchingForRoomAndDormitory() {
        assertEquals(1, service.searchUsers("Schönherz 1612").size());
    }

    @Test
    public void searchingWithMultipleTermsIsRestrictive() {
        createUser("Koli", "B124");
        assertTrue(service.searchUsers("Koli 1612").isEmpty());
    }

    private void createUser() {
        createUser("Schönherz", "1612");
    }

    private void createUser(String dorm, String room) {
        User user = new UserBuilder().build();
        user.getPrivateAttributes().add(new UserAttribute(UserAttributeName.ROOM_NUMBER, true));
        user.setRoom(room);
        user.setDormitory(dorm);
        getEm().persist(user);
        getEm().flush();
    }


}
