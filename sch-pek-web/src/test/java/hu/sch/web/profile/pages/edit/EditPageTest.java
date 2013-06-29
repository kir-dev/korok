package hu.sch.web.profile.pages.edit;

import hu.sch.web.profile.edit.EditPage;
import hu.sch.web.test.WebTest;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

/**
 *
 * @author aldaris
 */
public class EditPageTest extends WebTest {

    @Test
    public void testPageCreation() {
        tester.startPage(EditPage.class);
        tester.assertRenderedPage(EditPage.class);
    }

    @Test
    public void testImageMimeType() {
        FormTester formTester = tester.newFormTester("personForm", false);
        formTester.setFile("fileInput", new File("src/main/webapp/images/btnEdit.gif"), "html");
        formTester.submit("submitButton");
        tester.assertErrorMessages(new String[]{"A fotó formátuma nem megfelelő! Megfelelő formátumok: jpeg, png, gif."});

        formTester = tester.newFormTester("personForm", false);
        formTester.setFile("fileInput", new File("src/main/webapp/images/btnEdit.gif"), "image/gif");
        formTester.submit("submitButton");
        tester.assertNoErrorMessage();
    }
}
