/**
 * Copyright (c) 2009, Peter Major
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
package hu.sch.web.profile.pages.edit;

import hu.sch.web.components.ImageResizer;
import hu.sch.domain.profile.IMAccount;
import hu.sch.domain.profile.IMProtocol;
import hu.sch.domain.profile.Person;
import hu.sch.services.LdapManagerLocal;
import hu.sch.web.components.ImageResource;
import hu.sch.web.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.components.ValidationStyleBehavior;
import hu.sch.web.components.customlinks.AttributeAjaxFallbackLink;
import hu.sch.util.PatternHolder;
import hu.sch.web.profile.pages.show.ShowPersonPage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.apache.wicket.validation.validator.UrlValidator;

/**
 *
 * @author konvergal
 */
public class PersonForm extends Form<Person> {

    @EJB(name = "LdapManagerBean")
    private LdapManagerLocal ldapManager;
    private static Logger log = Logger.getLogger(PersonForm.class);
    private final Person person;
    private Date dob;
    private FileUpload upload;
    private final RefreshingView<IMAccount> refreshView;

    public PersonForm(String componentName, final Person person) {
        super(componentName);
        this.person = person;
        setModel(new CompoundPropertyModel<Person>(person));
        setMultiPart(true);

        createNameFields();
        createGenderField();
        createAdditionalFields();
        createSvieFields();
        createDormitoryFields();

        //Ezt muszáj a konstruktorban csinálni a final kulcsszó miatt.
        final WebMarkupContainer rowPanel = new WebMarkupContainer("rowPanel");
        final IModel<List<IMAccount>> model = new PropertyModel<List<IMAccount>>(person, "IMAccounts");
        refreshView = new RefreshingView<IMAccount>("ims", model) {

            @Override
            protected Iterator<IModel<IMAccount>> getItemModels() {
                return new ModelIteratorAdapter<IMAccount>(model.getObject().iterator()) {

                    @Override
                    protected IModel<IMAccount> model(IMAccount object) {
                        return new Model<IMAccount>(object);
                    }
                };
            }

            @Override
            protected void populateItem(final Item<IMAccount> item) {
                final IMAccount acc = item.getModelObject();
                final List<IMAccount> currObj = (List<IMAccount>) getDefaultModelObject();
                item.add(new DropDownChoice("imProtocol",
                        new PropertyModel(acc, "protocol"),
                        Arrays.asList(IMProtocol.values())));
                item.add(new TextField("imPresenceID",
                        new PropertyModel(acc, "presenceID")));
                item.add(new AjaxFallbackButton("imRemove", PersonForm.this) {

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        currObj.remove(acc);
                        if (target != null) {
                            target.addComponent(rowPanel);
                        }
                    }
                }.setDefaultFormProcessing(false));
            }
        }.setItemReuseStrategy(new ReuseIfModelsEqualStrategy());
        rowPanel.add(refreshView);

