package hu.sch.util;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ksisu
 */
public class HungarianStringComparatorTest {

    public HungarianStringComparatorTest() {
    }

    @Test
    public void testCompare() {
        String input[] = {"w", "y", "l", "e", "f", "u", "ó", "i", "ö", "s", "é", "a", "d", "m", "ú", "í", "x", "ü", "n", "á", "q", "p", "k", "ő", "c", "t", "z", "r", "b", "v", "o", "j", "ű", "h", "g"};
        String expResult[] = {"a", "á", "b", "c", "d", "e", "é", "f", "g", "h", "i", "í", "j", "k", "l", "m", "n", "o", "ó", "ö", "ő", "p", "q", "r", "s", "t", "u", "ú", "ü", "ű", "v", "w", "x", "y", "z"};
        Arrays.sort(input, new HungarianStringComparator());
        assertArrayEquals(expResult, input);
    }
}