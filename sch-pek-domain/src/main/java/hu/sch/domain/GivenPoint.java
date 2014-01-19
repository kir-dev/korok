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

    public GivenPoint(String neptun, Integer points) {
        this.neptun = neptun;
        this.points = points;
    }

    public String getNeptun() {
        return neptun;
    }

    public Integer getPoints() {
        return points;
    }
}
