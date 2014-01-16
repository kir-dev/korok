package hu.sch.web.wicket.util;

import hu.sch.domain.Group;
import hu.sch.util.HungarianStringComparator;
import java.util.*;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

/**
 *
 * @author aldaris
 */
public class SortableGroupDataProvider extends SortableDataProvider<Group, String> {

    private List<Group> groups;

    public SortableGroupDataProvider(List<Group> groups) {
        this.groups = groups;
        setSort("name", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<Group> iterator(long first, long count) {
        SortParam<String> sp = getSort();
        return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
    }

    @Override
    public long size() {
        return groups.size();
    }

    public List<Group> getIndex(String prop, boolean asc) {
        if (prop == null) {
            return groups;
        }
        if (prop.equals("name")) {
            if (asc) {
                Collections.sort(groups, new Comparator<Group>() {

                    @Override
                    public int compare(Group o1, Group o2) {
                        return HungarianStringComparator.scompare(o1.getName(), o2.getName());
                    }
                });
            } else {
                Collections.sort(groups, new Comparator<Group>() {

                    @Override
                    public int compare(Group o1, Group o2) {
                        return HungarianStringComparator.scompare(o1.getName(), o2.getName());
                    }
                });
            }
        } else {
            throw new RuntimeException("uknown sort option [" + prop
                    + "]. valid options: [name] , [svieMembershipType]");
        }
        return groups;
    }

    private List<Group> find(long first, long count, String property, boolean ascending) {
        List<Group> ret = getIndex(property, ascending).subList((int) first, (int) (first + count));
        return ret;
    }

    @Override
    public IModel<Group> model(Group object) {
        return new LoadableDetachableGroupModel(object);
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
