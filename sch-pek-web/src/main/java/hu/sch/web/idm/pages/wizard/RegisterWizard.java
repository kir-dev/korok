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
package hu.sch.web.idm.pages.wizard;

import hu.sch.domain.RegisteringPerson;
import hu.sch.domain.util.PatternHolder;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.RegistrationManagerLocal;
import hu.sch.services.exceptions.InvalidNewbieStateException;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.services.exceptions.UserAlreadyExistsException;
import hu.sch.web.idm.pages.RegistrationFinishedPage;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.wizard.IWizardModel;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardModel;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardStep;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * Regisztrációs varázsló.
 * Utókornak: sorry a sok belső osztályért, de így a legegyszerűbb megoldani, 
 * hogy a formok ne veszítsenek el adatokat, peace ;)
 *
 * @author aldaris
 * @author balo
 */
public class RegisterWizard extends Wizard {

    //TODO: log4j konfiggal külön fájlba logolni!!!
    private static final Logger logger = Logger.getLogger(RegisterWizard.class);
    //
    @EJB(name = "RegistrationManager")
    RegistrationManagerLocal registrationManager;
    //
    @EJB(name = "LdapManagerBean")
    LdapManagerLocal ldapManager;
    //
    private RegisteringPerson person = new RegisteringPerson();
    private String newPass; //ezek a mezők használva vannak a CPM által
    private String newPass2;
    private RegistrationMode regMode;

    public RegisterWizard(String id) {
        super(id, false);

        setDefaultModel(new CompoundPropertyModel<RegisterWizard>(this));

        IWizardModel model = new DynamicWizardModel(new RegistrationModeSelectStep());
        init(model);

        //prevent duplicated feedback messages, PekPage already contains a feedbackpanel
        getForm().replace(new WebMarkupContainer(FEEDBACK_ID));
    }

    @Override
    public void onCancel() {
        super.onCancel();
        regMode = null;
        person = new RegisteringPerson();
        newPass = newPass2 = null;
        getWizardModel().reset();
    }

    @Override
    public void onFinish() {
        super.onFinish();

        try {
//            registrationManager.reg(person);
            logger.warn("registration.onFinish()");
        } catch (RuntimeException re) {
            getSession().error("A regisztráció közben hiba lépett fel!");
            throw new RestartResponseException(RegistrationFinishedPage.class);
        }
        getSession().info("Sikeres regisztráció!");
        setResponsePage(RegistrationFinishedPage.class);
    }

    private class RegistrationModeSelectStep extends DynamicWizardStep {

        public RegistrationModeSelectStep() {
            super(null, new StringResourceModel("reg.modeselect.title", null), new StringResourceModel("reg.modeselect.help", null));
            final RadioGroup<RegistrationMode> radioGroup = new RadioGroup<RegistrationMode>("regMode");
            ListView<RegistrationMode> lv = new ListView<RegistrationMode>("choiceList", new RegistrationModeListModel()) {
                @Override
                protected void populateItem(ListItem<RegistrationMode> item) {
                    item.add(new Radio("radio", item.getModel()));
                    item.add(new Label("name", new StringResourceModel(item.getModelObject().toString(), null)));
                }
            };
            radioGroup.add(lv);
            radioGroup.setRequired(true);
            add(radioGroup);
        }

        @Override
        public boolean isLastStep() {
            return false;
        }

        @Override
        public IDynamicWizardStep next() {
            switch (regMode) {
                case ACTIVE_WITH_NEPTUN_CODE:
                    person.setNewbie(false);
                    return new NeptunLoginStep(this);
                case NEWBIE_WITH_NEPTUN_CODE:
                    person.setNewbie(true);
                    return new NeptunLoginStep(this);
                case NEWBIE_WITH_OM_CODE:
                    person.setNewbie(true);
                    return new NeptunLoginStep(this);
                default:
                    return new NeptunLoginStep(this);
            }
        }
    }

    private class NeptunLoginStep extends DynamicWizardStep {

