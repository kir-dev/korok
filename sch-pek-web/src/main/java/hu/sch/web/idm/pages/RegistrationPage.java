package hu.sch.web.idm.pages;

import hu.sch.web.kp.KorokPage;

/**
 *
 * @author aldaris
 * @author tomi
 */
public class RegistrationPage extends KorokPage {

    public RegistrationPage() {
        setHeaderLabelText("Regisztráció");
    }

    @Override
    protected boolean needsLogin() {
        return false;
    }
}
