/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.web.kp.pages.consider;

import hu.sch.domain.ValuationStatistic;
import hu.sch.domain.Semester;
import hu.sch.services.ValuationManagerLocal;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 *
 * @author hege
 */
public class SortableValuationStatisticDataProvider extends SortableDataProvider<ValuationStatistic> {
    ValuationManagerLocal ertekelesManager;
    
    private List<ValuationStatistic> statList;
    private Semester szemeszter;

    public SortableValuationStatisticDataProvider(ValuationManagerLocal ertekelesManager, Semester szemeszter) {
        this.ertekelesManager = ertekelesManager;
        this.szemeszter = szemeszter;
        setSort(new SortParam("csoportNev", true));
    }
    
    public Iterator<ValuationStatistic> iterator(int first, int count) {
        // cache-elt példány
        //statList = null;
        return getStatList().iterator();
    }

    public int size() {
        return getStatList().size();
    }

    public List<ValuationStatistic> getStatList() {
        if (statList == null) {
            statList = ertekelesManager.findErtekelesStatisztikaForSzemeszter(szemeszter, getSort().getProperty());
        }
        return statList;
    }

    public IModel<ValuationStatistic> model(ValuationStatistic object) {
        return new CompoundPropertyModel<ValuationStatistic>(object);
    }
}
