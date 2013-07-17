package hu.sch.web.wicket.util;

import hu.sch.domain.user.User;
import java.text.Collator;
import java.util.*;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public class SortableUserDataProvider extends SortableDataProvider<User, String> {

    private List<User> users;
    private static final Collator huCollator = Collator.getInstance(new Locale("hu"));

    public SortableUserDataProvider(List<User> user) {
        users = user;
        setSort("name", SortOrder.ASCENDING);
    }

    @Override
    public Iterator<User> iterator(long first, long count) {
        SortParam<String> sp = getSort();
        return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
    }

    @Override
    public long size() {
        return users.size();
    }

    public List<User> getIndex(String prop, boolean asc) {
        if (prop == null) {
            return users;
        }
        if (prop.equals("name")) {
            if (asc) {
                Collections.sort(users, new Comparator<User>() {

                    @Override
                    public int compare(User o1, User o2) {
                        return huCollator.compare(o1.getFullName(), o2.getFullName());
                    }
                });
            } else {
                Collections.sort(users, new Comparator<User>() {

                    @Override
                    public int compare(User o1, User o2) {
                        return huCollator.compare(o2.getFullName(), o1.getFullName());
                    }
                });
            }
        } else if (prop.equals("svieMembershipType")) {
            if (asc) {
                Collections.sort(users, new Comparator<User>() {

                    @Override
                    public int compare(User o1, User o2) {
                        if (o1.getSvieMembershipType().equals(o2.getSvieMembershipType())) {
                            return huCollator.compare(o1.getFullName(), o2.getFullName());
                        } else {
                            return o1.getSvieMembershipType().compareTo(o2.getSvieMembershipType());
                        }
                    }
                });
            } else {
                Collections.sort(users, new Comparator<User>() {

                    @Override
                    public int compare(User o1, User o2) {
                        if (o1.getSvieMembershipType().equals(o2.getSvieMembershipType())) {
                            return huCollator.compare(o2.getFullName(), o1.getFullName());
                        } else {
                            return o2.getSvieMembershipType().compareTo(o1.getSvieMembershipType());
                        }
                    }
                });
            }
        } else {
            throw new RuntimeException("uknown sort option [" + prop
                    + "]. valid options: [name] , [svieMembershipType]");
        }
        return users;
    }

    @Override
    public IModel<User> model(User object) {
        return new Model<User>(object);
    }

    private List<User> find(long first, long count, String property, boolean ascending) {
        List<User> ret = getIndex(property, ascending).subList((int) first, (int) (first + count));
        return ret;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
