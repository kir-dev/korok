/**
 * Copyright (c) 2009-2010, Peter Major
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

import hu.sch.domain.profile.Person;
import hu.sch.domain.util.PatternHolder;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.idm.pages.RegistrationFinishedPage;
import hu.sch.web.wicket.components.AjaxWizardButtonBar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.StringTokenizer;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.wizard.IWizardModel;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardModel;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardStep;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
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
 */
public class RegisterWizard extends Wizard {

    private static final String ATTRIBUTES_SQL = "SELECT * FROM users_full_dormitory WHERE usr_id=?";
    private static final String VIR_CHECK_SQL = "SELECT kirauth(?, md5(?))";
    private static final String NEPTUN_CHECK_SQL = "SELECT neptun, nev FROM neptun_list WHERE UPPER(neptun)=UPPER(?) AND szuldat=?";
    //TODO: log4j konfiggal külön fájlba logolni!!!
    private static final Logger logger = Logger.getLogger(RegisterWizard.class);
    //sima JDBC hogy ne kelljen csak emiatt felmappelni attribútumokat és 
    //foglalkozni velük
    @Resource(name = "jdbc/sch")
    private DataSource ds;
    @EJB(name = "LdapManagerBean")
    LdapManagerLocal ldapManager;
    private Person person = new Person();
    private Date dob;
    private String virUserName;
    private String virPass;
    private String neptun;
    private String newPass;
    private String newPass2;
    private RegistrationMode regMode;

    public RegisterWizard(String id) {
        super(id, false);

        setDefaultModel(new CompoundPropertyModel<RegisterWizard>(this));

        IWizardModel model = new DynamicWizardModel(new RegistrationModeSelectStep());
        init(model);
    }

    @Override
    public void onCancel() {
        super.onCancel();
        regMode = null;
        dob = null;
        person = new Person();
        virUserName = virPass = neptun = newPass = newPass2 = null;
        getWizardModel().reset();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        person.setStatus("Inactive");
        try {
            ldapManager.registerPerson(person, newPass);
        } catch (RuntimeException re) {
            getSession().error("A regisztráció közben hiba lépett fel!");
            throw new RestartResponseException(RegistrationFinishedPage.class);
        }
        getSession().info("Sikeres regisztráció!");
        setResponsePage(RegistrationFinishedPage.class);
    }

    //lásd AjaxWizardButtonBar JavaDoc
    @Override
    protected Component newButtonBar(String id) {
        return new AjaxWizardButtonBar(id, this);
    }

    private boolean checkExistingPerson() throws IllegalArgumentException {
        try {
            Person dummy = ldapManager.getPersonByNeptun(neptun);
            throw new IllegalArgumentException("Már rendelkezel felhasználóval (\"" + dummy.getUid()
                    + "\"), kérlek igényelj új jelszót a https://idp.sch.bme.hu/opensso/password oldalon!");
        } catch (PersonNotFoundException pnfe) {
        }

        return false;
    }

    private enum RegistrationMode {

        VIR_ACCOUNT("Meglévő VIR regisztráció"),
        NEPTUN_CODE("Aktív villanykari hallgató NEPTUN kód");
        private String name;

