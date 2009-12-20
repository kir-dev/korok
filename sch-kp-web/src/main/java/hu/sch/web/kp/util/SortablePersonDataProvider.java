/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.util;

import hu.sch.domain.profile.Person;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public class SortablePersonDataProvider extends SortableDataProvider<Person> {

    private List<Person> persons;
    private Collator huCollator = Collator.getInstance(new Locale("hu"));

    public SortablePersonDataProvider(List<Person> persons) {
        this.persons = persons;
        setSort("name", true);
    }

    @Override
    public Iterator<? extends Person> iterator(int first, int count) {
        SortParam sp = getSort();
        return find(first, count, sp.getProperty(), sp.isAscending()).iterator();

    }

    @Override
    public IModel<Person> model(Person t) {
        return new Model<Person>(t);
    }

    @Override
    public int size() {
        return persons.size();
    }

    private List<Person> find(int first, int count, String property, boolean ascending) {
        List<Person> ret = getIndex(property, ascending).subList(first, first + count);
        return ret;
    }

    private List<Person> getIndex(String property, boolean ascending) {
        if (property == null) {
            return persons;
        }
        if (property.equals("name")) {
            if (ascending) {
                Collections.sort(persons, new Comparator<Person>() {

                    public int compare(Person o1, Person o2) {
                        return huCollator.compare(o1.getFullName(), o2.getFullName());
                    }
                });
            } else {
                Collections.sort(persons, new Comparator<Person>() {

                    public int compare(Person o1, Person o2) {
                        return huCollator.compare(o2.getFullName(), o1.getFullName());
                    }
                });
            }
        } else if (property.equals("uid")) {
            if (ascending) {
                Collections.sort(persons, new Comparator<Person>() {

                    public int compare(Person o1, Person o2) {
                        return huCollator.compare(o1.getUid(), o2.getUid());
                    }
                });
            } else {
                Collections.sort(persons, new Comparator<Person>() {

                    public int compare(Person o1, Person o2) {
                        return huCollator.compare(o2.getUid(), o1.getUid());
                    }
                });
            }
        } else if (property.equals("neptun")) {
            if (ascending) {
                Collections.sort(persons, new Comparator<Person>() {

                    public int compare(Person o1, Person o2) {
                        return huCollator.compare((o1.getNeptun() == null) ? "" : o1.getNeptun(),
                                (o2.getNeptun() == null) ? "" : o2.getNeptun());
                    }
                });
            } else {
                Collections.sort(persons, new Comparator<Person>() {

                    public int compare(Person o1, Person o2) {
                        return huCollator.compare((o2.getNeptun() == null) ? "" : o2.getNeptun(),
                                (o1.getNeptun() == null) ? "" : o1.getNeptun());
                    }
                });
            }
        } else {
            throw new RuntimeException("uknown sort option [" + property
                    + "]. valid options: [name] , [uid] , [neptun]");
        }
        return persons;
    }
}