        public NeptunLoginStep(IDynamicWizardStep previousStep) {
            super(previousStep, new StringResourceModel("reg.neptun.title", null),
                    new StringResourceModel("reg.neptun.help", null));

            final RequiredTextField<String> neptun = new RequiredTextField<String>("person.neptun");
            neptun.add(StringValidator.exactLength(6));
            add(neptun);
            final DateTextField dob = new DateTextField("person.dateOfBirth", "yyyy.MM.dd.");
            dob.setRequired(true);
            add(dob);

            add(new IFormValidator() {
                @Override
                public FormComponent<?>[] getDependentFormComponents() {
                    return new FormComponent<?>[]{neptun, dob};
                }

                @Override
                public void validate(Form<?> form) {
                    try {
                        person.setDateOfBirth(dob.getConvertedInput());
                        person.setNeptun(neptun.getConvertedInput().toUpperCase());
                        registrationManager.canPersonRegisterWithNeptun(person);
                    } catch (PersonNotFoundException | InvalidNewbieStateException ex) {
                        error(new StringResourceModel(ex.getMessage(), getForm(), null).getString());
                    } catch (UserAlreadyExistsException ex) {
                        error(new StringResourceModel(ex.getMessage(), getForm(),
                                null, new Object[]{ex.getUid()}).getString());
                    }
                }
            });
        }

        @Override
        public boolean isLastStep() {
            return false;
        }

        @Override
        public IDynamicWizardStep next() {
            return new NeptunInfoStep(this);
        }
    }

    private class NeptunInfoStep extends DynamicWizardStep {

        public NeptunInfoStep(IDynamicWizardStep previousStep) {
            super(previousStep, new StringResourceModel("reg.neptuninfo.title", null),
                    new StringResourceModel("reg.neptuninfo.help", null));

            RequiredTextField<String> mail = new RequiredTextField<String>("person.mail");
            mail.add(EmailAddressValidator.getInstance());
            add(mail);
            RequiredTextField<String> sn = new RequiredTextField<String>("person.lastName");
            sn.add(new PatternValidator(PatternHolder.NAME_PATTERN));
            add(sn);
            RequiredTextField<String> givenName = new RequiredTextField<String>("person.firstName");
            givenName.add(new PatternValidator(PatternHolder.NAME_PATTERN));
            add(givenName);
        }

        @Override
        public boolean isLastStep() {
            return false;
        }

        @Override
        public IDynamicWizardStep next() {
            return new NewAccountStep(this);
        }
    }

    private class NewAccountStep extends DynamicWizardStep {

        public NewAccountStep(DynamicWizardStep previousStep) {
            super(previousStep, new StringResourceModel("reg.new.user.title", null),
                    new StringResourceModel("reg.new.user.help", null));

            final RequiredTextField<String> uidField = new RequiredTextField<String>("person.uid");
            uidField.add(new PatternValidator(PatternHolder.UID_PATTERN));
            uidField.add(StringValidator.lengthBetween(2, 10));
            uidField.add(new IValidator<String>() {
                @Override
                public void validate(IValidatable<String> validatable) {
                    String uid = validatable.getValue();
                    try {
                        ldapManager.getPersonByUid(uid);
                        validatable.error(new ValidationError().addMessageKey("reg.err.existing.user"));
                    } catch (PersonNotFoundException pnfe) {
                        //nem találtuk meg a felhasználót, ez most pont jó :)
                    }
                }
            });
            add(uidField);
            PasswordTextField pwdTF = new PasswordTextField("newPass");
            pwdTF.add(new StringValidator.MinimumLengthValidator(6));
            PasswordTextField pwdTF2 = new PasswordTextField("newPass2");
            add(pwdTF, pwdTF2);
            add(new EqualPasswordInputValidator(pwdTF, pwdTF2));
        }

        @Override
        public boolean isLastStep() {
            return true;
        }

        @Override
        public IDynamicWizardStep next() {
            return null;
        }
    }
}
