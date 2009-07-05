/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.profile.pages.edit;

import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.error.ErrorPage;
import hu.sch.web.profile.pages.template.ProfilePage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author konvergal
 */
public class EditPage extends ProfilePage {

    public Person person;

    public EditPage() {
        super();
        add(new FeedbackPanel("feedbackPanel"));
        try {
            person = ldapManager.getPersonByUid(getUid());
        } catch (PersonNotFoundException e) {
            getSession().error("A felhasználó nem található!");
            setResponsePage(ErrorPage.class);
            return;
        }

        setHeaderLabelModel(new PropertyModel<Person>(person, "uid"));
        
        add(new PersonForm("personForm", person));
    }
}