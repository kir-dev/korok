package hu.sch.ejb.test.group;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.enums.SvieMembershipType;
import hu.sch.domain.enums.SvieStatus;
import hu.sch.domain.user.User;
import hu.sch.ejb.GroupManagerBean;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.MembershipBuilder;
import hu.sch.ejb.test.builder.UserBuilder;
import hu.sch.services.GroupManagerLocal;
import java.util.List;
import javax.persistence.PersistenceException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tomi
 */
public class GroupManagerTest extends AbstractDatabaseBackedTest {

    GroupManagerLocal groupManager;

    @Override
    protected void before() {
        super.before();
        groupManager = new GroupManagerBean(getEm());
    }

    @Test
    public void getAllGroupsWithPrimaryMemberCount() {
        User u = new UserBuilder()
                .withSvieMemebership(SvieMembershipType.RENDESTAG)
                .withSvieStatus(SvieStatus.ELFOGADVA)
                .build();

        Membership ms = new MembershipBuilder()
                .withUser(u)
                .build();

        u.setSviePrimaryMembership(ms);

        getEm().persist(u);
        getEm().persist(ms.getGroup());
        getEm().persist(ms);
        getEm().flush();


        List<Group> groups = groupManager.getAllGroups(true);

        assertEquals(1, groups.size());
        assertNotNull(groups.get(0).getNumberOfPrimaryMembers());
        assertEquals(1L, (long)groups.get(0).getNumberOfPrimaryMembers());
    }
}
