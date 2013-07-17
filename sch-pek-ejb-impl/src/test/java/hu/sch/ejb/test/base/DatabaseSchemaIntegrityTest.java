package hu.sch.ejb.test.base;

import hu.sch.domain.user.IMAccount;
import hu.sch.domain.user.IMProtocol;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author tomi
 */
public class DatabaseSchemaIntegrityTest extends AbstractDatabaseBackedTest {

    @Test
    public void entityMappingIsValid() {
        assertNotEquals(0L, getEm().getMetamodel().getEntities().size());
    }

    @Test
    public void saveEntity() {
        getEm().persist(new IMAccount(IMProtocol.icq, "hello world"));
        long count = (long) getEm().createQuery("SELECT COUNT(i) from IMAccount i").getSingleResult();
        assertNotEquals(0L, count);
    }

    @Test
    public void savedEntityHasId() {
        getEm().persist(new IMAccount(IMProtocol.icq, "hello world"));
        IMAccount acc = getEm().createQuery("SELECT i FROM IMAccount i", IMAccount.class).getSingleResult();
        assertNotNull(acc.getId());
    }

    @Test
    public void imAccountsTableIsEmpty() {
        long count = (long) getEm().createQuery("SELECT COUNT(i) from IMAccount i").getSingleResult();
        assertEquals(0L, count);
    }
}