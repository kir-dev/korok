package hu.sch.web.kp.admin;

import hu.sch.ejb.SystemManagerBean;
import hu.sch.services.SystemManagerLocal;
import hu.sch.web.test.WebTest;
import javax.naming.NamingException;
import static junit.framework.Assert.*;
import org.apache.wicket.Session;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

/**
 *
 * @author aldaris
 */
public class EditSettingsTest extends WebTest {

    @Test
    public void newbieTest() throws NamingException {
        SystemManagerLocal systemManager = lookupEJB(SystemManagerBean.class);

        tester.startPage(EditSettings.class);
        tester.assertRenderedPage(EditSettings.class);

        tester.assertVisible("kirdevfragment");

        Session sess1 = tester.getSession();
        String style1 = sess1.getStyle();

        FormTester formTester = tester.newFormTester("kirdevfragment:kirdevForm");
        formTester.setValue("newbieTime", !systemManager.getNewbieTime());
        formTester.submit();

        sess1.invalidateNow();
        assertTrue(sess1.isSessionInvalidated());

        //Kell, hogy tényleg legyen új sessionünk
        //tester.createRequestCycle();
        assertEquals("TODO: createRequestCycle()-t lecserélni", true, false);
        Session sess2 = tester.getSession();
        String style2 = sess2.getStyle();

        assertNotSame(style1, style2);
    }
}
