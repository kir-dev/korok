package hu.sch.web.profile.pages.show;

import hu.sch.domain.profile.IMAccount;
import hu.sch.domain.profile.Person;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import hu.sch.services.EntitlementManagerRemote;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.components.ImageResource;
import hu.sch.web.error.ErrorPage;
import hu.sch.web.profile.pages.community.CreateCommunityProfile;
import hu.sch.web.profile.pages.search.DormitoryRoomNumberLinkPanel;
import hu.sch.web.profile.pages.template.ProfilePage;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.link.Link;

/**
 * Homepage
 */
public class ShowPersonPage extends ProfilePage {

    @EJB(name = "foo", mappedName = "EntitlementManager")
    EntitlementManagerRemote entitlementManager;
    //private static final Logger log = Logger.getLogger(ShowPersonPage.class);
    private Person person;

    public void setPerson(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    private void bindPerson() {
        setDefaultModel(new CompoundPropertyModel<Person>(person));
        setHeaderLabelModel(new PropertyModel<Person>(person, "fullName"));
        //add(new Label("uid"));
        //add(new Label("fullName"));

        if (person.getVirId() != null) {
            add(new ExternalLink("communityProfile",
                    "/korok/showuser/id/" +
                    person.getVirId(), "Közösségi profil"));
            add(new Label("createCommunityProfile").setVisible(false));
        } else {
            add(new Label("communityProfile").setVisible(false));
            //hogy ne lehessen könyvjelzőzni a linket
            Link pageLink = new Link("createCommunityProfile") {

                @Override
                public void onClick() {
                    setResponsePage(new CreateCommunityProfile(new ShowPersonPage()));
                    return;
                }
            };


            add(pageLink);
            //Ha nem a saját profilunkat nézzük, akkor ne jelenjen meg a készítős link
            if (!person.getUid().equalsIgnoreCase(getUid())) {
                pageLink.setVisible(false);
            }
        }

        WebMarkupContainer mailWMC = new WebMarkupContainer("mailWMC");

        mailWMC.add(new SmartLinkLabel("mail"));
        mailWMC.setVisible(
                !person.isPrivateAttribute("mail") &&
                person.getMail() != null);
        add(mailWMC);

        add(new ListView<IMAccount>("ims", person.getIMAccounts()) {

                    @Override
                    protected void populateItem(ListItem<IMAccount> item) {
                        final IMAccount acc = item.getModelObject();
                        item.add(new Label("imProtocol",
                                new PropertyModel<IMAccount>(acc, "protocol")));
                        item.add(new Label("imPresenceID",
                                new PropertyModel<IMAccount>(acc, "presenceID")));
                    }
                });

        WebMarkupContainer mobileWMC = new WebMarkupContainer("mobileWMC");

        mobileWMC.add(new Label("mobile"));
        mobileWMC.setVisible(
                !person.isPrivateAttribute("mobile") &&
                person.getMobile() != null);
        add(mobileWMC);

        WebMarkupContainer homePhoneWMC = new WebMarkupContainer("homePhoneWMC");

        homePhoneWMC.add(new Label("homePhone"));
        homePhoneWMC.setVisible(
                !person.isPrivateAttribute("homePhone") &&
                person.getHomePhone() != null);
        add(homePhoneWMC);

        WebMarkupContainer webpageWMC = new WebMarkupContainer("webpageWMC");

        webpageWMC.add(new SmartLinkLabel("webpage"));
        webpageWMC.setVisible(
                !person.isPrivateAttribute("labeledURI") &&
                person.getWebpage() != null);
        add(webpageWMC);

        WebMarkupContainer roomNumberWMC =
                new WebMarkupContainer("roomNumberWMC");

        roomNumberWMC.add(new DormitoryRoomNumberLinkPanel("roomNumber", person));
//        roomNumberWMC.add(new Label("roomNumber"));
        roomNumberWMC.setVisible(
                !person.isPrivateAttribute("roomNumber") &&
                person.getRoomNumber() != null);
        add(roomNumberWMC);

        WebMarkupContainer nickNameWMC = new WebMarkupContainer("nickNameWMC");

        nickNameWMC.add(new Label(
                "nickName"));
        nickNameWMC.setVisible(person.getNickName() != null);
        add(nickNameWMC);

        /*        WebMarkupContainer neptunWMC = new WebMarkupContainer("neptunWMC");
        neptunWMC.add(new Label("neptun"));
        neptunWMC.setVisible(!person.isPrivateAttribute("schacPersonalUniqueCode"));
        add(neptunWMC);*/

        WebMarkupContainer homePostalAddressWMC =
                new WebMarkupContainer("homePostalAddressWMC");

        homePostalAddressWMC.add(new Label("homePostalAddress"));
        homePostalAddressWMC.setVisible(
                !person.isPrivateAttribute("homePostalAddress") &&
                person.getHomePostalAddress() != null);
        add(homePostalAddressWMC);

        /*        add(new Label("gender", new Model() {
        
        @Override
        public Object getObject() {
        Person p = (Person) getModelObject();
        if (p.getGender() == null) {
        return new String("");
        }
        
        if (p.getGender().equals("1")) {
        return new String("Férfi");
        } else if (p.getGender().equals("2")) {
        return new String("Nő");
        }
        
        return new String("");
        }
        }));*/

        Label dateOfBirth = new Label("dateOfBirth");
        Date dob;
        if (person.getDateOfBirth() != null) {
            try {
                dob =
                        new SimpleDateFormat("yyyyMMdd").parse(person.getDateOfBirth());
                dateOfBirth = new Label("dateOfBirth", new SimpleDateFormat("yyyy. MMMM dd.",
                        new Locale("hu")).format(dob));
            } catch (ParseException ex) {
            }
        }
        WebMarkupContainer dateOfBirthWMC =
                new WebMarkupContainer("dateOfBirthWMC");

        dateOfBirthWMC.add(dateOfBirth);

        dateOfBirthWMC.setVisible(
                !person.isPrivateAttribute("schacDateOfBirth") &&
                person.getDateOfBirth() != null);
        add(dateOfBirthWMC);

//        add(new Label("image", person.getImage().getClass().getCanonicalName()));

        NonCachingImage photo = new NonCachingImage("photo", new AbstractReadOnlyModel<ImageResource>() {

            @Override
            public ImageResource getObject() {
                // TODO Auto-generated method stub
                return new ImageResource(person.getPhoto(), "png");
            }
        });
        photo.setVisible(person.getPhoto() != null);
        add(photo);
    //add(new Label("status"));
    }

    public ShowPersonPage() {
        add(new FeedbackPanel("feedbackPanel"));
        try {
            setPerson(ldapManager.getPersonByUid(getUid()));
            bindPerson();
        } catch (PersonNotFoundException e) {
        }
    }

    public ShowPersonPage(PageParameters params) {
        add(new FeedbackPanel("feedbackPanel"));
        String uid = params.getString("uid");
        String virid = params.getString("virid");

        if (uid == null && virid == null) {
            getSession().error("A felhasználó nem található!");
            setResponsePage(ErrorPage.class);
            return;
        } else if (uid != null && virid == null) {
            try {
                setPerson(ldapManager.getPersonByUid(uid));
            } catch (PersonNotFoundException e) {
                getSession().error("A felhasználó nem található!");
                setResponsePage(ErrorPage.class);
                return;
            }
        } else if (uid == null && virid != null) {
            try {
                setPerson(ldapManager.getPersonByVirId(virid));
            } catch (PersonNotFoundException e) {
                getSession().error("A felhasználó nem található!");
                setResponsePage(ErrorPage.class);
                return;
            }
        } else {
            getSession().error("A felhasználó nem található!");
            setResponsePage(ErrorPage.class);
            return;
        }
        bindPerson();
    }

    protected EntitlementManagerRemote getEntitlementManager() {
        return entitlementManager;
    }
}
