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

    public ValuationDetailPanel(String id, boolean showSvieColumn) {
        super(id);

        MultiLineLabel valuationText = new MultiLineLabel("valuationText");
        valuationText.setEscapeModelStrings(false);
        add(valuationText);

        MultiLineLabel principle = new MultiLineLabel("principle");
        principle.setEscapeModelStrings(false);
        add(principle);

        valuationTable = new ValuationTableForGroup("valuationTable", null, showSvieColumn);
        add(valuationTable.getDataTable());
    }

    public void updateValuation(Valuation ertekeles) {
        if (ertekeles != null) {
            valuationTable.updateList(valuationManager.findRequestsForValuation(ertekeles.getId()));
            setDefaultModel(new CompoundPropertyModel<Valuation>(ertekeles));
        }
    }
}
