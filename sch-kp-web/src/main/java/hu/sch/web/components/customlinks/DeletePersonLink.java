/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components.customlinks;

import hu.sch.domain.profile.Person;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.components.ConfirmationBoxRenderer;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author aldaris
 */
public class DeletePersonLink extends Panel {

    @EJB(name = "LdapManagerBean")
    private LdapManagerLocal ldapManager;

    public DeletePersonLink(String id, final Person person, final Class forwardTo) {
        super(id);

        Link<Void> deletePersonLink = new Link<Void>("deletePersonLink") {

            @Override
            public void onClick() {
                try {
                    ldapManager.deletePersonByUid(person.getUid());
                } catch (PersonNotFoundException e) {
                }

                getSession().info("A felhasználó (" + person.getUid() + ", " + person.getFullName() + ", "
                        + person.getMail() + ") sikeresen törölve lett.");
                setResponsePage(forwardTo);
            }
        };
        deletePersonLink.add(
                new ConfirmationBoxRenderer("return confirm(\"Biztos, hogy törölni akarod a felasználót? \\n Uid: "
                + person.getUid() + "\\n Név: " + person.getFullName() + "\\n Mail: "
                + person.getMail() + "\");"));
        add(deletePersonLink);
    }
}
