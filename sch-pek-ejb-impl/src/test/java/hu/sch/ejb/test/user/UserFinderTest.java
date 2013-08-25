/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb.test.user;

import hu.sch.ejb.UserManagerBean;
import hu.sch.ejb.test.base.AbstractDatabaseBackedTest;
import hu.sch.ejb.test.builder.UserBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author tomi
 */
public class UserFinderTest extends AbstractDatabaseBackedTest {

    UserManagerBean bean;

    @Override
    protected void before() {
        new UserBuilder().withNeptun("ABCDEF").create(getEm());
        bean = new UserManagerBean(getEm());
    }

    @Test
    public void findByExistingNeptun() {
        Assert.assertNotNull(bean.findUserByNeptun("abcdef"));
    }

    @Test
    public void findByNonExistingNeptun() {
        Assert.assertNull(bean.findUserByNeptun("123456"));
    }

}
