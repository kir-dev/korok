package hu.sch.util.test;

import hu.sch.util.Strings;
import org.junit.Test;
import org.junit.Assert;

/**
 *
 * @author tomi
 */
public class StringsTest {

    @Test
    public void bytesAreAlwaysRepresentedWithTwoChars() {
        String hex = Strings.hex(new byte[] { 1 });
        Assert.assertEquals("01", hex);
    }

    @Test
    public void bytesAreRepresentedWithLowerCaseChars() {
        String hex = Strings.hex(new byte[] { 15 });
        Assert.assertEquals("0f", hex);
    }
}
