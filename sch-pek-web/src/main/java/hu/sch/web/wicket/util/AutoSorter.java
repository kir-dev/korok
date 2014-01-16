package hu.sch.web.wicket.util;

import hu.sch.util.HungarianStringComparator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.model.PropertyModel;

/**
 * Automatikusan rendezhetünk vele listákat, felhasználva a
 * {@link PropertyModel}t.
 *
 * @author messo
 * @since 2.3.1
 */
public class AutoSorter {

    public static <T> void sort(final List<T> list, SortParam<String> sortParam) {
        final String prop = sortParam.getProperty();
        final int r = sortParam.isAscending() ? 1 : -1;

        // ne rendezzünk.
        if (prop == null) {
            return;
        }

        Collections.sort(list, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                Comparable m1 = (Comparable) new PropertyModel<T>(o1, prop).getObject();
                Comparable m2 = (Comparable) new PropertyModel<T>(o2, prop).getObject();

                if (m1 == null && m2 == null) {
                    return 0;
                } else if (m1 == null) {
                    return -r;
                } else if (m2 == null) {
                    return r;
                } else {
                    if (m1 instanceof String && m2 instanceof String) {
                        return r * HungarianStringComparator.scompare((String) m1, (String) m2);
                    } else {
                        return r * m1.compareTo(m2);
                    }
                }
            }
        });
    }
}
