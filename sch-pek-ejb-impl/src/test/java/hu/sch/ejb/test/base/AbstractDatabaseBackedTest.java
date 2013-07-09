package hu.sch.ejb.test.base;

import hu.sch.ejb.test.util.TestConfiguration;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for tests that
 *
 * @author tomi
 */
public abstract class AbstractDatabaseBackedTest {

    private static final String PERSISTENCE_UNIT_NAME = "PEK-DOMAIN-TEST-PU";

    private static EntityManagerFactory emFactory;

    private EntityManager em;
    private EntityTransaction transaction;

    public AbstractDatabaseBackedTest() {

    }

    @Before
    public void setup() {
        em = getEntityManagerFactory().createEntityManager();

        transaction = em.getTransaction();
        transaction.begin();
        transaction.setRollbackOnly();

        before();
    }

    @After
    public void tearDown() {
        after();

        if (transaction != null) {
            transaction.rollback();
        }
        if (em != null) {
            em.close();
        }
    }

    /**
     * Override this method to initialize your tests.
     *
     * It is called after the persistence context has been set up.
     */
    protected void before() {

    }

    /**
     * Override this method to tear down your tests.
     *
     * It is called before the persistence context shuts down.
     */
    protected void after() {

    }

    protected EntityManager getEm() {
        return em;
    }

    private static EntityManagerFactory getEntityManagerFactory() {
        if (emFactory == null) {
            Map<String, String> additionalConfigs = new HashMap<>();
            additionalConfigs.put("javax.persistence.jdbc.password", TestConfiguration.getPassword());
            emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, additionalConfigs);
        }
        return emFactory;
    }
}
