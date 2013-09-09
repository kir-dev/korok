package hu.sch.web.profile.edit;

import hu.sch.domain.user.Gender;
import hu.sch.domain.user.IMAccount;
import hu.sch.domain.user.IMProtocol;
import hu.sch.domain.user.ProfileImage;
import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.services.ImageRemoverService;
import hu.sch.services.UserManagerLocal;
import hu.sch.util.PatternHolder;
import hu.sch.services.exceptions.NotImplementedException;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.web.profile.show.ShowPersonPage;
import hu.sch.web.wicket.behaviors.ValidationStyleBehavior;
import hu.sch.web.wicket.components.ImageResource;
import hu.sch.web.wicket.components.ProfileImageResource;
import hu.sch.web.wicket.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.wicket.components.customlinks.AttributeAjaxFallbackLink;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
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
class PersonForm extends Form<User> {

    private final User user;
    private List<FileUpload> upload;
    private final RefreshingView<IMAccount> refreshView;
    private static final int NAMES_MIN_LENGTH = 2;
    private static final int NAMES_MAX_LENGTH = 40;
    @EJB(name = "UserManagerBean")
    private UserManagerLocal userManager;

    public PersonForm(final String componentName, final User user) {
        super(componentName);
        this.user = user;
        setModel(new CompoundPropertyModel<>(user));
        setMultiPart(true);

        createNameFields();
        createGenderField();
        createAdditionalFields();
        createDormitoryFields();

        //Ezt muszáj a konstruktorban csinálni a final kulcsszó miatt.
        final WebMarkupContainer rowPanel = new WebMarkupContainer("rowPanel");
        final IModel<Set<IMAccount>> model = new PropertyModel<>(user, "imAccounts");
        refreshView = new RefreshingView<IMAccount>("ims", model) {
            @Override
            protected Iterator<IModel<IMAccount>> getItemModels() {
                return new ModelIteratorAdapter<IMAccount>(model.getObject().iterator()) {
                    @Override
                    protected IModel<IMAccount> model(IMAccount object) {
                        return Model.of(object);
                    }
                };
            }

            @Override
            protected void populateItem(final Item<IMAccount> item) {
                final IMAccount acc = item.getModelObject();
                final Set<IMAccount> currObj = (Set<IMAccount>) getDefaultModelObject();
                item.add(new DropDownChoice("imProtocol",
                        new PropertyModel(acc, "protocol"),
                        Arrays.asList(IMProtocol.values())));
                item.add(new TextField("imPresenceID",
                        new PropertyModel(acc, "accountName")));
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
                ((Set<IMAccount>) refreshView.getDefaultModelObject()).add(new IMAccount(IMProtocol.gtalk, ""));
                if (target != null) {
                    target.add(rowPanel);
                }
                for (IMAccount iMAccount : ((Set<IMAccount>) refreshView.getDefaultModelObject())) {
                    System.out.println(iMAccount.toString());
                }
            }
        }.setDefaultFormProcessing(false));

        rowPanel.setOutputMarkupId(true);
        add(rowPanel);

        createPhotoField();

        add(new Label("neptunLabel", new PropertyModel(user, "neptunCode")));

        initAjaxPrivateLinks();

