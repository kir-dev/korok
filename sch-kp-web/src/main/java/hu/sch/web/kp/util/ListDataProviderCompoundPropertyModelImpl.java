package hu.sch.web.kp.util;

import java.io.Serializable;
import java.util.List;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class ListDataProviderCompoundPropertyModelImpl<T extends Serializable> extends ListDataProvider<T> {

    public ListDataProviderCompoundPropertyModelImpl(List<T> list) {
        super(list);
    }

    @Override
    public IModel<T> model(T object) {
        return new CompoundPropertyModel<T>(object);
    }
}
