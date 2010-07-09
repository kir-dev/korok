/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.web.wicket.util;

import java.util.List;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;

/**
 * Rendezhető lista (igazából egy wrapper, ami rendezgeti a listát)
 *
 * @author  messo
 * @since   2.3.1
 * @see AutoSorter
 */
public class OrderableList<T> {

    protected final List<T> list;
    protected final Class<T> clazz;
    protected SortParam lastSortParam = null;

    public OrderableList(List<T> list, Class<T> clazz) {
        this.list = list;
        this.clazz = clazz;
    }

    public List<T> getList() {
        return list;
    }

    /**
     * Rendezi a listát, ha kell
     */
    public void sort(SortParam sp) {
        if( shouldOrder(sp) ) {
            AutoSorter.sort(list, clazz, sp);
            lastSortParam = sp;
        }
    }

    public int size() {
        return list.size();
    }

    private boolean shouldOrder(SortParam sp) {
        if( lastSortParam == null ) {
            // még nem volt rendezve
            return true;
        }

        return !sp.getProperty().equals(lastSortParam.getProperty()) ||
                sp.isAscending() != lastSortParam.isAscending();
    }
}
