package hu.sch.ejb;

import hu.sch.domain.PointHistory;
import hu.sch.domain.Semester;
import hu.sch.domain.user.User;
import hu.sch.services.PointHistoryManagerLocal;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
@Stateless
public class PointHistoryManagerBean implements PointHistoryManagerLocal {

    private final static Logger logger = LoggerFactory.getLogger(PointHistoryManagerBean.class);

    private static final String pointGenerationQuery =
            "SELECT p.user_id AS usr_id, LEAST(TRUNC(SQRT(SUM(p.sum * p.sum))),100) AS point "
            + "FROM ("
            + "SELECT pontigenyles.usr_id AS user_id, SUM(pontigenyles.pont) AS sum "
            + "FROM ertekelesek v "
            + "RIGHT JOIN pontigenyles ON pontigenyles.ertekeles_id = v.id "
            + "WHERE "
            + "v.next_version IS NULL "
            + "AND v.pontigeny_statusz = 'ELFOGADVA' "
            + "AND (v.semester = :semester OR v.semester = :prevSemester) "
            + "GROUP BY v.grp_id, pontigenyles.usr_id) AS p "
            + "GROUP BY p.user_id";

    @PersistenceContext
    private EntityManager em;

    @Override
    @Asynchronous
    public void generateForSemesterAsync(Semester semester) {
        logger.info("Starting point history generation batch job.");
        if (semester == null) {
            throw new IllegalArgumentException("semester cannot be null");
        }
        long then = System.currentTimeMillis();

        deleteHistoryForSemester(semester);
        generatePointHistoryForSemester(semester);

        long elapsed = System.currentTimeMillis() - then;
        logger.info("Elapsed time for point history batch job: {} ms", elapsed);

    }

    private void deleteHistoryForSemester(Semester semester) {
        Query q = em.createQuery("DELETE FROM PointHistory ph WHERE ph.semester = :semester");
        q.setParameter("semester", semester);

        q.executeUpdate();
    }

    private void generatePointHistoryForSemester(Semester semester) {
        Query q = em.createNativeQuery(pointGenerationQuery);
        q.setParameter("semester", semester.getId());
        q.setParameter("prevSemester", semester.getPrevious().getId());

        List<Object[]> results = q.getResultList();

        for (Object[] row : results) {
            BigInteger userId = (BigInteger) row[0];
            BigDecimal point = (BigDecimal) row[1];

            PointHistory ph = new PointHistory();
            ph.setPoint(point.intValue());
            ph.setSemester(semester);
            ph.setUser(em.getReference(User.class, userId.longValue()));

            em.persist(ph);
        }
    }
}
