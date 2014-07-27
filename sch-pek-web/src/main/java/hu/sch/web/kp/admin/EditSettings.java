package hu.sch.web.kp.admin;

import hu.sch.services.PointHistoryManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.KorokPage;
import javax.inject.Inject;
import org.apache.wicket.RestartResponseException;

/**
 *
 * @author aldaris
 */
public class EditSettings extends KorokPage {


    @Inject
    private PointHistoryManagerLocal pointHistoryManager;

    @Inject
    private ValuationManagerLocal valuationManager;

    public EditSettings() {
        //Jogosultságellenőrzés
        if (!(isCurrentUserJETI() || isCurrentUserSVIE() || isCurrentUserAdmin())) {
            getSession().error("Nincs jogod a megadott művelethez");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        setHeaderLabelText("Adminisztráció");

        JetiFragment jetiFragment = new JetiFragment("jetifragment", "jetipanel");
        SvieFragment svieFragment = new SvieFragment("sviefragment", "sviepanel");
        KirDevFragment kirDevFragment = new KirDevFragment("kirdevfragment", "kirdevpanel");

        jetiFragment.setVisible(isCurrentUserJETI());
        svieFragment.setVisible(isCurrentUserSVIE());
        kirDevFragment.setVisible(isCurrentUserAdmin());

        add(jetiFragment, svieFragment, kirDevFragment);
    }
}
