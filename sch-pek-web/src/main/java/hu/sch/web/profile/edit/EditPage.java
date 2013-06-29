package hu.sch.web.profile.edit;

import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.profile.ProfilePage;

/**
 *
 * @author konvergal
 */
public class EditPage extends ProfilePage {

    public Person person;

    public EditPage() {
        super();
        try {
            person = ldapManager.getPersonByUid(getRemoteUser());
        } catch (PersonNotFoundException e) {
            getSession().error("A felhasználó nem található!");
            setResponsePage(getApplication().getHomePage());
            return;
        }

        setHeaderLabelText(person.getUid());

        add(new PersonForm("personForm", person));
    }
}
