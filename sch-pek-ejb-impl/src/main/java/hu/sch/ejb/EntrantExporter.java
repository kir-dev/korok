package hu.sch.ejb;

import au.com.bytecode.opencsv.CSVWriter;
import hu.sch.domain.EntrantExportItem;
import hu.sch.domain.Semester;
import hu.sch.domain.enums.EntrantType;
import hu.sch.domain.enums.ValuationStatus;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
public class EntrantExporter {

    private static final Logger logger = LoggerFactory.getLogger(EntrantExporter.class);
    private static final String[] headers = new String[] {"Név", "Neptun", "E-mail", "Elsődleges kör", "Kapott belépők száma", "Indoklások"};

    private final Semester semester;
    private final EntrantType entrantType;
    private final int minEntrantNumber;
    private EntityManager em;

    public EntrantExporter(EntityManager em, Semester semester, EntrantType entrantType, int minEntrantNumber) {
        this.semester = semester;
        this.entrantType = entrantType;
        this.minEntrantNumber = minEntrantNumber;
        this.em = em;
    }

    public String toCSV() {
        return generateCSV(groupEntrantItems(getEntrants()));
    }

    private List<EntrantExportItem> getEntrants() {
        TypedQuery<EntrantExportItem> q = em.createQuery(
                "SELECT NEW hu.sch.domain.EntrantExportItem(u.id, u.firstName, u.lastName, u.neptunCode, u.emailAddress, g.name, e.valuation.group.name, e.valuationText) "
                + "FROM EntrantRequest e "
                + "INNER JOIN e.user u "
                // not everybody has a primary group, so this needs to be a left join
                + "LEFT JOIN u.sviePrimaryMembership pms "
                + "LEFT JOIN pms.group g "
                + "WHERE e.entrantType = :type "
                + "AND e.valuation.semester = :semester "
                + "AND e.valuation.entrantStatus = :status "
                + "AND e.valuation.nextVersion IS NULL", EntrantExportItem.class);

        q.setParameter("type", entrantType);
        q.setParameter("semester", semester);
        q.setParameter("status", ValuationStatus.ELFOGADVA);

        return q.getResultList();
    }

    private List<EntrantExportLine> groupEntrantItems(List<EntrantExportItem> entrants) {
        Map<Long, EntrantExportLine> result = new HashMap<>();
        for (EntrantExportItem item : entrants) {
            if (!result.containsKey(item.getUserId())) {
                result.put(item.getUserId(), new EntrantExportLine(item));
            } else {
                result.get(item.getUserId()).addItem(item);
            }
        }
        
        List<EntrantExportLine> resultList = new ArrayList<>(result.values());
        Collections.sort(resultList);

        return resultList;
    }

    private String generateCSV(List<EntrantExportLine> lines) {
        StringWriter strWriter = new StringWriter();
        CSVWriter writer = new CSVWriter(strWriter);
        writer.writeNext(headers);

        int count = 0;
        for (EntrantExportLine line : lines) {
            if (line.getEntrantNumber() >= minEntrantNumber) {
                ++count;
                writer.writeNext(line.getFields());
            }
        }

        logger.info("Exported {} entrants for (semester, type, min) ({}, {}, {})", count, semester, entrantType, minEntrantNumber);
        return strWriter.toString();
    }
}
