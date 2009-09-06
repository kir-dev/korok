/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.svie;

import hu.sch.domain.Membership;
import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.SvieStatus;
import hu.sch.domain.User;
import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.components.ValidationStyleBehavior;
import hu.sch.web.components.customlinks.AttributeAjaxFallbackLink;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.kp.util.PatternHolder;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 *
 * @author aldaris
 */
public final class SvieRegistration extends SecuredPageTemplate {

    private Person person = null;
    private User user;
    private String choosed;

    /**
     * Ezzel a konstruktorral egyszerre gyorsítjuk a kódot és megoldjuk, hogy az
     * oldal ne legyen könyvjelzőzhető
     * @param user A felhasználó, aki szeretne SVIE-be regisztrálni
     */
    public SvieRegistration(final User _user) {
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
                SvieMembershipType type = SvieMembershipType.valueOf(choosed);
                user.setSvieMembershipType(type);
                user.setSvieStatus(SvieStatus.FELDOLGOZASALATT);
                userManager.updateUser(user);
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

        IModel<List<KeyValuePair>> membershipTypes = new LoadableDetachableModel<List<KeyValuePair>>() {

            @Override
            protected List<KeyValuePair> load() {
                List<KeyValuePair> l = new ArrayList<KeyValuePair>();
                for (Membership membership : user.getMemberships()) {
                    if (membership.getEnd() == null && membership.getGroup().getIsSvie()) {
                        l.add(new KeyValuePair(SvieMembershipType.RENDESTAG, "Rendes tag"));
                        break;
                    }
                }
                l.add(new KeyValuePair(SvieMembershipType.PARTOLOTAG, "Pártoló tag"));
                return l;
            }
        };
        RadioChoice<KeyValuePair> msTypeRadioChoice =
                new RadioChoice<KeyValuePair>("radiochoice", new PropertyModel<KeyValuePair>(this, "choosed"), membershipTypes);
        msTypeRadioChoice.setChoiceRenderer(new MsTypeRadioChoices());
        msTypeRadioChoice.setLabel(new Model<String>("Tagságtípus"));
        msTypeRadioChoice.setRequired(true);
        form.add(msTypeRadioChoice);
        form.add(new SimpleFormComponentLabel("radioLabel", msTypeRadioChoice));
        add(form);
    }

    private static class MsTypeRadioChoices implements IChoiceRenderer<Object> {

        public Object getDisplayValue(Object object) {
            KeyValuePair gender = (KeyValuePair) object;
            return gender.getValue();
        }

        public String getIdValue(Object object, int index) {
            return object.toString();
        }
    }

    private class KeyValuePair {

        private SvieMembershipType key;
        private String value;

        public KeyValuePair(SvieMembershipType key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public SvieMembershipType getKey() {
            return key;
        }

        @Override
        public String toString() {
            return this.key.name();
        }
    }
}

