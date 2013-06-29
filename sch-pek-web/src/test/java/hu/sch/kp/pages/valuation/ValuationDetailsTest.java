package hu.sch.kp.pages.valuation;

import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationPeriod;
import hu.sch.domain.ValuationStatistic;
import hu.sch.ejb.SystemManagerBean;
import hu.sch.ejb.ValuationManagerBean;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.test.WebTest;
import java.util.List;
import javax.naming.NamingException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.Test;

/**
 *
 * @author messo
 * @since 2.3.1
 */
public class ValuationDetailsTest extends WebTest {

    @Test
    public void render() {
        try {
            SystemManagerLocal systemManager = lookupEJB(SystemManagerBean.class);
            // elmentjük az előző időszakot
            ValuationPeriod prevIdoszak = systemManager.getErtekelesIdoszak();
            // mindenképpen legyen elbírálási időszak
            systemManager.setErtekelesIdoszak(ValuationPeriod.ERTEKELESELBIRALAS);

            // kérjük le a statisztikákat
            ValuationManagerLocal valuationManager = lookupEJB(ValuationManagerBean.class);
            List<ValuationStatistic> list = valuationManager.findValuationStatisticForSemester();
            if (!list.isEmpty()) {
                // válasszunk ki belőle az elsőt és nézzük meg, hogy lerenderelhető-e
                Valuation val = list.get(0).getValuation();
                tester.startPage(ValuationDetails.class, new PageParameters().add("id", val.getId()));
                tester.assertRenderedPage(ValuationDetails.class);
            }

            systemManager.setErtekelesIdoszak(prevIdoszak);
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
    }
}
