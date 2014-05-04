package hu.sch.ejb;

import hu.sch.domain.EntrantExportItem;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

/**
 * This class represents a line in the the entrant export csv.
 *
 * @author tomi
 * @since 2.6.3
 */
public class EntrantExportLine implements Comparable<EntrantExportLine> {
    private static final String PLACEHOLDER = "-";

    /**
     * User id
     */
    private Long id;
    /**
     * User's full name
     */
    private String fullName;

    /**
     * Items for the line
     */
    private List<EntrantExportItem> items;

    public EntrantExportLine(EntrantExportItem item) {
        id = item.getUserId();
        fullName = item.getFullName();
        items = new ArrayList<>();
        items.add(item);
    }

    public void addItem(EntrantExportItem item) {
        assert item.getUserId().equals(id);
        assert item.getFullName().equals(fullName);

        items.add(item);
    }

    @Override
    public int compareTo(EntrantExportLine o) {
        Collator collator = Collator.getInstance(new Locale("hu"));
        return collator.compare(this.fullName, o.fullName);
    }

    public int getEntrantNumber() {
        return items.size();
    }

    public String[] getFields() {
        EntrantExportItem first =  items.get(0);

        String[] fields = new String[] {
            fullName,
            valueOfPlaceholder(first.getNeptun()),
            first.getEmail(),
            valueOfPlaceholder(first.getPrimaryGroup()),
            String.valueOf(getEntrantNumber()),
            getValuationText()
        };

        return fields;
    }

    private String getValuationText() {
        List<String> list = new ArrayList<>();
        for (EntrantExportItem item : items) {
            StringBuilder builder = new StringBuilder("*");
            builder.append(item.getValuationGroupName());
            builder.append("*: ");
            builder.append(item.getValuationText());
            list.add(builder.toString());

        }
        return StringUtils.join(list, " || ");
    }

    private String valueOfPlaceholder(String value) {
        return StringUtils.isBlank(value) ? PLACEHOLDER : value;
    }
}
