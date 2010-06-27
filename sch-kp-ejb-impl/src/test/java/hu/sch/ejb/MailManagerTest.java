package hu.sch.ejb;

import hu.sch.test.base.AbstractTest;
import hu.sch.services.MailManagerLocal;
import javax.naming.NamingException;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aldaris
 */
public class MailManagerTest extends AbstractTest {

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
