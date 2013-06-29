package hu.sch.web.kp.pages.user;

import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.test.WebTest;
import org.junit.Test;

/**
 *
 * @author aldaris
 */
public class ShowUserTest extends WebTest {

    @Test
    public void testMyPage() {
        tester.startPage(ShowUser.class);
        tester.assertRenderedPage(ShowUser.class);
        tester.assertNoErrorMessage();
    }
}
