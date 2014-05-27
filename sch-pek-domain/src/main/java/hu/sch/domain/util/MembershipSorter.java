package hu.sch.domain.util;

import hu.sch.domain.Membership;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author tomi
 */
public class MembershipSorter {

    private final Collection<Membership> memberships;

    public MembershipSorter(Collection<Membership> memberships) {
        this.memberships = memberships;
    }

    /**
     * Sort memberships. Put active memberships first, but falls back to group
     * name comparison when when memberships status (active or inactive) equals.
     *
     * It does not change the underlying {@link Membership} collection.
     *
     * @return a sorted list of memberships.
     */
    public List<Membership> sort() {
        if (memberships == null) {
            return Collections.emptyList();
        }
        List<Membership> result = new ArrayList<>(memberships);
        Collections.sort(result, new MembershipComparator());

        return result;
    }

    private static class MembershipComparator implements Comparator<Membership> {

        @Override
        public int compare(Membership ms1, Membership ms2) {
            // put active before inactive memberships
            if (ms1.getEnd() == null ^ ms2.getEnd() == null) {
                return ms1.getEnd() == null ? -1 : 1;
            }
            // fallback to lexical sorting of group names
            return ms1.getGroup().compareTo(ms2.getGroup());
        }

    }
}
