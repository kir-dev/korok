/**
 * Copyright (c) 2008-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package hu.sch.web.kp.svie;

import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.User;
import hu.sch.domain.profile.Person;
import hu.sch.services.SvieManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.wicket.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.wicket.behaviors.ValidationStyleBehavior;
import hu.sch.web.wicket.components.customlinks.AttributeAjaxFallbackLink;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.kp.KorokPage;
import hu.sch.domain.util.PatternHolder;
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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 *
 * @author aldaris
 */
public final class SvieRegistration extends KorokPage {

    @EJB(name = "SvieManagerBean")
    SvieManagerLocal svieManager;
    private Person person = null;
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
        try {
            person = ldapManager.getPersonByVirId(user.getId().toString());
        } catch (PersonNotFoundException pnfe) {
            getSession().error("A felhasználó nem található.");
            throw new RestartResponseException(ShowUser.class);
        }

        setHeaderLabelText("SVIE Regisztráció");

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

        RequiredTextField<String> homePostalAddressTF = new RequiredTextField<String>("homePostalAddress");
        homePostalAddressTF.add(new ValidationStyleBehavior());
        homePostalAddressTF.setLabel(new Model<String>("Cím *"));
        form.add(homePostalAddressTF);
        form.add(new ValidationSimpleFormComponentLabel("homePostalAddressLabel", homePostalAddressTF));

        AttributeAjaxFallbackLink.setPerson(person);
        form.add(new AttributeAjaxFallbackLink("homePostalAddressAttributeLink", "homePostalAddressAttributeImg", "homePostalAddress"));

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
