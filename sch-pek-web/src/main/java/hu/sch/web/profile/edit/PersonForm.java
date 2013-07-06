package hu.sch.web.profile.edit;

import hu.sch.domain.profile.IMAccount;
import hu.sch.domain.profile.IMProtocol;
import hu.sch.domain.profile.Person;
import hu.sch.domain.util.ImageResizer;
import hu.sch.domain.util.PatternHolder;
import hu.sch.services.LdapManagerLocal;
import hu.sch.web.profile.show.ShowPersonPage;
import hu.sch.web.wicket.behaviors.ValidationStyleBehavior;
import hu.sch.web.wicket.components.ImageResource;
import hu.sch.web.wicket.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.wicket.components.customlinks.AttributeAjaxFallbackLink;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.*;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.apache.wicket.validation.validator.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author konvergal
 */
public class PersonForm extends Form<Person> {

    @EJB(name = "LdapManagerBean")
    private LdapManagerLocal ldapManager;
    private static final Logger logger = LoggerFactory.getLogger(PersonForm.class);
    private final Person person;
    private Date dob;
    private List<FileUpload> upload;
    private final RefreshingView<IMAccount> refreshView;
    private static final int NAMES_MIN_LENGTH = 2;
    private static final int NAMES_MAX_LENGTH = 40;

    public PersonForm(final String componentName, final Person person) {
        super(componentName);
        this.person = person;
        setModel(new CompoundPropertyModel<Person>(person));
        setMultiPart(true);

        createNameFields();
        createGenderField();
        createAdditionalFields();
//        createSvieFields();
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
                    protected void onError(AjaxRequestTarget target, Form<?> form) {
                    }

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        currObj.remove(acc);
                        if (target != null) {
                            target.add(rowPanel);
                        }
                    }
                }.setDefaultFormProcessing(false));
            }
        }.setItemReuseStrategy(new ReuseIfModelsEqualStrategy());
        rowPanel.add(refreshView);

        rowPanel.add(new AjaxFallbackButton("imAdd", PersonForm.this) {

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                ((List<IMAccount>) refreshView.getDefaultModelObject()).add(new IMAccount(IMProtocol.icq, ""));
                if (target != null) {
                    target.add(rowPanel);
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
        final Button submitButton = new Button("submitButton") {

            @Override
            public void onSubmit() {
                if (upload != null && !upload.isEmpty()) {
                    // formátum ellenőrzés
                    List<String> validImageContentTypes = Arrays.asList(new String[]{"image/jpeg", "image/png", "image/gif"});
                    FileUpload fu = upload.get(0);
                    if (!validImageContentTypes.contains(fu.getContentType())) {
                        logger.warn("Uploaded picture with unknown image format: " + fu.getContentType());
                        error("A fotó formátuma nem megfelelő! Megfelelő formátumok: jpeg, png, gif.");
                        return;
                    }

                    try {
                        ImageResizer imageResizer = new ImageResizer(fu.getBytes(), Person.IMAGE_MAX_SIZE);

                        if (imageResizer != null) {
                            imageResizer.resizeImage();
                            person.setPhoto(imageResizer.getByteArray());
                        }
                    } catch (IOException ioe) {
                        logger.error("IO error occured during image processing", ioe);
                        error("Hiba történt a fotó feldolgozása közben!");
                    }
                }

                if (!hasError()) {
                    if (dob != null) {
                        person.setDateOfBirth(dob);
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
        final RequiredTextField<String> lastNameTF = new RequiredTextField<String>("lastName");
        lastNameTF.add(StringValidator.lengthBetween(NAMES_MIN_LENGTH, NAMES_MAX_LENGTH));
        lastNameTF.add(new ValidationStyleBehavior());
        add(lastNameTF);
        lastNameTF.setLabel(new Model<String>("Vezetéknév *"));
        add(new ValidationSimpleFormComponentLabel("lastNameLabel", lastNameTF));

        final RequiredTextField<String> firstNameTF = new RequiredTextField<String>("firstName");
        firstNameTF.add(StringValidator.lengthBetween(NAMES_MIN_LENGTH, NAMES_MAX_LENGTH));
        firstNameTF.add(new ValidationStyleBehavior());
        add(firstNameTF);
        firstNameTF.setLabel(new Model<String>("Keresztnév *"));
        add(new ValidationSimpleFormComponentLabel("firstNameLabel", firstNameTF));

        final TextField<String> nickNameTF = new TextField<String>("nickName");
        nickNameTF.add(StringValidator.lengthBetween(NAMES_MIN_LENGTH, NAMES_MAX_LENGTH));
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
        dob = person.getDateOfBirth();
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

    protected void createSvieFields() {
        TextField<String> mothersNameTF = new TextField<String>("mothersName");
        mothersNameTF.add(new PatternValidator(PatternHolder.NAME_PATTERN));
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
                target.add(this);
                target.add(photo);
            }
        };
        photoRemoveLink.setOutputMarkupId(true);
        add(photoRemoveLink);

        if (person.getPhoto() == null) {
            photo.setVisible(false);
            photoRemoveLink.setVisible(false);
        }

        add(new FileUploadField("fileInput", new PropertyModel<List<FileUpload>>(this, "upload")));
    }

    private void initAjaxPrivateLinks() {
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

        @Override
        public Object getDisplayValue(Object object) {
            KeyValuePairInForm gender = (KeyValuePairInForm) object;
            return gender.getValue();
        }

        @Override
        public String getIdValue(Object object, int index) {
            return object.toString();
        }
    }

    public static class KeyValuePairInForm {

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
