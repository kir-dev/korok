package hu.sch.web.idm.pages;

import hu.sch.web.idm.pages.wizard.RegisterWizard;
import hu.sch.web.kp.KorokPage;
import org.apache.wicket.RestartResponseException;

/**
 *
 * @author aldaris
 */
public class RegistrationPage extends KorokPage {

    public RegistrationPage() {
        setHeaderLabelText("Regisztráció");
        createNavbarWithSupportId(33);
        if (getRemoteUser() != null) {
            getSession().error("Már be vagy jelentkezve, miért is szeretnél újra regisztrálni?");
            throw new RestartResponseException(getApplication().getHomePage());
        }
        add(new RegisterWizard("wizard"));
    }

    @Override
    protected boolean needsLogin() {
        return false;
    }
}
