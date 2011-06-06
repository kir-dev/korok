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
package hu.sch.web.wicket.components.customlinks;

import hu.sch.domain.EntrantType;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.wicket.util.ByteArrayResourceStream;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * Egy olyan link, amire ha a user rákapcsol, akkor letöltődik CSV-ben az
 * adott félévhez tartozó belépők listája (ismétlődés nélkül, neptun kóddal,
 * elsődleges körrel, indoklással, email címmel)
 *      * x vagy annál több kb-t kapott emberek listája (x állítható, min 1)
 *      * áb-s lista
 *
 * @author  balo
 * @since   2.4.3
 */
public final class CsvExportForJetiLink extends Link<Void> {

    private static Logger logger = Logger.getLogger(CsvExportForJetiLink.class);
    private final EntrantType entrantType;
    private final Integer minEntrantNum;
    private final String fileName;
    @EJB(name = "SystemManagerBean")
    protected SystemManagerLocal systemManager;
    @EJB(name = "ValuationManagerBean")
    protected ValuationManagerLocal valuationManager;

    /**
     *
     * @param id
     * @param entrantType belépő típusa
     * @param minEntrantNum ennyi vagy ennél több belépőt kapott emberek szerepeljenek
     * az exportban
     */
    public CsvExportForJetiLink(final String id, final String fileName, 
            final EntrantType entrantType, final Integer minEntrantNum) {

        super(id);
        this.entrantType = entrantType;
        this.minEntrantNum = minEntrantNum;
        this.fileName = fileName;
    }

    @Override
    public final void onClick() {
        try {

            String content = valuationManager.findApprovedEntrantsForExport(
                    systemManager.getSzemeszter(), entrantType, minEntrantNum);

            IResourceStream resourceStream = new ByteArrayResourceStream(
                    content.getBytes("UTF-8"), "text/csv");
            getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(resourceStream) {

                @Override
                public String getFileName() {
                    return fileName;
                }
            });
        } catch (Exception ex) {
            getSession().error(getLocalizer().getString("err.export", this));
            logger.error("Error while generating CSV export about "
                    + entrantType.toString() + "s with " + minEntrantNum + " min value", ex);
        }
    }
}
