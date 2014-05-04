package hu.sch.services;

import hu.sch.domain.Semester;
import javax.ejb.Local;

/**
 *
 * @author tomi
 */
@Local
public interface PointHistoryManagerLocal {

    /**
     * Generate point history records for a given semester.
     * @param semester the semester to generate for-
     */
    void generateForSemesterAsync(Semester semester);
}
