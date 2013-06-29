package hu.sch.domain.util;

import java.io.Serializable;
import java.util.Date;

/**
 * Egyszerű intervallumot reprezentál (naptól napig)
 *
 * @author  messo
 * @since   2.3.1
 */
public class DateInterval implements Comparable<DateInterval>, Serializable {

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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DateInterval) {
            DateInterval other = (DateInterval) obj;
            return (start == other.start || (start != null && start.equals(other.start)))
                    && (end == other.end || (end != null && end.equals(other.end)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.start != null ? this.start.hashCode() : 0);
        hash = 97 * hash + (this.end != null ? this.end.hashCode() : 0);
        return hash;
    }
}
