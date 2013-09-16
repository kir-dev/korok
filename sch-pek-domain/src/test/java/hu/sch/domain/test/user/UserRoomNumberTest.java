package hu.sch.domain.test.user;

import hu.sch.domain.user.User;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tomi
 */
public class UserRoomNumberTest {

    private static final String DORMITORY = "Dorm";
    private static final String ROOM = "11";

    private User user;

    @Before
    public void setup() {
        user = new User();
        user.setDormitory(DORMITORY);
        user.setRoom(ROOM);
    }

    @Test
    public void nullRoomNumberAsString() {
        user.setRoom(null);
        user.setDormitory(null);

        assertEquals("", user.getFullRoomNumber());
    }

    @Test
    public void roomNumberAsString()  {
        assertEquals(DORMITORY + " " + ROOM, user.getFullRoomNumber());
    }

    @Test
    public void roomNumberWithEmptyNumberPartAsString() {
        user.setRoom(null);

        assertEquals(DORMITORY, user.getFullRoomNumber());
    }

    @Test
    public void roomNumberWithEmptyDormitoryAsString() {
        user.setDormitory(null);

        assertEquals(ROOM, user.getFullRoomNumber());
    }
}
