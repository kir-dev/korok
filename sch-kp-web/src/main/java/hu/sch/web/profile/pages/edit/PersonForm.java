/*
 *  Copyright 2008 konvergal.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
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
import hu.sch.web.profile.pages.show.ShowPersonPage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.apache.wicket.validation.validator.UrlValidator;

/**
 *
 * @author konvergal
 */
public class PersonForm extends Form<Person> {

    @EJB(name = "LdapManagerBean")
    private LdapManagerLocal ldapManager;
    public Person person;
    public Date dob;
    public FileUploadField fileUploadField;

    private static class genderRadioChoiceRenderer implements IChoiceRenderer<Object> {

        public Object getDisplayValue(Object object) {
            KeyValuePairInForm gender = (KeyValuePairInForm)object;
            return gender.getValue();
        }

        public String getIdValue(Object object, int index) {
            return object.toString();
        }
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public PersonForm(String componentName, Person person) {
        super(componentName);
        setPerson(person);
        setModel(new CompoundPropertyModel<Person>(person));

        initFormComponents();
    }

    public PersonForm(String componentName) {
        super(componentName);
        setModel(new CompoundPropertyModel<Person>(person));

        initFormComponents();
    }

    public void initFormComponents() {
        setMultiPart(true);

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

        RequiredTextField<String> mailTF = new RequiredTextField<String>("mail");
        mailTF.add(EmailAddressValidator.getInstance());
        mailTF.add(new ValidationStyleBehavior());
        add(mailTF);
        mailTF.setLabel(new Model<String>("E-mail *"));
        add(new ValidationSimpleFormComponentLabel("mailLabel", mailTF));

        TextField<String> mobileTF = new TextField<String>("mobile");
        add(mobileTF);
        mobileTF.setLabel(new Model<String>("Mobil"));
        add(new SimpleFormComponentLabel("mobileLabel", mobileTF));

        TextField<String> homePhoneTF = new TextField<String>("homePhone");
        add(homePhoneTF);
        homePhoneTF.setLabel(new Model<String>("Vezetékes"));
        add(new SimpleFormComponentLabel("homePhoneLabel", homePhoneTF));

        TextField<String> homePostalAddressTF = new TextField<String>("homePostalAddress");
        add(homePostalAddressTF);
        homePostalAddressTF.setLabel(new Model<String>("Cím"));
        add(new SimpleFormComponentLabel("homePostalAddressLabel", homePostalAddressTF));

        TextField<String> rNumberTF = new TextField<String>("rNumber");
        add(rNumberTF);
        rNumberTF.setLabel(new Model<String>("Szobaszám"));
        add(new SimpleFormComponentLabel("rNumberLabel", rNumberTF));

        TextField<String> webpageTF = new TextField<String>("webpage");
        webpageTF.add(new UrlValidator());
        webpageTF.add(new ValidationStyleBehavior());
        add(webpageTF);
        webpageTF.setLabel(new Model<String>("Weboldal"));
        add(new ValidationSimpleFormComponentLabel("webpageLabel", webpageTF));

        List<String> dormitories =
                Arrays.asList(new String[]{"Schönherz", "Tétény", "Kármán", "Vásárhelyi"});
        DropDownChoice<String> dormitory = new DropDownChoice<String>("dormitory", dormitories);
        dormitory.setNullValid(true);
        add(dormitory);
        dormitory.setLabel(new Model<String>("Kollégium"));
        add(new SimpleFormComponentLabel("dormitoryLabel", dormitory));


        if (person.getDateOfBirth() != null) {
            try {
                dob =
                        new SimpleDateFormat("yyyyMMdd").parse(person.getDateOfBirth());
            } catch (ParseException ex) {
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
        genderRadioChoice.setChoiceRenderer(new genderRadioChoiceRenderer());
        add(genderRadioChoice);
        genderRadioChoice.setLabel(new Model<String>("Nem"));
        add(new SimpleFormComponentLabel("genderLabel", genderRadioChoice));

        add(new ListView("ims", new PropertyModel(person, "IMAccounts")) {

            @Override
            protected void populateItem(ListItem item) {
                final IMAccount acc = (IMAccount) item.getModelObject();
                item.add(new DropDownChoice("imProtocol",
                        new PropertyModel(acc, "protocol"),
                        Arrays.asList(IMProtocol.values())));
                item.add(new TextField("imPresenceID",
                        new PropertyModel(acc, "presenceID")));
                item.add(new Button("imRemove") {

                    @Override
                    public void onSubmit() {
                        person.getIMAccounts().remove(acc);
                    }
                });
            }
        });
        add(new Button("imAdd") {

            @Override
            public void onSubmit() {
                person.getIMAccounts().add(new IMAccount(IMProtocol.icq, ""));
            }
        });

        add(new Label("neptunLabel", new PropertyModel(person, "neptun")));

        class AttributeAjaxFallbackLink extends AjaxFallbackLink {

            String privateAttr;
            boolean isPrivateAttr;
            Image img;

            public AttributeAjaxFallbackLink(String id) {
                super(id);
            }

            public AttributeAjaxFallbackLink(String linkId, String imgId, final String privateAttr) {
                this(linkId);
                this.privateAttr = privateAttr;
                isPrivateAttr = person.isPrivateAttribute(privateAttr);

                img = new Image(imgId);
                img.setOutputMarkupId(true);
                setImgModel();

                this.add(img);
            }

            public void setImgModel() {
                if (isPrivateAttr) {
                    img.setDefaultModel(new Model(new ResourceReference(AttributeAjaxFallbackLink.class, "images/private.gif")));
                } else {
                    img.setDefaultModel(new Model(new ResourceReference(AttributeAjaxFallbackLink.class, "images/public.gif")));
                }
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                person.inversePrivateAttribute(privateAttr);
                isPrivateAttr = !isPrivateAttr;

                setImgModel();
                target.addComponent(img);
            }

            /*                @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
            return new AjaxCallDecorator() {
            
            @Override
            public CharSequence decorateScript(CharSequence script) {
            return "inverseAttribute(this); " + script;
            }
            };
            }*/
        }


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

                this.setVisible(false);
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

        add(fileUploadField = new FileUploadField("fileInput", new PropertyModel<FileUpload>(person, "photo")));

        add(new AttributeAjaxFallbackLink("mailAttributeLink", "mailAttributeImg", "mail"));
        add(new AttributeAjaxFallbackLink("mobileAttributeLink", "mobileAttributeImg", "mobile"));
        add(new AttributeAjaxFallbackLink("homePhoneAttributeLink", "homePhoneAttributeImg", "homePhone"));
        add(new AttributeAjaxFallbackLink("roomNumberAttributeLink", "roomNumberAttributeImg", "roomNumber"));
        add(new AttributeAjaxFallbackLink("homePostalAddressAttributeLink", "homePostalAddressAttributeImg", "homePostalAddress"));
        add(new AttributeAjaxFallbackLink("webpageAttributeLink", "webpageAttributeImg", "labeledURI"));
        add(new AttributeAjaxFallbackLink("dateOfBirthAttributeLink", "dateOfBirthAttributeImg", "schacDateOfBirth"));
//            add(new AttributeAjaxFallbackLink("neptunAttributeLink", "neptunAttributeImg", "schacPersonalUniqueCode"));

        add(new Button("submit") {

            @Override
            public void onSubmit() {
                final FileUpload upload = fileUploadField.getFileUpload();
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
        });
    }
}
