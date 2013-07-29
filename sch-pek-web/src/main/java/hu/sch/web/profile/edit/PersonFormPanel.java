package hu.sch.web.profile.edit;

import hu.sch.domain.user.User;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Panel for holding the person form.
 *
 * @author tomi
 */
public class PersonFormPanel extends Panel {

    protected final User user;
    protected final Form<User> form;

    public PersonFormPanel(String id, User user) {
        super(id);
        this.user = user;
        this.form = new PersonForm("personForm", user);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.add(form);
    }
}
