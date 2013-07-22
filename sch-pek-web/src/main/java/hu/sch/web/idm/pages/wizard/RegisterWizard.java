package hu.sch.web.idm.pages.wizard;

import hu.sch.domain.user.RegisteringUser;
import hu.sch.domain.util.PatternHolder;
import hu.sch.services.RegistrationManagerLocal;
import hu.sch.services.exceptions.CreateFailedException;
import hu.sch.services.exceptions.InvalidNewbieStateException;
import hu.sch.services.exceptions.UserAlreadyExistsException;
import hu.sch.services.exceptions.UserNotFoundException;
import hu.sch.web.idm.pages.RegistrationFinishedPage;
import javax.ejb.EJB;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Regisztrációs varázsló.
 * Utókornak: sorry a sok belső osztályért, de így a legegyszerűbb megoldani,
 * hogy a formok ne veszítsenek el adatokat, peace ;)
 *
 * @author aldaris
 * @author balo
 */
public class RegisterWizard extends Wizard {

    private static final Logger logger = LoggerFactory.getLogger(RegisterWizard.class);
    //
    @EJB(name = "RegistrationManager")
    RegistrationManagerLocal registrationManager;
    //
    private RegisteringUser person = new RegisteringUser();
    private String newPass; //ezek a mezők használva vannak a CPM által
    private String newPass2;
    private RegistrationMode regMode;

    public RegisterWizard(String id) {
        super(id, false);

        setDefaultModel(new CompoundPropertyModel<>(this));

        IWizardModel model = new DynamicWizardModel(new RegistrationModeSelectStep());
        init(model);

        //prevent duplicated feedback messages, PekPage already contains a feedbackpanel
        getForm().replace(new WebMarkupContainer(FEEDBACK_ID));
    }

    @Override
    public void onCancel() {
        super.onCancel();
        regMode = null;
        person = new RegisteringUser();
        newPass = newPass2 = null;
        getWizardModel().reset();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        try {
            registrationManager.doRegistration(person, newPass);
        } catch (UserNotFoundException | CreateFailedException ex) {
            getSession().error("A regisztráció közben hiba lépett fel!");
            logger.warn("Exception on finishing registration, RegisteringPerson=" + person.toString(), ex);
            throw new RestartResponseException(RegistrationFinishedPage.class);
        }
        getSession().info("Sikeres regisztráció!");
        setResponsePage(RegistrationFinishedPage.class);
    }

    private class RegistrationModeSelectStep extends DynamicWizardStep {

        public RegistrationModeSelectStep() {
            super(null, new StringResourceModel("reg.modeselect.title", null), new StringResourceModel("reg.modeselect.help", null));
            final RadioGroup<RegistrationMode> radioGroup = new RadioGroup<>("regMode");
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
                    return new EducationIdLoginStep(this);
                default:
                    return new NeptunLoginStep(this);
            }
        }
    }

    private class NeptunLoginStep extends DynamicWizardStep {

        public NeptunLoginStep(IDynamicWizardStep previousStep) {
            super(previousStep, new StringResourceModel("reg.neptun.title", null),
                    new StringResourceModel("reg.neptun.help", null));

            final RequiredTextField<String> neptun = new RequiredTextField<>("person.neptun");
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
                        registrationManager.canUserRegisterWithNeptun(person);
                        //
                    } catch (UserNotFoundException | InvalidNewbieStateException ex) {
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
            return new PersonalInfoStep(this);
        }
    }

    private class EducationIdLoginStep extends DynamicWizardStep {

        public EducationIdLoginStep(IDynamicWizardStep previousStep) {
            super(previousStep, new StringResourceModel("reg.educationId.title", null),
                    new StringResourceModel("reg.educationId.help", null));

            final RequiredTextField<String> educationId = new RequiredTextField<>("person.educationId");
            educationId.add(new PatternValidator(PatternHolder.EDUCATION_ID_PATTERN));
            add(educationId);
            final DateTextField dob = new DateTextField("person.dateOfBirth", "yyyy.MM.dd.");
            dob.setRequired(true);
            add(dob);

            add(new IFormValidator() {
                @Override
                public FormComponent<?>[] getDependentFormComponents() {
                    return new FormComponent<?>[]{educationId, dob};
                }

                @Override
                public void validate(Form<?> form) {
                    try {
                        person.setDateOfBirth(dob.getConvertedInput());
                        person.setEducationId(educationId.getConvertedInput());
                        registrationManager.canUserRegisterWithEducationId(person);
                        //
                    } catch (UserNotFoundException | InvalidNewbieStateException ex) {
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
            return new PersonalInfoStep(this);
        }
    }

    private class PersonalInfoStep extends DynamicWizardStep {

        public PersonalInfoStep(IDynamicWizardStep previousStep) {
            super(previousStep, new StringResourceModel("reg.personalinfo.title", null),
                    new StringResourceModel("reg.personalinfo.help", null));

            RequiredTextField<String> mail = new RequiredTextField<>("person.mail");
            mail.add(EmailAddressValidator.getInstance());
            add(mail);
            RequiredTextField<String> sn = new RequiredTextField<>("person.lastName");
            sn.add(new PatternValidator(PatternHolder.NAME_PATTERN));
            add(sn);
            RequiredTextField<String> givenName = new RequiredTextField<>("person.firstName");
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

            final RequiredTextField<String> uidField = new RequiredTextField<>("person.screenName");
            uidField.add(new PatternValidator(PatternHolder.UID_PATTERN));
            uidField.add(StringValidator.lengthBetween(2, 10));
            uidField.add(new IValidator<String>() {
                @Override
                public void validate(final IValidatable<String> validatable) {
                    final String uid = validatable.getValue();
                    if (registrationManager.isUidTaken(uid)) {
                        validatable.error(new ValidationError().addKey("reg.error.existing.user"));
                    }
                }
            });
            add(uidField);
            PasswordTextField pwdTF = new PasswordTextField("newPass");
            pwdTF.add(StringValidator.minimumLength(6));
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
