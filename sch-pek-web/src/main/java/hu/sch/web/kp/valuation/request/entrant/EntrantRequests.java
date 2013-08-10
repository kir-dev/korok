package hu.sch.web.kp.valuation.request.entrant;

import hu.sch.domain.enums.ValuationPeriod;
import hu.sch.web.kp.valuation.request.Requests;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author messo
 */
public class EntrantRequests extends Requests {

    public EntrantRequests(PageParameters params) {
        super(params);
        if (valuation == null) {
            return;
        }

        // Mikor szerkeszthető egy pontozás:
        // a) Ha értékelés leadás van + körvezető, VAGY
        // b) Ha értékelés elbírálás van + JETI
        // a kettő közül valamelyik igaz ÉS
        // 1. a belépőkérelmeket még nem fogadták el
        // 2. a mostani félévhez tartozik
        // 3. a legfrissebb verzió, tehát nem egy régebbi, már elavult értékelés
        if (!valuation.isObsolete() && !valuation.entrantsAreAccepted()
                && valuation.getSemester().equals(systemManager.getSzemeszter())
                && ((valPeriod == ValuationPeriod.ERTEKELESLEADAS && isUserGroupLeader(valuation.getGroup()))
                || (valPeriod == ValuationPeriod.ERTEKELESELBIRALAS && isCurrentUserJETI()))) {
            setHeaderLabelText("Belépőigénylések leadása");
            add(new EntrantRequestEditor("panel", valuation));
        } else {
            setHeaderLabelText("Kiosztott belépők");
            add(new EntrantRequestViewer("panel", valuation));
        }
    }
}
