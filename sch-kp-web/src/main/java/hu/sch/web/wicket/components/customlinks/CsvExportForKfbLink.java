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
package hu.sch.web.wicket.components.customlinks;

import hu.sch.domain.GivenPoint;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.ValuationManagerLocal;
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
 * Egy olyan link, amire ha a user rákapcsol, akkor letöltődik CSV-ben az
 * adott félévhez tartozó pontozás.
 *
 * @author  messo
 * @since   2.3.1
 */
public class CsvExportForKfbLink extends Link<Void> {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;
    @EJB(name = "SystemManagerBean")
    SystemManagerLocal systemManager;

    public CsvExportForKfbLink(String id) {
        super(id);
    }

    @Override
    public void onClick() {
        try {
            IResourceStream resourceStream = new ByteArrayResourceStream(
                    ((ByteArrayOutputStream) generateContent()).toByteArray(),
                    "text/csv");
            getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(resourceStream) {

                @Override
                public String getFileName() {
                    return ("kfbexport.csv");
                }
            });

        } catch (Exception ex) {
            getSession().error("Az export során hiba történt!");
            setResponsePage(getApplication().getHomePage());
        }
    }

    public OutputStream generateContent() throws IOException {
        OutputStream os = new ByteArrayOutputStream();

        List<GivenPoint> points = valuationManager.getPointsForKfbExport(systemManager.getSzemeszter());

        os.write("Neptun kód,Számított pont\n".getBytes("UTF-8"));
        for (GivenPoint p : points) {
            os.write(p.getNeptun().getBytes());
            os.write(',');
            os.write(p.getPoints().toString().getBytes());
            os.write("\n".getBytes());
        }

        return os;
    }
}