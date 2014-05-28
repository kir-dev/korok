package hu.sch.domain;

import hu.sch.domain.user.User;
import java.io.Serializable;

/**
 * Egy felhasználó értékelését jellemzi (pont- és belépőkérelem)
 *
 * @author  messo
 * @since   2.3.1
 * @see     PointRequest
 * @see     EntrantRequest
 */
public class ValuationData implements Serializable {

    private User user;
    private Valuation valuation = null;
    private PointRequest pointRequest;
    private EntrantRequest entrantRequest;

    public ValuationData(User user, PointRequest pointRequest, EntrantRequest entrantRequest) {
        this.user = user;
        this.pointRequest = pointRequest;
        this.entrantRequest = entrantRequest;

        init();
    }

    private void init() {
        if (pointRequest == null) {
            pointRequest = new PointRequest();
            // ha a pontkérelem null, akkor lennie kell belépőnek
            valuation = entrantRequest.getValuation();
        }
        if (entrantRequest == null) {
            entrantRequest = new EntrantRequest();
            // ha a belépőkérelem null, akkor lennie kell pontnak
            valuation = pointRequest.getValuation();
        }
    }

    public User getUser() {
        return user;
    }

    public Group getGroup() {
        return valuation.getGroup();
    }

    public Semester getSemester() {
        return valuation.getSemester();
    }

    public EntrantRequest getEntrantRequest() {
        return entrantRequest;
    }

    public void setEntrantRequest(EntrantRequest eReq) {
        this.entrantRequest = eReq;
    }

    public PointRequest getPointRequest() {
        return pointRequest;
    }

    public void setPointRequest(PointRequest pReq) {
        this.pointRequest = pReq;
    }
}
