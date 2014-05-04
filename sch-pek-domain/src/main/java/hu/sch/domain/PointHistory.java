package hu.sch.domain;

import hu.sch.domain.user.User;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * A PointHistory entity is representing a user's community points for a given
 * semester and group.
 *
 * @author tomi
 * @since 2.6.3
 *
 */
@Entity
@Table(name = "point_history")
@SequenceGenerator(name = "point_history_seq", allocationSize = 1, sequenceName = "point_history_seq")
@NamedQueries({
    @NamedQuery(name = PointHistory.findBySemester, query = "SELECT ph FROM PointHistory ph JOIN FETCH ph.user WHERE ph.semester = :semester"),
    @NamedQuery(name = PointHistory.findByUser, query = "SELECT ph FROM PointHistory ph WHERE ph.user = :user ORDER BY ph.semester DESC"),
})
public class PointHistory implements Serializable {

    public static final String findBySemester = "findBySemester";
    public static final String findByUser = "findByUser";

    @Id
    @GeneratedValue(generator = "point_history_seq")
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usr_id")
    private User user;

    @Column(name = "point")
    @NotNull
    private Integer point;

    @Embedded
    @NotNull
    private Semester semester;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }
}
