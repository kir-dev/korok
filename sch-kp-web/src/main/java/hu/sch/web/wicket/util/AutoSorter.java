/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.wicket.util;

import hu.sch.domain.util.SortProperty;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;

/**
 * Automatikusan rendezhetünk vele listákat, ha a megfelelő gettereket (amelyeknek
 * {@link java.lang.Comparable Comparable} interfészt megvalósító objektummal kell
 * visszatérnie) megannotáltuk a {@link SortProperty @SortProperty} annotációval.
 *
 * <p>TODO - adjunk bele egy kis cache-t (Map&lt;Class, Map&lt;String, Method&gt;)</p>
 *
 * @author  messo
 * @since   2.3.1
 */
public class AutoSorter {

    public static <T> void sort(final List<T> list, final Class<T> clazz, SortParam sortParam) {
        final String prop = sortParam.getProperty();
        final int r = sortParam.isAscending() ? 1 : -1;

        // ne rendezzünk.
        if (prop == null) {
            return;
        }

        Method[] methods = clazz.getMethods();
        for (final Method m : methods) {
            final SortProperty sp = m.getAnnotation(SortProperty.class);
            if (sp == null) {
                continue;
            }
            if (sp.value().equals(prop)) {
                // megvan, hogy a rendezést melyik metódusra kéne meghívni
                Collections.sort(list, new Comparator<T>() {

                    @Override
                    public int compare(T o1, T o2) {
                        // ha az elem null, akkor az elől legyen.
                        try {
                            Comparable a = (Comparable) m.invoke(o1);
                            if (a == null) {
                                // ha null, akkor az elől legyen.
                                return -r;
                            }
                            Comparable b = (Comparable) m.invoke(o2);
                            if (b == null) {
                                return r;
                            }
                            return r * a.compareTo(b);
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(AutoSorter.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalArgumentException ex) {
                            Logger.getLogger(AutoSorter.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InvocationTargetException ex) {
                            Logger.getLogger(AutoSorter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return 0;
                    }
                });
                return;
            }
        }
        throw new IllegalArgumentException("A property (" + prop + ") alapján nem lehet automatikusan rendezni!");
    }
}
