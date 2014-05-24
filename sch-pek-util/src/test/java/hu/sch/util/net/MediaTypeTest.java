/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.util.net;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tomi
 */
public class MediaTypeTest {

    @Test
    public void equalsReturnTrueOnlyIfTypeAndSubtypeAreEqual() {
        MediaType m = MediaType.parse("image/jpeg");

        assertTrue(m.equals(MediaType.IMAGE_JPEG));
        assertFalse(m.equals(MediaType.IMAGE_GIF));
    }

    @Test
    public void parsingWellformattedMediaType() {
        MediaType m = MediaType.parse("image/jpeg");
        assertEquals("image", m.getType());
        assertEquals("jpeg", m.getSubType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parsingInvalidMediaType() {
        MediaType.parse("image/jpeg/error");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parsingNullString() {
        MediaType.parse(null);
    }

    @Test
    public void isAnyReturnsTrueWhenThereIsAtLeastOneMathing() {
        MediaType m = MediaType.parse("image/jpeg");

        assertTrue(m.isAny(MediaType.IMAGE_GIF, MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG));
    }

    @Test
    public void isAnyReturnsFalseOnlyWhenThereIsNoMatch() {
        MediaType m = MediaType.parse("image/jpeg");
        assertFalse(m.isAny(MediaType.TEXT_PLAIN, MediaType.TEXT_HTML));
    }

    @Test
    public void getContentTypeCreatesHTTPCompiantContentType() {
        assertEquals("image/png", MediaType.IMAGE_PNG.getContentType());
    }
}