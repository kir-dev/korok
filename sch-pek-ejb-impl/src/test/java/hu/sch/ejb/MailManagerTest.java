package hu.sch.ejb;

import hu.sch.services.MailManagerLocal;
import hu.sch.test.base.ContainerAwareAbstractTest;
import javax.naming.NamingException;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aldaris
 */
public class MailManagerTest extends ContainerAwareAbstractTest {

    private static MailManagerLocal mailManager;

    @BeforeClass
    public static void initialize() {
        try {
            mailManager = lookupEJB(MailManagerBean.class);
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
    }

    @Test
    public void doSomething() {
        assertTrue(mailManager != null);
    }
}
