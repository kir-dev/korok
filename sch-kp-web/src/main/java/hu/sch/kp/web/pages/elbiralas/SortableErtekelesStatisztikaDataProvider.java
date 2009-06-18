/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.kp.web.pages.elbiralas;

import hu.sch.domain.ErtekelesStatisztika;
import hu.sch.domain.Szemeszter;
import hu.sch.kp.services.ErtekelesManagerLocal;
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
public class SortableErtekelesStatisztikaDataProvider extends SortableDataProvider {
    ErtekelesManagerLocal ertekelesManager;
    
    private List<ErtekelesStatisztika> statList;
    private Szemeszter szemeszter;

    public SortableErtekelesStatisztikaDataProvider(ErtekelesManagerLocal ertekelesManager, Szemeszter szemeszter) {
        this.ertekelesManager = ertekelesManager;
        this.szemeszter = szemeszter;
        setSort(new SortParam("csoportNev", true));
    }
    
    public Iterator<?> iterator(int first, int count) {
        // cache-elt példány
        //statList = null;
        return getStatList().iterator();
    }

    public int size() {
        return getStatList().size();
    }

    public List<ErtekelesStatisztika> getStatList() {
        if (statList == null) {
            statList = ertekelesManager.findErtekelesStatisztikaForSzemeszter(szemeszter, getSort().getProperty());
        }
        return statList;
    }

    public IModel model(Object object) {
        return new CompoundPropertyModel(object);
    }
}
