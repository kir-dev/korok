package hu.sch.ejb;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import hu.sch.services.MailManagerLocal;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aldaris
 */
public class MailManagerTest {

    private static MailManagerLocal mailManager;

    @BeforeClass
    public static void initialize() {
        try {
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(EJBContainer.MODULES, new File("target/classes"));
            EJBContainer ejb = EJBContainer.createEJBContainer(properties);
            Context ic = ejb.getContext();
            mailManager = (MailManagerLocal) ic.lookup("java:global/classes/MailManagerBean");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void doSomething() {
        assertTrue(mailManager != null);
    }
}
