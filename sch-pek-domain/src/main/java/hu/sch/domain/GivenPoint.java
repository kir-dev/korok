package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Id;

/**
 * A getPointsForSemester tárolt eljárás eredményeihez használjuk.
 *
 * A tárolt eljárás és a hozzá tartozó típus definíciója a
 * resources/pointsForSemester.sql-ben található
 * 
 * @author  messo
 * @author  balo
 * @since   2.3.1
 */
public class GivenPoint implements Serializable {

    @Id
    private String neptun;
    private Integer points;

    //fixme #67
    //temporary factory method; it can be removed after we moved stored procedures into the code (#67)
    public static GivenPoint createFrom(final Object[] record) {
        final GivenPoint result = new GivenPoint();
        result.setNeptun((String) record[0]);
        result.setPoints(Integer.valueOf(String.valueOf(record[1])));

        return result;
    }

    public String getNeptun() {
        return neptun;
    }

    public void setNeptun(String neptun) {
        this.neptun = neptun;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
