package hu.sch.web.kp.consider;

import hu.sch.domain.ValuationStatistic;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 *
 * @author hege
 */
public class ValuationStatisticDataProvider implements IDataProvider<ValuationStatistic> {

    private List<ValuationStatistic> statList;

    public ValuationStatisticDataProvider(List<ValuationStatistic> list) {
        statList = list;
    }

    @Override
    public Iterator<ValuationStatistic> iterator(final long first, final long count) {
        return statList.subList((int) first, (int) (first + count)).iterator();
    }

    @Override
    public long size() {
        return statList.size();
    }

    @Override
    public IModel<ValuationStatistic> model(ValuationStatistic object) {
        return new CompoundPropertyModel<ValuationStatistic>(object);
    }

    @Override
    public void detach() {
    }
}
