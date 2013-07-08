package hu.sch.web.kp.valuation.request;

import hu.sch.domain.AbstractValuationRequest;
import hu.sch.domain.user.User;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationPeriod;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.valuation.Valuations;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValueConversionException;

/**
 *
 * @author messo
 */
public class Requests extends KorokPage {

    @EJB(name = "ValuationManagerBean")
    protected ValuationManagerLocal valuationManager;
    protected Valuation valuation = null;
    protected ValuationPeriod valPeriod = null;

    public Requests(PageParameters params) {
        Long vid = null;
        try {
            vid = params.get("vid").toLong();
        } catch (StringValueConversionException ex) {
        }
        // TODO(messo): kapcsoljuk a Valuation mellé a Group-ot is, így kicsit gyorsabb
        if (vid == null || (valuation = valuationManager.findErtekelesById(vid)) == null) {
            error("Nincs ilyen értékelés!");
            setResponsePage(Valuations.class);
            return;
        }

        valPeriod = systemManager.getErtekelesIdoszak();

        setDefaultModel(new CompoundPropertyModel<Valuation>(valuation));
        add(new Label("group.name"));
        add(new Label("semester"));
    }

    /**
     * Removes requests which don't belong to any active member. (In case of
     * members changed between requests)
     *
     * @param requests
     * @param actualMembers
     */
    public static void cleanOldBoysFromRequests(final List<? extends AbstractValuationRequest> requests,
            final List<User> actualMembers) {

        for (final Iterator<? extends AbstractValuationRequest> requestIterator = requests.iterator();
                requestIterator.hasNext();) {

            final AbstractValuationRequest request = requestIterator.next();
            if (!actualMembers.contains(request.getUser())) {
                requestIterator.remove();
            }
        }
    }
}
