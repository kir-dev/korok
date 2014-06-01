package hu.sch.ejb.test.user;

import hu.sch.domain.SpotImage;
import hu.sch.util.config.ImageUploadConfig;
import hu.sch.domain.user.User;
import hu.sch.ejb.UserManagerBean;
import hu.sch.util.config.Configuration;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.UserBuilder;
import hu.sch.ejb.test.util.Queries;
import hu.sch.util.ConfigurationStub;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author tomi
 */
public class RecommendedPhotoTest extends AbstractDatabaseBackedTest {

    private UserManagerBean bean;
    private final String neptun = "ABCDEF";

    @Override
    protected void before() {
        ConfigurationStub cfg = new ConfigurationStub();
        cfg.setImageUploadConfig(new ImageUploadConfig("/", 200, 200));

        bean = new UserManagerBean(getEm());
        bean.setConfig(cfg);
        createUser();
    }

    @Test
    public void declinePhoto() {
        bean.declineRecommendedPhoto(bean.findUserByNeptun(neptun));

        assertFalse(bean.findUserByNeptun(neptun).isShowRecommendedPhoto());
        assertEquals(0, (long)Queries.count(getEm(), SpotImage.class));
    }

    private void createUser() {
        UserBuilder b = new UserBuilder().withNeptun(neptun);
        User user = b.build();
        user.setShowRecommendedPhoto(true);

        SpotImage img = new SpotImage();
        img.setImagePath("");
        img.setNeptunCode(neptun);

        getEm().persist(user);
        getEm().persist(img);
        getEm().flush();
        getEm().clear();
    }
}
