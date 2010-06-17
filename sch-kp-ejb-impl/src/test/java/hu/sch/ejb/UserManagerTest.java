package hu.sch.ejb;

import hu.sch.services.UserManagerLocal;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aldaris
 */
public class UserManagerTest {

    private static UserManagerLocal userManager;

    @BeforeClass
    public static void initialize() {
        try {
            EJBContainer ejb = EJBContainer.createEJBContainer();
            Context ic = ejb.getContext();
            userManager = (UserManagerLocal) ic.lookup("UserManagerBean");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void doSomething() {
        assertTrue(true);
    }
}