        private RegistrationMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private class RegistrationModeSelectStep extends DynamicWizardStep {

        public RegistrationModeSelectStep() {
            super(null, new StringResourceModel("reg.modeselect.title", null), new StringResourceModel("reg.modeselect.help", null));
            final RadioGroup<RegistrationMode> radioGroup = new RadioGroup<RegistrationMode>("regMode");
            ListView<RegistrationMode> lv = new ListView<RegistrationMode>("choiceList", Arrays.asList(RegistrationMode.values())) {

                @Override
                protected void populateItem(ListItem<RegistrationMode> item) {
                    item.add(new Radio("radio", item.getModel()));
                    item.add(new Label("name", item.getModelObject().toString()));
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
            if (regMode.equals(RegistrationMode.NEPTUN_CODE)) {
                return new NeptunLoginStep(this);
            } else {
                return new VirLoginStep(this);
            }
        }
    }

    private class VirLoginStep extends DynamicWizardStep {

        public VirLoginStep(IDynamicWizardStep previousStep) {
            super(previousStep, new StringResourceModel("reg.virlogin.title", null), new StringResourceModel("reg.virlogin.help", null));
            final TextField<String> userName = new RequiredTextField<String>("virUserName");
            userName.add(StringValidator.maximumLength(80));
            final TextField<String> pass = new PasswordTextField("virPass");
            pass.setRequired(true);

            add(userName, pass);
            add(new IFormValidator() {

                @Override
                public FormComponent<?>[] getDependentFormComponents() {
                    return new FormComponent<?>[]{userName, pass};
                }

                @Override
                public void validate(Form<?> form) {
                    try {
                        if (!checkPwd(userName.getValue(), pass.getValue())) {
                            error("Hiba lépett fel a regisztráció közben");
                        }
                    } catch (IllegalArgumentException iae) {
                        error(iae.getMessage());
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
            return new NewAccountStep(this);
        }

        private boolean checkPwd(String userName, String password) throws IllegalArgumentException {
            Connection conn = null;
            ResultSet results = null;
            PreparedStatement stmt = null;
            String virAuthString = null;

            try {
                conn = ds.getConnection();
                stmt = conn.prepareStatement(VIR_CHECK_SQL);
                stmt.setString(1, userName);
                stmt.setString(2, password);

                results = stmt.executeQuery();

                int index = 0;

                while (results.next()) {
                    index++;
                    if (index > 1) {
                        logger.error("Too many results from kirauth()");
                        throw new IllegalArgumentException("Hiba lépett fel a regisztráció közben");
                    }
                    virAuthString = results.getString(1);
                    if (virAuthString == null) {
                        logger.info("No results from kirauth()");
                        throw new IllegalArgumentException("Nincs ilyen felhasználó!");
                    }
                }
                if (index == 0) {
                    logger.warn("No results from kirauth()");
                    throw new IllegalArgumentException("Nincs ilyen felhasználó!");
                }

                // parse in the userid. its in the first field of response
                // "userid/name/room"
                StringTokenizer tok = new StringTokenizer(virAuthString);
                String userId = tok.nextToken("/");

                long usr_id = 0;
                usr_id = Long.parseLong(userId);

                if (usr_id <= 0) {
                    logger.error("Usr_id parse error in: " + virAuthString);
                    throw new IllegalArgumentException("Hiba lépett fel a regisztráció közben");
                }

                // select user attributes
                stmt = conn.prepareStatement(ATTRIBUTES_SQL);

                stmt.setLong(1, usr_id);
                results = stmt.executeQuery();

                if (!results.next()) {
                    logger.error("No record in users database for the user");
                    throw new IllegalArgumentException("Nincs ilyen felhasználó!");
                }

                // fill in transient user attributes
                person.setVirId(usr_id);
                neptun = results.getString("usr_neptun");

                //don't use the generated dummy _usrid neptun codes
                if (!neptun.startsWith("_")) {
                    person.setNeptun(neptun);
                }


                checkExistingPerson();
                person.setFullName(results.getString("usr_name"));
                person.setNickName(results.getString("usr_nickname"));
                person.setFirstName(results.getString("usr_firstname"));
                person.setLastName(results.getString("usr_lastname"));
                person.setMail(results.getString("usr_email"));

                String isGirl = results.getString("usr_is_a_girl");
                if (isGirl.equals("t")) {
                    person.setGender("2");
                } else if (isGirl.equals("f")) {
                    person.setGender("1");
                }

                person.setStudentStatus(results.getString("usr_status"));

                return true;
            } catch (SQLException sqle) {
                logger.error("Error occured during trying to registration process", sqle);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Exception ex) {
                        logger.error("Unable to close database connection!", ex);
                    }
                }
            }
            return false;
        }
    }

    private class NeptunLoginStep extends DynamicWizardStep {

        public NeptunLoginStep(IDynamicWizardStep previousStep) {
            super(previousStep, new StringResourceModel("reg.neptun.title", null), new StringResourceModel("reg.neptun.help", null));
            final RequiredTextField<String> neptun = new RequiredTextField<String>("neptun");
            neptun.add(StringValidator.exactLength(6));
            add(neptun);
            final DateTextField dob = new DateTextField("dob", "yyyy.MM.dd.");
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
                        if (!checkNeptun(neptun.getValue().toUpperCase(), dob.getConvertedInput())) {
                            error("Érvénytelen Neptun kód-születésnap pár!");
                        }
                    } catch (IllegalArgumentException iae) {
                        error(iae.getMessage());
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

        private boolean checkNeptun(String neptun, Date birthDate) throws IllegalArgumentException {
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet results = null;

            try {
                conn = ds.getConnection();
                stmt = conn.prepareStatement(NEPTUN_CHECK_SQL);
                stmt.setString(1, neptun);
                stmt.setDate(2, new java.sql.Date(birthDate.getTime()));
                results = stmt.executeQuery();
                if (results.next()) {
                    person.setNeptun(neptun);
                    checkExistingPerson();
                    person.setStudentStatus("akt");
                    person.setDateOfBirth(new SimpleDateFormat("yyyyMMdd").format(birthDate));

                    return true;
                } else {
                    return false;
                }
            } catch (SQLException ex) {
                logger.error("SQLException during neptun check", ex);
                throw new IllegalArgumentException("Hiba lépett fel a regisztráció közben!");
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Exception dbe) {
                        logger.warn("Fail to close database:", dbe);
                        throw new IllegalArgumentException("Hiba lépett fel a regisztráció közben!");
                    }
                }
            }
        }
    }

    private class NeptunInfoStep extends DynamicWizardStep {

        public NeptunInfoStep(IDynamicWizardStep previousStep) {
            super(previousStep, new StringResourceModel("reg.neptuninfo.title", null), new StringResourceModel("reg.neptuninfo.help", null));
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
            person.setFullName(person.getLastName() + " " + person.getFirstName());
            return new NewAccountStep(this);
        }
    }

    private class NewAccountStep extends DynamicWizardStep {

        public NewAccountStep(DynamicWizardStep previousStep) {
            super(previousStep, "Új felhasználói név megadása", "Kérlek add meg az új felhasználói nevedet, valamint a hozzá tartozó jelszavad. A jelszó legalább 6 karakter hosszú kell legyen!");
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