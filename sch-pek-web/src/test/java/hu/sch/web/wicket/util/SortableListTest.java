package hu.sch.web.wicket.util;

import hu.sch.test.base.AbstractTest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author  messo
 */
public class SortableListTest extends AbstractTest {

    private static class TestObject {

        private String string;
        private Date date;

        public TestObject(String string, Date date) {
            this.string = string;
            this.date = date;
        }

        public String getString() {
            return string;
        }

        public Date getDate() {
            return date;
        }
    }
    private static final Date d1;
    private static final Date d2;
    private static final Date d3;
    private static final Date d4;
    private static final Date d5;
    static {
        Calendar c = Calendar.getInstance();
        c.set(2010, 05, 07);
        d4 = c.getTime();
        c.add(Calendar.DATE, 1);
        d3 = c.getTime();
        c.add(Calendar.DATE, 1);
        d2 = c.getTime();
        c.add(Calendar.DATE, 1);
        d1 = c.getTime();
        d5 = null;
    }
    private static final TestObject[] testObjects = {
        new TestObject("Lorem", d2),
        new TestObject("ipsum", d4),
        new TestObject("dolor", d1),
        new TestObject("sit", d3),
        new TestObject("amet", d5)
    };
    private static SortableList<TestObject> list = new SortableList<TestObject>();

    @Before
    public void listInit() {
        list.setList(Arrays.asList(testObjects));
    }

    @Test
    public void sortByStringAsc() {
        list.sort(new SortParam("string", true));
        assertEquals(get(0).getString(), "amet");
        assertEquals(get(1).getString(), "dolor");
        assertEquals(get(2).getString(), "ipsum");
        assertEquals(get(3).getString(), "Lorem");
        assertEquals(get(4).getString(), "sit");
    }

    @Test
    public void sortByStringDesc() {
        list.sort(new SortParam("string", false));
        assertEquals(get(0).getString(), "sit");
        assertEquals(get(1).getString(), "Lorem");
        assertEquals(get(2).getString(), "ipsum");
        assertEquals(get(3).getString(), "dolor");
        assertEquals(get(4).getString(), "amet");
    }

    @Test
    public void sortByDateAsc() {
        list.sort(new SortParam("date", true));
        assertEquals(get(0).getDate(), d5);
        assertEquals(get(1).getDate(), d4);
        assertEquals(get(2).getDate(), d3);
        assertEquals(get(3).getDate(), d2);
        assertEquals(get(4).getDate(), d1);
    }

    @Test
    public void sortByDateDesc() {
        list.sort(new SortParam("date", false));
        assertEquals(get(0).getDate(), d1);
        assertEquals(get(1).getDate(), d2);
        assertEquals(get(2).getDate(), d3);
        assertEquals(get(3).getDate(), d4);
        assertEquals(get(4).getDate(), d5);
    }

    private TestObject get(int idx) {
        return list.getList().get(idx);
    }
}
