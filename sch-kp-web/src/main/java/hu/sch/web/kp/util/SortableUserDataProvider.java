/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.util;

import hu.sch.domain.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public class SortableUserDataProvider extends SortableDataProvider<User> {

    private List<User> users;
    private final List<User> nameIdx = new ArrayList<User>();
    private final List<User> nameDescIdx = new ArrayList<User>();
    private final List<User> msTypeIdx = new ArrayList<User>();
    private final List<User> msTypeDescIdx = new ArrayList<User>();

    public SortableUserDataProvider(List<User> user) {
        users = user;
        setSort("name", true);
        nameIdx.addAll(users);
        nameDescIdx.addAll(users);
        msTypeIdx.addAll(users);
        msTypeDescIdx.addAll(users);
        updateIndexes();
    }

    public Iterator<User> iterator(int first, int count) {
        SortParam sp = getSort();
        return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
    }

    public int size() {
        return users.size();
    }

    public List<User> getIndex(String prop, boolean asc) {
        if (prop == null) {
            return nameIdx;
        }
        if (prop.equals("name")) {
            return (asc) ? nameIdx : nameDescIdx;
        } else if (prop.equals("svieMembershipType")) {
            return (asc) ? msTypeIdx : msTypeDescIdx;
        }
        throw new RuntimeException("uknown sort option [" + prop +
                "]. valid options: [firstName] , [lastName]");
    }

    public IModel<User> model(User object) {
        return new Model<User>(object);
    }

    private List<User> find(int first, int count, String property, boolean ascending) {
        List<User> ret = getIndex(property, ascending).subList(first, first + count);
        return ret;
    }

    public void updateIndexes() {
        Collections.sort(nameIdx, new Comparator<User>() {

            public int compare(User o1, User o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        Collections.sort(nameDescIdx, new Comparator<User>() {

            public int compare(User o1, User o2) {
                return o2.getName().compareTo(o1.getName());
            }
        });
        Collections.sort(msTypeIdx, new Comparator<User>() {

            public int compare(User o1, User o2) {
                if (o1.getSvieMembershipType().equals(o2.getSvieMembershipType())) {
                    return o1.getName().compareTo(o2.getName());
                } else {
                    return o1.getSvieMembershipType().compareTo(o2.getSvieMembershipType());
                }
            }
        });
        Collections.sort(msTypeDescIdx, new Comparator<User>() {

            public int compare(User o1, User o2) {
                if (o1.getSvieMembershipType().equals(o2.getSvieMembershipType())) {
                    return o2.getName().compareToIgnoreCase(o1.getName());
                } else {
                    return o2.getSvieMembershipType().compareTo(o1.getSvieMembershipType());
                }
            }
        });
    }
}
