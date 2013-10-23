package hu.sch.ejb.test.user;

import hu.sch.domain.user.LostPasswordToken;
import hu.sch.domain.user.User;
import hu.sch.ejb.AccountManagerBean;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.UserBuilder;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author balo
 */
public class LostPasswordTest extends AbstractDatabaseBackedTest {

    private AccountManagerBean bean;
    private int userCtr;
    private User[] users;
    private final Date now = new Date();
    private final Date expired1 = DateUtils.addMilliseconds(now, -(int) (AccountManagerBean.LOST_PW_TOKEN_VALID_MS + 1));
    private final Date expired2 = DateUtils.addDays(now, -2);

    public LostPasswordTest() {
    }

    @Override
    protected void before() {
        super.before();

        bean = new AccountManagerBean(getEm());

        users = new User[]{
            getEm().merge(getNextUser()),
            getEm().merge(getNextUser()),
            getEm().merge(getNextUser())
        };
    }

    private User getNextUser() {
        ++userCtr;
        return new UserBuilder().withFirstName("first" + userCtr)
                .withLastName(userCtr + "last")
                .withEmail("test" + userCtr + "@tst.tt").build();
    }

    @Test
    public void leaveValidLostPasswordToken() {
        getEm().persist(new LostPasswordToken(users[0], "token1", now));

        getEm().flush();

        bean.removeExpiredLostPasswordTokens();

        getEm().clear(); //this needs to refresh the states of the tokens after delete

        Assert.assertNotNull(getEm().find(LostPasswordToken.class, users[0].getId()));
    }

    @Test
    public void removeExpiredLostPasswordTokens() {

        getEm().persist(new LostPasswordToken(users[1], "token2", expired1));
        getEm().persist(new LostPasswordToken(users[2], "token3", expired2));
        getEm().flush();

        bean.removeExpiredLostPasswordTokens();

        getEm().clear(); //this needs to refresh the states of the tokens after delete

        Assert.assertNull(getEm().find(LostPasswordToken.class, users[1].getId()));
        Assert.assertNull(getEm().find(LostPasswordToken.class, users[2].getId()));
    }

}
