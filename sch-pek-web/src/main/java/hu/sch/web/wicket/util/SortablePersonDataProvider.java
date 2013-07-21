package hu.sch.web.wicket.util;

import hu.sch.domain.user.User;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

/**
 *
 * @author aldaris
 */
public class SortablePersonDataProvider extends SortableDataProvider<User, String> {

    private SortableList<User> persons;

    public SortablePersonDataProvider(List<User> persons) {
        this.persons = new SortableList<User>(persons);
        setSort("fullName", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends User> iterator(final long first, final long count) {
        persons.sort(getSort());
        return persons.getList().subList((int) first, (int) (first + count)).iterator();
    }

    @Override
    public IModel<User> model(User p) {
        return new LoadableDetachableUserModel(p);
    }

    @Override
    public long size() {
        return persons.size();
    }

    public List<User> getPersons() {
        return persons.getList();
    }

    public void setPersons(List<User> list) {
        persons.setList(list);
        persons.sort(getSort());
    }
}
