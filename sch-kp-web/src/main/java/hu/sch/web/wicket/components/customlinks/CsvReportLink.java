/**
 * Copyright (c) 2009, Peter Major
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
package hu.sch.web.wicket.components.customlinks;

import hu.sch.domain.User;
import hu.sch.services.SvieManagerLocal;
import hu.sch.web.wicket.util.ByteArrayResourceStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;

/**
 *
 * @author aldaris
 */
public final class CsvReportLink extends Panel {

    @EJB(name = "SvieManagerBean")
    SvieManagerLocal svieManager;

    public CsvReportLink(String id) {
        super(id);
        add(new Link<Void>("csvLink") {

            public void onClick() {
                try {
                    IResourceStream resourceStream = new ByteArrayResourceStream(
                            ((ByteArrayOutputStream) generateContent()).toByteArray(),
                            "text/csv");
                    getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(resourceStream) {

                        @Override
                        public String getFileName() {
                            return ("export.csv");
                        }
                    });
                } catch (Exception ex) {
                    getSession().error("Hiba történt a CSV export generálása közben!");
                    ex.printStackTrace();
                }
            }
        });
    }

    public OutputStream generateContent() throws IOException {
        OutputStream os = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder(100);
        List<User> users = svieManager.getDelegatedUsers();

        sb.append("Delegált neve,Képviselt kör\n");
        for (User user : users) {
            sb.append(user.getName()).append(",");
            sb.append(user.getSviePrimaryMembership().getGroup().getName()).append("\n");
        }
        os.write(sb.toString().getBytes("UTF-8"));
        return os;
    }
}