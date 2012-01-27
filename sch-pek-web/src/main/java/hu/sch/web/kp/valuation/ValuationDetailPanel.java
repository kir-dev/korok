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
package hu.sch.web.kp.valuation;

import hu.sch.domain.Valuation;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.wicket.components.tables.ValuationTable;
import hu.sch.web.wicket.components.tables.ValuationTableForGroup;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * Egy egyszerű panel, ami megmutatja az értékelés szövegét, pontozási elveket
 * és a pontokat/belépőket.
 *
 * @author aldaris
 * @author messo
 */
public class ValuationDetailPanel extends Panel {

    @EJB(name = "ValuationManagerBean")
    private ValuationManagerLocal valuationManager;
    private final ValuationTable valuationTable;

    public ValuationDetailPanel(String id) {
        super(id);

        setDefaultModel(new CompoundPropertyModel<Valuation>((Valuation) null));

        MultiLineLabel valuationText = new MultiLineLabel("valuationText");
        valuationText.setEscapeModelStrings(false);
        add(valuationText);

        MultiLineLabel principle = new MultiLineLabel("principle");
        principle.setEscapeModelStrings(false);
        add(principle);

        valuationTable = new ValuationTableForGroup("valuationTable", null);
        add(valuationTable.getDataTable());
    }

    public void updateValuation(Valuation ertekeles) {
        if (ertekeles != null) {
            valuationTable.updateList(valuationManager.findRequestsForValuation(ertekeles.getId()));
            setDefaultModelObject(ertekeles);
        }
    }
}