        //nem szabad használni a Form#onSubmit() függvényt, mindenképp szükség
        //van erre a gombra, különben az IM listás Ajax elromlik! A setResponsePage
        //true flag miatt készül ki valamiért.
        //Bővebben: http://osdir.com/ml/users-wicket.apache.org/2009-08/msg00836.html
        final Button submitButton = new Button("submitButton") {
            @Override
            public void onSubmit() {
                if (hasError()) {
                    error("Hiba a formon!");
                    return;
                    // TODO: proper error message
                }

                ProfileImage image = null;
                if (upload != null && !upload.isEmpty()) {
                    FileUpload fu = upload.get(0);
                    image = new ProfileImage(fu.getContentType(), fu.getBytes(), fu.getSize());
                }
                try {
                    userManager.updateUser(user, image);
                } catch (PekEJBException ex) {
                    error(new StringResourceModel(ex.getErrorCode().getMessageKey(), this,
                            null, ex.getParameters()).getString());
                    return;
                }
                getSession().info("Sikeres adatmódosítás. :)");
                setResponsePage(ShowPersonPage.class);
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
        DropDownChoice<Gender> genderChoice = new DropDownChoice<>("gender", Arrays.asList(Gender.values()));
//        genderChoice.setModel(new PropertyModel<Gender>(user, "gender"));
        genderChoice.setChoiceRenderer(new EnumChoiceRenderer<Gender>(this));
        add(genderChoice);
        genderChoice.setLabel(Model.of("Nem"));
        add(new SimpleFormComponentLabel("genderLabel", genderChoice));
    }

    private void createAdditionalFields() {
        DateTextField dateTF = new DateTextField("dateOfBirth", new StyleDateConverter("S-", true)) {
            @Override
            public Locale getLocale() {
                return new Locale("hu");
            }
        };

        dateTF.add(new ValidationStyleBehavior());
        dateTF.setLabel(new Model<String>("Születési dátum"));
        add(new ValidationSimpleFormComponentLabel("dateOfBirthLabel", dateTF));
        add(dateTF);

        TextField<String> homePostalAddressTF = new TextField<String>("homeAddress");
        add(homePostalAddressTF);
        homePostalAddressTF.setLabel(new Model<String>("Cím"));
        add(new SimpleFormComponentLabel("homePostalAddressLabel", homePostalAddressTF));

        RequiredTextField<String> mailTF = new RequiredTextField<String>("emailAddress");
        mailTF.add(EmailAddressValidator.getInstance());
        mailTF.add(new ValidationStyleBehavior());
        add(mailTF);
        mailTF.setLabel(new Model<String>("E-mail *"));
        add(new ValidationSimpleFormComponentLabel("mailLabel", mailTF));

        TextField<String> mobileTF = new TextField<String>("cellPhone");
        add(mobileTF);
        mobileTF.setLabel(new Model<String>("Mobil"));
        mobileTF.add(new PatternValidator(PatternHolder.PHONE_NUMBER_PATTERN));
        add(new ValidationSimpleFormComponentLabel("mobileLabel", mobileTF));

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

    private void createDormitoryFields() {
        TextField<String> rNumberTF = new TextField<String>("room");
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
        final NonCachingImage photo = new NonCachingImage("photo", new AbstractReadOnlyModel<ProfileImageResource>() {
            @Override
            public ProfileImageResource getObject() {
                return new ProfileImageResource(user);
            }
        });
        photo.setOutputMarkupId(true);
        add(photo);

        AjaxFallbackLink photoRemoveLink = new AjaxFallbackLink("photoRemoveLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    new ImageRemoverService().removeProfileImage(user);
                    user.setPhotoPath(null);
                    userManager.updateUser(user);
                } catch (PekEJBException ex) {
//                    error(new StringResourceModel(ex.getErrorCode().getMessageKey(),
//                            null, ex.getParameters()));
                    // TODO: report error to user
                    throw new NotImplementedException("report error to user");
                }

                setVisible(false);
                photo.setVisible(false);
                target.add(this);
                target.add(photo);
            }
        };
        photoRemoveLink.setOutputMarkupId(true);
        add(photoRemoveLink);

        if (!user.hasPhoto()) {
            photo.setVisible(false);
            photoRemoveLink.setVisible(false);
        }

        add(new FileUploadField("fileInput", new PropertyModel<List<FileUpload>>(this, "upload")));
    }

    private void initAjaxPrivateLinks() {
        add(new AttributeAjaxFallbackLink("mailAttributeLink", "mailAttributeImg", UserAttributeName.EMAIL, user));
        add(new AttributeAjaxFallbackLink("mobileAttributeLink", "mobileAttributeImg", UserAttributeName.CELL_PHONE, user));
        add(new AttributeAjaxFallbackLink("roomNumberAttributeLink", "roomNumberAttributeImg", UserAttributeName.ROOM_NUMBER, user));
        add(new AttributeAjaxFallbackLink("homePostalAddressAttributeLink", "homePostalAddressAttributeImg", UserAttributeName.HOME_ADDRESS, user));
        add(new AttributeAjaxFallbackLink("webpageAttributeLink", "webpageAttributeImg", UserAttributeName.WEBPAGE, user));
        add(new AttributeAjaxFallbackLink("dateOfBirthAttributeLink", "dateOfBirthAttributeImg", UserAttributeName.DATE_OF_BIRTH, user));
    }
}
