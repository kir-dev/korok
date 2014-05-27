package hu.sch.domain.util;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

/**
 *
 * @author tomi
 */
public class MembershipSorterTest {

    @Test
    public void activeMembershipIsBeforeInactive() {
        Membership inactive = new Membership();
        inactive.setEnd(new Date());
        Membership active = new Membership();
        List<Membership> list = Arrays.asList(inactive, active);

        MembershipSorter sorter = new MembershipSorter(list);
        assertThat(sorter.sort()).containsExactly(active, inactive);
    }

    @Test
    public void returnsEmptyListForNull() {
        List<Membership> result = new MembershipSorter(null).sort();
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    public void fallsBackToGroupNameComparison() {
        Membership ms1 = new Membership();
        Group g1 = new Group();
        g1.setName("a");
        ms1.setGroup(g1);

        Membership ms2 = new Membership();
        Group g2 = new Group();
        g2.setName("b");
        ms2.setGroup(g2);

        List<Membership> result = new MembershipSorter(Arrays.asList(ms2, ms1)).sort();
        assertThat(result).containsExactly(ms1, ms2);
    }
}
