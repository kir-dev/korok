package hu.sch.ejb.test.user;

import hu.sch.domain.SpotImage;
import hu.sch.domain.config.Configuration;
import hu.sch.domain.config.ImageUploadConfig;
import hu.sch.domain.user.User;
import hu.sch.ejb.EjbConstructorArgument;
import hu.sch.ejb.UserManagerBean;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.UserBuilder;
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
        Configuration cfg =  mock(Configuration.class);
        when(cfg.getImageUploadConfig_instance()).thenReturn(new ImageUploadConfig("/", 200));

        Configuration.override(cfg);

        bean = new UserManagerBean(new EjbConstructorArgument(getEm()));
        createUser();
    }

    @Test
    public void declinePhoto() {
        bean.declineRecommendedPhoto(bean.findUserByNeptun(neptun));

        assertFalse(bean.findUserByNeptun(neptun).isShowRecommendedPhoto());
        assertEquals(0L, getEm().createQuery("SELECT COUNT(i) FROM SpotImage i").getSingleResult());
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
