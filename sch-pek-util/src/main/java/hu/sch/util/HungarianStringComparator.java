package hu.sch.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Simple class to hungarian string compare.
 *
 * @author ksisu
 */
public class HungarianStringComparator implements Comparator<String> {
 
    public static int scompare(String s1, String s2) {
        return new HungarianStringComparator().compare(s1, s2);
    }
 
    @Override
    public int compare(String s1, String s2) {
        Collator huCollator = Collator.getInstance(new Locale("hu"));
        return huCollator.compare(s1, s2);
    }
}