        rowPanel.add(new AjaxFallbackButton("imAdd", PersonForm.this) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ((List<IMAccount>) refreshView.getDefaultModelObject()).add(new IMAccount(IMProtocol.icq, ""));
                if (target != null) {
                    target.addComponent(rowPanel);
                }
                for (IMAccount iMAccount : ((List<IMAccount>) refreshView.getDefaultModelObject())) {
                    System.out.println(iMAccount.toString());
                }
            }
        }.setDefaultFormProcessing(false));

        rowPanel.setOutputMarkupId(true);
        add(rowPanel);

        createPhotoField();

        add(new Label("neptunLabel", new PropertyModel(person, "neptun")));

        initAjaxPrivateLinks();
        onInit();

        //nem szabad használni a Form#onSubmit() függvényt, mindenképp szükség
        //van erre a gombra, különben az IM listás Ajax elromlik! A setResponsePage
        //true flag miatt készül ki valamiért.
        //Bővebben: http://osdir.com/ml/users-wicket.apache.org/2009-08/msg00836.html
        Button submitButton = new Button("submitButton") {

            @Override
            public void onSubmit() {
                if (upload != null) {
                    ImageResizer imageResizer = null;
                    try {
                        imageResizer = new ImageResizer(upload);
                    } catch (ImageResizer.NotValidImageException e) {
                        error("A fotó formátuma nem megfelelő! Megfelelő formátumok: jpeg, png, gif.");
                    } catch (Exception e) {
                        error("Hiba történt a fotó feldolgozása közben!");
                    }

                    if (imageResizer != null) {
                        try {
                            imageResizer.setMaxSize(320);
                            imageResizer.resizeImage();
                            person.setPhoto(imageResizer.getByteArray());
                        } catch (Exception e) {
                            error("Hiba történt a fotó feldolgozása közben!");
                        }
                    }
                }

                if (!hasError()) {
                    if (dob != null) {
                        person.setDateOfBirth(new SimpleDateFormat("yyyyMMdd").format(dob));
                    }

                    ldapManager.update(person);
                    getSession().info("Sikeres adatmódosítás. :)");
                    setResponsePage(ShowPersonPage.class);
                }

            }
        };
        add(submitButton);
        setDefaultButton(submitButton);
    }

    private void createNameFields() {
        RequiredTextField<String> lastNameTF = new RequiredTextField<String>("lastName");
        lastNameTF.add(StringValidator.lengthBetween(2, 40));
        lastNameTF.add(new ValidationStyleBehavior());
        add(lastNameTF);
        lastNameTF.setLabel(new Model<String>("Vezetéknév *"));
        add(new ValidationSimpleFormComponentLabel("lastNameLabel", lastNameTF));

        RequiredTextField<String> firstNameTF = new RequiredTextField<String>("firstName");
        firstNameTF.add(StringValidator.lengthBetween(2, 40));
        firstNameTF.add(new ValidationStyleBehavior());
        add(firstNameTF);
        firstNameTF.setLabel(new Model<String>("Keresztnév *"));
        add(new ValidationSimpleFormComponentLabel("firstNameLabel", firstNameTF));

        TextField<String> nickNameTF = new TextField<String>("nickName");
        nickNameTF.add(StringValidator.lengthBetween(2, 40));
        nickNameTF.add(new ValidationStyleBehavior());
        add(nickNameTF);
        nickNameTF.setLabel(new Model<String>("Becenév"));
        add(new ValidationSimpleFormComponentLabel("nickNameLabel", nickNameTF));
    }

    private void createGenderField() {
        IModel<List<KeyValuePairInForm>> genders = new LoadableDetachableModel<List<KeyValuePairInForm>>() {

            @Override
            public List<KeyValuePairInForm> load() {
                List<KeyValuePairInForm> l = new ArrayList<KeyValuePairInForm>();
                l.add(new KeyValuePairInForm("2", "Nő"));
                l.add(new KeyValuePairInForm("1", "Férfi"));
                return l;
            }
        };
        RadioChoice<KeyValuePairInForm> genderRadioChoice = new RadioChoice<KeyValuePairInForm>("gender", genders);
        genderRadioChoice.setChoiceRenderer(new GenderRadioChoices());
        add(genderRadioChoice);
        genderRadioChoice.setLabel(new Model<String>("Nem"));
        add(new SimpleFormComponentLabel("genderLabel", genderRadioChoice));
    }

    private void createAdditionalFields() {
        if (person.getDateOfBirth() != null) {
            try {
                dob = new SimpleDateFormat("yyyyMMdd").parse(person.getDateOfBirth());
            } catch (ParseException ex) {
                log.warn("Error while parsing date");
            }
        }
        DateTextField dateTF = new DateTextField("dateOfBirth", new PropertyModel<Date>(this, "dob"), new StyleDateConverter("S-", true)) {

            @Override
            public Locale getLocale() {
                return new Locale("hu");
            }
        };

        dateTF.add(new ValidationStyleBehavior());
        dateTF.setLabel(new Model<String>("Születési dátum"));
        add(new ValidationSimpleFormComponentLabel("dateOfBirthLabel", dateTF));
        add(dateTF);

        TextField<String> homePostalAddressTF = new TextField<String>("homePostalAddress");
        add(homePostalAddressTF);
        homePostalAddressTF.setLabel(new Model<String>("Cím"));
        add(new SimpleFormComponentLabel("homePostalAddressLabel", homePostalAddressTF));

        RequiredTextField<String> mailTF = new RequiredTextField<String>("mail");
        mailTF.add(EmailAddressValidator.getInstance());
        mailTF.add(new ValidationStyleBehavior());
        add(mailTF);
        mailTF.setLabel(new Model<String>("E-mail *"));
        add(new ValidationSimpleFormComponentLabel("mailLabel", mailTF));

        TextField<String> mobileTF = new TextField<String>("mobile");
        add(mobileTF);
        mobileTF.setLabel(new Model<String>("Mobil"));
        mobileTF.add(new PatternValidator(PatternHolder.PHONE_NUMBER_PATTERN));
        add(new ValidationSimpleFormComponentLabel("mobileLabel", mobileTF));

        TextField<String> homePhoneTF = new TextField<String>("homePhone");
        add(homePhoneTF);
        homePhoneTF.setLabel(new Model<String>("Vezetékes"));
        homePhoneTF.add(new PatternValidator(PatternHolder.PHONE_NUMBER_PATTERN));
        add(new ValidationSimpleFormComponentLabel("homePhoneLabel", homePhoneTF));

        TextField<String> webpageTF = new TextField<String>("webpage") {

            @Override
            public String[] getInputAsArray() {
                //lásd: http://www.mail-archive.com/users@wicket.apache.org/msg29215.html
                String[] inputArray = super.getInputAsArray();
                if (inputArray != null && inputArray.length != 0
                        && inputArray[0] != null) {
                    String value = inputArray[0];
                    if (!value.startsWith("http") && !value.isEmpty()) {
                        value = "http://" + value;
                        inputArray[0] = value;
                    }
                }
                return inputArray;
            }
        };

        webpageTF.add(new UrlValidator());
        webpageTF.add(new ValidationStyleBehavior());
        add(webpageTF);
        webpageTF.setLabel(new Model<String>("Weboldal"));
        add(new ValidationSimpleFormComponentLabel("webpageLabel", webpageTF));
    }

    private void createSvieFields() {
        TextField<String> mothersNameTF = new TextField<String>("mothersName");
        mothersNameTF.add(new PatternValidator(PatternHolder.MOTHER_NAME_PATTERN));
        mothersNameTF.add(new ValidationStyleBehavior());
        add(mothersNameTF);
        mothersNameTF.setLabel(new Model<String>("Anyja neve"));
        add(new ValidationSimpleFormComponentLabel("mothersNameLabel", mothersNameTF));

        TextField<String> estGradTF = new TextField<String>("estimatedGraduationYear");
        estGradTF.add(new PatternValidator(PatternHolder.GRADUATION_YEAR_PATTERN));
        estGradTF.add(new ValidationStyleBehavior());
        add(estGradTF);
        estGradTF.setLabel(new Model<String>("Egyetem várható befejezési ideje"));
        add(new ValidationSimpleFormComponentLabel("estGradLabel", estGradTF));
    }

    private void createDormitoryFields() {
        TextField<String> rNumberTF = new TextField<String>("rNumber");
        add(rNumberTF);
        rNumberTF.setLabel(new Model<String>("Szobaszám"));
        add(new SimpleFormComponentLabel("rNumberLabel", rNumberTF));
        List<String> dormitories =
                Arrays.asList(new String[]{"Schönherz", "Tétény", "Kármán", "Vásárhelyi"});
        DropDownChoice<String> dormitory = new DropDownChoice<String>("dormitory", dormitories);
        dormitory.setNullValid(true);
        add(dormitory);
        dormitory.setLabel(new Model<String>("Kollégium"));
        add(new SimpleFormComponentLabel("dormitoryLabel", dormitory));
    }

    private void createPhotoField() {
        final NonCachingImage photo = new NonCachingImage("photo", new AbstractReadOnlyModel<ImageResource>() {

            @Override
            public ImageResource getObject() {
                // TODO Auto-generated method stub
                return new ImageResource(person.getPhoto(), "png");
            }
        });
        photo.setOutputMarkupId(true);
        add(photo);

        AjaxFallbackLink photoRemoveLink = new AjaxFallbackLink("photoRemoveLink") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                person.setPhoto(null);
                ldapManager.update(person);

                setVisible(false);
                photo.setVisible(false);
                target.addComponent(this);
                target.addComponent(photo);
            }
        };
        photoRemoveLink.setOutputMarkupId(true);
        add(photoRemoveLink);

        if (person.getPhoto() == null) {
            photo.setVisible(false);
            photoRemoveLink.setVisible(false);
        }

        add(new FileUploadField("fileInput", new PropertyModel<FileUpload>(this, "upload")));
    }

    public void initAjaxPrivateLinks() {
        AttributeAjaxFallbackLink.setPerson(person);
        add(new AttributeAjaxFallbackLink("mailAttributeLink", "mailAttributeImg", "mail"));
        add(new AttributeAjaxFallbackLink("mobileAttributeLink", "mobileAttributeImg", "mobile"));
        add(new AttributeAjaxFallbackLink("homePhoneAttributeLink", "homePhoneAttributeImg", "homePhone"));
        add(new AttributeAjaxFallbackLink("roomNumberAttributeLink", "roomNumberAttributeImg", "roomNumber"));
        add(new AttributeAjaxFallbackLink("homePostalAddressAttributeLink", "homePostalAddressAttributeImg", "homePostalAddress"));
        add(new AttributeAjaxFallbackLink("webpageAttributeLink", "webpageAttributeImg", "labeledURI"));
        add(new AttributeAjaxFallbackLink("dateOfBirthAttributeLink", "dateOfBirthAttributeImg", "schacDateOfBirth"));
//            add(new AttributeAjaxFallbackLink("neptunAttributeLink", "neptunAttributeImg", "schacPersonalUniqueCode"));
    }

    protected void onInit() {
    }

    private static class GenderRadioChoices implements IChoiceRenderer<Object> {

        public Object getDisplayValue(Object object) {
            KeyValuePairInForm gender = (KeyValuePairInForm) object;
            return gender.getValue();
        }

        public String getIdValue(Object object, int index) {
            return object.toString();
        }
    }

    public class KeyValuePairInForm {

        private String id;
        private String value;

        public KeyValuePairInForm(String id, String value) {
            this.id = id;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return this.id;
        }
    }
}
