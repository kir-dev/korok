package hu.sch.web.wicket.util;

import java.io.Serializable;
import java.util.List;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;

/**
 * Rendezhető lista (igazából egy wrapper, ami rendezgeti a listát)
 *
 * @author  messo
 * @since   2.3.1
 * @see AutoSorter
 */
public class SortableList<T> implements Serializable {

    protected List<T> list = null;
    protected SortParam lastSortParam = null;

    public SortableList() {
    }

    public SortableList(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
        lastSortParam = null;
    }

    /**
     * Rendezi a listát, ha kell
     */
    public void sort(SortParam sp) {
        if (shouldOrder(sp)) {
            AutoSorter.sort(list, sp);
            lastSortParam = sp;
        }
    }

    public int size() {
        return list.size();
    }

    private boolean shouldOrder(SortParam sp) {
        if (lastSortParam == null) {
            // még nem volt rendezve
            return true;
        }

        return !sp.getProperty().equals(lastSortParam.getProperty())
                || sp.isAscending() != lastSortParam.isAscending();
    }
}
