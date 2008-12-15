/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.profile;

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
        try {
            person = LDAPPersonManager.getInstance().getPersonByUid(getUid());
        }
        catch (PersonNotFoundException e) {
            setResponsePage(new ErrorPage("A felhasználó nem található!"));
            return;
        }

        setHeaderLabelModel(new PropertyModel(person, "uid"));
        add(new FeedbackPanel("feedbackPanel"));
        
        add(new PersonForm("personForm", person) {

            @Override
            protected void onSubmit() {
                super.onSubmit();
                setResponsePage(new ShowPersonPage("Sikeres adatmódosítás. :)"));
            }
        });
    }
}