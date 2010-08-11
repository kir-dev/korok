/**
 * Copyright (c) 2008-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
