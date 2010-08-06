/**
 * Copyright (c) 2009-2010, Peter Major
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

package hu.sch.web.error;

import hu.sch.services.MailManagerLocal;
import hu.sch.web.kp.templates.KorokPageTemplate;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.protocol.http.WebRequest;

/**
 *
 * @author aldaris
 */
public final class InternalServerError extends KorokPageTemplate {

    private static Logger logger = Logger.getLogger(InternalServerError.class);
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
            mailManager.sendEmail("jee-dev@sch.bme.hu", "Programhiba", sb.toString());
        } catch (Exception e) {
            e.initCause(ex);
            logger.error("Error while sending the error e-mail!", e);
        }
    }
}

