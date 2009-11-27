/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.svie;

import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.User;
import hu.sch.domain.profile.Person;
import hu.sch.services.SvieManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.components.ValidationStyleBehavior;
import hu.sch.web.components.customlinks.AttributeAjaxFallbackLink;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.kp.util.PatternHolder;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 *
 * @author aldaris
 */
public final class SvieRegistration extends SecuredPageTemplate {

    @EJB(name = "SvieManagerBean")
    SvieManagerLocal svieManager;
    private Person person = null;
    private User user;
    private SvieMembershipType choosed;

    /**
     * Ezzel a konstruktorral egyszerre gyorsítjuk a kódot és megoldjuk, hogy az
     * oldal ne legyen könyvjelzőzhető
     * @param user A felhasználó, aki szeretne SVIE-be regisztrálni
     */
    public SvieRegistration(final User _user) {
        createNavbarWithSupportId(34);
        this.user = _user;
        try {
            person = ldapManager.getPersonByVirId(user.getId().toString());
        } catch (PersonNotFoundException pnfe) {
            getSession().error("A felhasználó nem található.");
            throw new RestartResponseException(ShowUser.class);
        }

        setHeaderLabelText("SVIE Regisztráció");
        add(new FeedbackPanel("pagemessages"));

        Form<Person> form = new Form<Person>("registrationForm", new CompoundPropertyModel<Person>(person)) {

            @Override
            protected void onSubmit() {
                ldapManager.update(person);
                svieManager.applyToSvie(user, choosed);
                if (!continueToOriginalDestination()) {
                    setResponsePage(getApplication().getHomePage());
                }
                return;
            }
        };
        form.setModel(new CompoundPropertyModel<Person>(person));

        RequiredTextField<String> mothersNameTF = new RequiredTextField<String>("mothersName");
        mothersNameTF.add(new PatternValidator(PatternHolder.mothersNamePattern));
        mothersNameTF.add(new ValidationStyleBehavior());
        mothersNameTF.setLabel(new Model<String>("Anyja neve *"));
        form.add(mothersNameTF);
        form.add(new ValidationSimpleFormComponentLabel("mothersNameLabel", mothersNameTF));

        RequiredTextField<String> estGradTF = new RequiredTextField<String>("estimatedGraduationYear");
        estGradTF.add(new PatternValidator(PatternHolder.graduationYearPattern));
        estGradTF.add(new ValidationStyleBehavior());
        estGradTF.setLabel(new Model<String>("Egyetem várható befejezési ideje *"));
        form.add(estGradTF);
        form.add(new ValidationSimpleFormComponentLabel("estGradLabel", estGradTF));

        RequiredTextField<String> homePostalAddressTF = new RequiredTextField<String>("homePostalAddress");
        homePostalAddressTF.add(new ValidationStyleBehavior());
        homePostalAddressTF.setLabel(new Model<String>("Cím *"));
        form.add(homePostalAddressTF);
        form.add(new ValidationSimpleFormComponentLabel("homePostalAddressLabel", homePostalAddressTF));

        AttributeAjaxFallbackLink.setPerson(person);
        form.add(new AttributeAjaxFallbackLink("homePostalAddressAttributeLink", "homePostalAddressAttributeImg", "homePostalAddress"));

        final RadioGroup<SvieMembershipType> radioGroup = new RadioGroup<SvieMembershipType>("choices", new PropertyModel<SvieMembershipType>(this, "choosed"));
        form.add(radioGroup);
        List<SvieMembershipType> msTypes = new ArrayList<SvieMembershipType>();
        msTypes.add(SvieMembershipType.PARTOLOTAG);
        msTypes.add(SvieMembershipType.RENDESTAG);
        ListView lv = new ListView("choiceList", msTypes) {

            @Override
            protected void populateItem(ListItem item) {
                item.add(new Radio("radio", item.getModel()));
                item.add(new Label("name", item.getModelObject().toString()));
            }
        };
        radioGroup.add(lv);

        add(form);
    }
}

