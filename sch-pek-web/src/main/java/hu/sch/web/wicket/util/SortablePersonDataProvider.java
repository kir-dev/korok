package hu.sch.web.wicket.util;

import hu.sch.domain.profile.Person;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

/**
 *
 * @author aldaris
 */
public class SortablePersonDataProvider extends SortableDataProvider<Person, String> {

    private SortableList<Person> persons;

    public SortablePersonDataProvider(List<Person> persons) {
        this.persons = new SortableList<Person>(persons);
        setSort(Person.SORT_BY_NAME, SortOrder.ASCENDING);
    }

    @Override
    public Iterator<? extends Person> iterator(final long first, final long count) {
        persons.sort(getSort());
        return persons.getList().subList((int) first, (int) (first + count)).iterator();
    }

    @Override
    public IModel<Person> model(Person p) {
        return new LoadableDetachablePersonModel(p);
    }

    @Override
    public long size() {
        return persons.size();
    }

    public List<Person> getPersons() {
        return persons.getList();
    }

    public void setPersons(List<Person> list) {
        persons.setList(list);
        persons.sort(getSort());
    }
}
