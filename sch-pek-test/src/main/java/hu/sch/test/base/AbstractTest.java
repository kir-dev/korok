package hu.sch.test.base;

import org.junit.BeforeClass;

/**
 *
 * @author aldaris
 */
public abstract class AbstractTest {

    private static final String LOGGING_CONFIG = "java.util.logging.config.file";

    @BeforeClass
    public static void initSystemProperties() {
        System.setProperty(LOGGING_CONFIG, TestConfig.getProperty(TestConfig.GF_ROOT) + "/domains/domain1/config/logging.properties");
    }
}
