package hu.sch.ejb;

import hu.sch.test.base.ContainerAwareAbstractTest;
import hu.sch.domain.Group;
import java.util.List;
import hu.sch.services.UserManagerLocal;
import javax.naming.NamingException;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aldaris
 */
public class UserManagerTest extends ContainerAwareAbstractTest {

    private static UserManagerLocal userManager;

    @BeforeClass
    public static void initialize() {
        try {
            userManager = lookupEJB(UserManagerBean.class);
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
    }

    @Test
    public void doSomething() {
        assertTrue(userManager != null);
    }

    @Test
    public void testGroups() {
        List<Group> groups = userManager.getAllGroups();
        System.out.println("Size of list is: " + groups.size());
        assertTrue(!groups.isEmpty());
    }
}
