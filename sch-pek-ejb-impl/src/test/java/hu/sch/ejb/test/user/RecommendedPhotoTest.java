package hu.sch.ejb.test.user;

import hu.sch.domain.SpotImage;
import hu.sch.services.config.ImageUploadConfig;
import hu.sch.domain.user.User;
import hu.sch.ejb.EjbConstructorArgument;
import hu.sch.ejb.UserManagerBean;
import hu.sch.services.config.Configuration;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.UserBuilder;
import hu.sch.ejb.test.util.Queries;
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
        when(cfg.getImageUploadConfig()).thenReturn(new ImageUploadConfig("/", 200));

        final EjbConstructorArgument args = new EjbConstructorArgument(getEm());
        args.setConfig(cfg);
        bean = new UserManagerBean(args);
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
