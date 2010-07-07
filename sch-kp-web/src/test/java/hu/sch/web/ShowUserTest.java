package hu.sch.web;

import hu.sch.test.base.AbstractTest;
import hu.sch.web.kp.pages.user.ShowUser;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author aldaris
 */
public class ShowUserTest extends AbstractTest {

    private static WicketTester tester;

    @BeforeClass
    public static void setUp() {
        tester = new WicketTester(new PhoenixApplication());
    }

    @Test
    public void testMyPage() {
        tester.startPage(ShowUser.class);
        tester.assertRenderedPage(ShowUser.class);
    }
}
