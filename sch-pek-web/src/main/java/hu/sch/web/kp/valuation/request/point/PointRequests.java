package hu.sch.web.kp.valuation.request.point;

import hu.sch.domain.enums.ValuationPeriod;
import hu.sch.web.kp.valuation.request.Requests;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author messo
 */
public class PointRequests extends Requests {

    public PointRequests(PageParameters params) {
        super(params);
        if (valuation == null) {
            return;
        }

        // Mikor szerkeszthető egy pontozás:
        // a) Ha értékelés leadás van + körvezető, VAGY
        // b) Ha értékelés elbírálás van + JETI
        // a kettő közül valamelyik igaz ÉS
        // 1. a pontokkérelmeket még nem fogadták el
        // 2. a mostani félévhez tartozik
        // 3. a legfrissebb verzió, tehát nem egy régebbi, már elavult értékelés
        if (!valuation.isObsolete() && !valuation.pointsAreAccepted()
                && valuation.getSemester().equals(systemManager.getSzemeszter())
                && ((valPeriod == ValuationPeriod.ERTEKELESLEADAS && isUserGroupLeader(valuation.getGroup()))
                || (valPeriod == ValuationPeriod.ERTEKELESELBIRALAS && isCurrentUserJETI()))) {
            setHeaderLabelText("Pontigénylés leadása");
            add(new PointRequestEditor("panel", valuation));
        } else {
            setHeaderLabelText("Kiosztott pontok");
            add(new PointRequestViewer("panel", valuation));
        }
    }
}
