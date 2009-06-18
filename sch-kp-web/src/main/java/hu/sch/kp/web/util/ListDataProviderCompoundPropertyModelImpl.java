package hu.sch.kp.web.util;

import java.util.List;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class ListDataProviderCompoundPropertyModelImpl extends ListDataProvider {

    public ListDataProviderCompoundPropertyModelImpl(List<?> list) {
        super(list);
    }

    @Override
    public IModel model(Object object) {
        return new CompoundPropertyModel(object);
    }
}
