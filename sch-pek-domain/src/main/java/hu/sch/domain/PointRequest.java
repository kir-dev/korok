package hu.sch.domain;

import hu.sch.domain.user.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author hege
 */
@Entity
@Table(name = "pontigenyles")
public class PointRequest extends AbstractValuationRequest {

    @Column(name = "pont")
    private Integer point;

    public PointRequest() {
        this(null, 0);
    }

    public PointRequest(final User user, final Integer point) {
        this.point = point;
        setUser(user);
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(final Integer point) {
        this.point = point;
    }

    /**
     * Lemásoljuk a kérelmet, hogy egy új értékeléshez elmenthessük.
     *
     * @param v az új értékelés, amihez lemásoltuk a kérelmet
     * @return másolat, amit elmenthetünk újként
     */
    public PointRequest copy(final Valuation v) {
        final PointRequest pr = new PointRequest();
        pr.setValuation(v);
        pr.setPoint(point);
        pr.setUser(getUser());
        return pr;
    }
}
