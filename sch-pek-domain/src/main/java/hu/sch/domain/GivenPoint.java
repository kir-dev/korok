package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;

/**
 * Dummy entitás, csak a getPointsForSemester tárolt eljárás eredményeihez használjuk.
 *
 * A tárolt eljárás és a hozzá tartozó típus definíciója a
 * resources/pointsForSemester.sql-ben található
 * 
 * @author  messo
 * @since   2.3.1
 */
@Entity
@NamedNativeQuery(name = GivenPoint.getDormitoryPoints,
query = "SELECT * FROM getPointsForSemester(:semester, :prevSemester)",
resultClass = GivenPoint.class)
public class GivenPoint implements Serializable {

    public static final String getDormitoryPoints = "getDormitoryPoints";

    @Id
    private String neptun;
    private Integer points;

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
