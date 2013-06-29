package hu.sch.web.test;

import hu.sch.test.base.ContainerAwareAbstractTest;
import hu.sch.web.PhoenixApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.BeforeClass;

/**
 *
 * @author  messo
 * @since   2.3.1
 */
public abstract class WebTest extends ContainerAwareAbstractTest {

    protected static WicketTester tester;

    @BeforeClass
    public static void initWicketTester() {
        tester = new WicketTester(new PhoenixApplication());
    }
}
