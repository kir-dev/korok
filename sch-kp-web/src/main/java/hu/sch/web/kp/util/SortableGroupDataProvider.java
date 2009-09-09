/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.util;

import hu.sch.domain.Group;
import java.text.Collator;
import java.util.ArrayList;
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
public class SortableGroupDataProvider extends SortableDataProvider<Group> {

    private List<Group> groups;
    private final List<Group> nameIdx = new ArrayList<Group>();
    private final List<Group> nameDescIdx = new ArrayList<Group>();
    private static final Collator huCollator = Collator.getInstance(new Locale("hu"));

    public SortableGroupDataProvider(List<Group> group) {
        groups = group;
        setSort("name", true);
        nameIdx.addAll(groups);
        nameDescIdx.addAll(groups);
        updateIndexes();
    }

    public Iterator<Group> iterator(int first, int count) {
        SortParam sp = getSort();
        return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
    }

    public int size() {
        return groups.size();
    }

    public List<Group> getIndex(String prop, boolean asc) {
        if (prop == null) {
            return nameIdx;
        }
        if (prop.equals("name")) {
            return (asc) ? nameIdx : nameDescIdx;
        }
        throw new RuntimeException("uknown sort option [" + prop +
                "]. valid options: [firstName] , [lastName]");
    }

    private List<Group> find(int first, int count, String property, boolean ascending) {
        List<Group> ret = getIndex(property, ascending).subList(first, first + count);
        return ret;
    }

    public void updateIndexes() {
        Collections.sort(nameIdx, new Comparator<Group>() {

            public int compare(Group o1, Group o2) {
                return huCollator.compare(o1.getName(), o2.getName());
            }
        });
        Collections.sort(nameDescIdx, new Comparator<Group>() {

            public int compare(Group o1, Group o2) {
                return huCollator.compare(o2.getName(), o1.getName());
            }
        });
    }

    public IModel<Group> model(Group object) {
        return new Model<Group>(object);
    }
}
