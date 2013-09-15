package hu.sch.web.kp.svie;

import hu.sch.domain.enums.SvieMembershipType;
import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.services.SvieManagerLocal;
import hu.sch.web.wicket.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.wicket.behaviors.ValidationStyleBehavior;
import hu.sch.web.wicket.components.customlinks.AttributeAjaxFallbackLink;
import hu.sch.web.kp.KorokPage;
import hu.sch.util.PatternHolder;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 * TODO: testing!
 * 
 * @author aldaris
 */
public final class SvieRegistration extends KorokPage {

    @Inject
    SvieManagerLocal svieManager;
    private User user;
    private SvieMembershipType choosed;

    /**
     * Ezzel a konstruktorral egyszerre gyorsítjuk a kódot és megoldjuk, hogy az
     * oldal ne legyen könyvjelzőzhető
     * @param _user A felhasználó, aki szeretne SVIE-be regisztrálni
     */
    public SvieRegistration(final User _user) {
        createNavbarWithSupportId(34);
        this.user = _user;

        setHeaderLabelText("SVIE Regisztráció");

        Form<User> form = new Form<User>("registrationForm", new CompoundPropertyModel<>(user)) {

            @Override
            protected void onSubmit() {
                svieManager.applyToSvie(user, choosed);
                continueToOriginalDestination();
                setResponsePage(getApplication().getHomePage());
            }
        };
        form.setModel(new CompoundPropertyModel<User>(user));

        RequiredTextField<String> mothersNameTF = new RequiredTextField<String>("mothersName");
        mothersNameTF.add(new PatternValidator(PatternHolder.NAME_PATTERN));
        mothersNameTF.add(new ValidationStyleBehavior());
        mothersNameTF.setLabel(new Model<String>("Anyja neve *"));
        form.add(mothersNameTF);
        form.add(new ValidationSimpleFormComponentLabel("mothersNameLabel", mothersNameTF));

        RequiredTextField<String> estGradTF = new RequiredTextField<String>("estimatedGraduationYear");
        estGradTF.add(new PatternValidator(PatternHolder.GRADUATION_YEAR_PATTERN));
        estGradTF.add(new ValidationStyleBehavior());
        estGradTF.setLabel(new Model<String>("Egyetem várható befejezési ideje *"));
        form.add(estGradTF);
        form.add(new ValidationSimpleFormComponentLabel("estGradLabel", estGradTF));

        RequiredTextField<String> homePostalAddressTF = new RequiredTextField<String>("homeAddress");
        homePostalAddressTF.add(new ValidationStyleBehavior());
        homePostalAddressTF.setLabel(new Model<String>("Cím *"));
        form.add(homePostalAddressTF);
        form.add(new ValidationSimpleFormComponentLabel("homePostalAddressLabel", homePostalAddressTF));

        AttributeAjaxFallbackLink attrLink = new AttributeAjaxFallbackLink("homePostalAddressAttributeLink", "homePostalAddressAttributeImg", UserAttributeName.HOME_ADDRESS, user);
        attrLink.setUser(user);
        form.add(attrLink);

        final RadioGroup<SvieMembershipType> radioGroup = new RadioGroup<SvieMembershipType>("choices", new PropertyModel<SvieMembershipType>(this, "choosed"));
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
        radioGroup.setRequired(true);
        form.add(radioGroup);

        add(form);
    }
}
