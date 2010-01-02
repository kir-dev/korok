/*
 *  Copyright 2009 aldaris.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package hu.sch.web.error;

import hu.sch.services.MailManagerLocal;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.protocol.http.WebRequest;

/**
 *
 * @author aldaris
 */
public final class InternalServerError extends SecuredPageTemplate {

    @EJB(name = "MailManagerBean")
    MailManagerLocal mailManager;

    public InternalServerError(Page page, Exception ex) {
        setHeaderLabelText("Hiba!");
        SmartLinkLabel mailtoLink = new SmartLinkLabel("support", "https://support.sch.bme.hu");
        add(mailtoLink);

        StringBuilder sb = new StringBuilder(500);
        sb.append("Helló!\n\nA Körök alkalmazás futása közben exception keletkezett, pedig nem kellett volna.");
        if (page != null) {
            sb.append("\nA hibáért a következő osztály volt a felelős: ").append(page.getClass().getName());
            sb.append(", ami a következő URL-en volt elérhető: ").append(page.getPageRelativePath());
        }
        HttpServletRequest request = ((WebRequest) getRequest()).getHttpServletRequest();

        sb.append("\nA hibát előidézte: " + request.getRemoteUser());
        sb.append("\n\t").append(request.getRemoteAddr());
        sb.append("\n\t").append(request.getAttribute("mail"));
        sb.append("\n\t").append(request.getAttribute("virid"));

        sb.append("\nA hibához tartozó stacktrace:\n\n");
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bs);
        ex.printStackTrace(ps);

        sb.append(bs.toString());
        sb.append("\n\nJó debugolást! :)\nKörök");
        try {
            mailManager.sendEmail("majorpetya@sch.bme.hu", "Programhiba", sb.toString());
        } catch (Exception e) {
        }
    }
}

