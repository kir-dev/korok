package hu.sch.test.base;

import javax.naming.NamingException;
import org.junit.BeforeClass;

/**
 *
 * @author aldaris
 */
public abstract class ContainerAwareAbstractTest extends AbstractTest {

    private static final String MODULE_NAME = "korok-ejb";

    @BeforeClass
    public static void setup() {
        ContainerHolder.fireUpEJBContainer();
    }

    public static <T> T lookupEJB(Class<T> clazz) throws NamingException {
        return (T) ContainerHolder.getContext().lookup("java:global/" + MODULE_NAME + "/" + clazz.getSimpleName());
    }
}
