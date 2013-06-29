package hu.sch.test.base;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

/**
 *
 * @author aldaris
 */
class ContainerHolder {

    private static final String EMB_GF_INSTALL_ROOT = "org.glassfish.ejb.embedded.glassfish.installation.root";
    private static volatile EJBContainer ejb = null;

    public static synchronized void fireUpEJBContainer() {
        if (ejb == null) {
            Map<String, Object> properties = new HashMap<String, Object>();
            //TODO?: Ez egy kicsit hekknek tűnik, viszont így nem száll el tranziens
            //módon exceptionnel, plusz a GF is sokkal gyorsabban elindul.
            properties.put(EJBContainer.MODULES, new File("../sch-pek-ejb-impl/target/classes"));
            properties.put(EMB_GF_INSTALL_ROOT, TestConfig.getProperty(TestConfig.GF_ROOT));
            ejb = EJBContainer.createEJBContainer(properties);
        }
    }

    public static Context getContext() {
        return ejb.getContext();
    }
}
