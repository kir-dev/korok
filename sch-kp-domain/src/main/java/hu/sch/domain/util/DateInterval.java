/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain.util;

import java.util.Date;

/**
 * Egyszerű intervallumot reprezentál (naptól napig)
 *
 * @author  messo
 * @since   2.3.1
 */
public class DateInterval implements Comparable<DateInterval> {

    protected Date start;
    protected Date end;

    public DateInterval(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public int compareTo(DateInterval o) {
        // kezdet szerint rendezünk
        int ret = start.compareTo(o.start);
        if (ret != 0) {
            return ret;
        }

        // ha a kezdet ugyanaz, akkor nézzük a végét.
        if (end != null && o.end != null) {
            return end.compareTo(o.end);
        }
        
        return 0;
    }
}
