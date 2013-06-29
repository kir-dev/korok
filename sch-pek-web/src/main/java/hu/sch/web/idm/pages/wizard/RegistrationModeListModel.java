package hu.sch.web.idm.pages.wizard;

import hu.sch.web.PhoenixApplication;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.wicket.Application;

/**
 * This model needs because wicket doesn't regenerate the Listview of RegistrationMode-s.
 * This way we can control the content of the listview every page reload.
 *
 * @author balo
 */
class RegistrationModeListModel implements org.apache.wicket.model.IModel<List<RegistrationMode>> {

    @Override
    public List<RegistrationMode> getObject() {
        final List<RegistrationMode> modes = new LinkedList<RegistrationMode>(Arrays.asList(RegistrationMode.values()));
        final boolean isNewbieTime = ((PhoenixApplication) Application.get()).isNewbieTime();

        if (!isNewbieTime) {
            for (Iterator<RegistrationMode> it = modes.iterator(); it.hasNext();) {
                final RegistrationMode mode = it.next();

                //if we aren't in newbieTime, remove newbie reg options from the list
                if (mode.isNewbie()) {
                    it.remove();
                }
            }
        }

        return Collections.unmodifiableList(modes);
    }

    @Override
    public void setObject(final List<RegistrationMode> object) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void detach() {
    }
}
