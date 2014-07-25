package hu.sch.web.kp.logout;

import hu.sch.web.kp.KorokPage;

/**
 *
 * @author aldaris
 */
public class Logout extends KorokPage {

    public Logout() {
        getSession().invalidate();
        setHeaderLabelText("Kijelentkezés");
        info("Sikeres kijelentkezés. :)");
    }

    @Override
    protected boolean needsLogin() {
        return false;
    }
}
