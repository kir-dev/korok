package hu.sch.profile;

import hu.sch.kp.services.UserManagerRemote;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Homepage
 */
public class ShowPersonPage extends ProfilePage {

    @EJB(name = "UserManager")
    UserManagerRemote userManager;
    private Person person;

    public void setPerson(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    private void bindPerson() {
        setModel(new CompoundPropertyModel(person));
        setHeaderLabelModel(new PropertyModel(person, "fullName"));
        //add(new Label("uid"));
        //add(new Label("fullName"));

        WebMarkupContainer mailWMC = new WebMarkupContainer("mailWMC");
        mailWMC.add(new Label("mail"));
        mailWMC.setVisible(!person.isPrivateAttribute("mail") &&
                person.getMail() != null);
        add(mailWMC);

        WebMarkupContainer mobileWMC = new WebMarkupContainer("mobileWMC");
        mobileWMC.add(new Label("mobile"));
        mobileWMC.setVisible(!person.isPrivateAttribute("mobile") &&
                person.getMobile() != null);
        add(mobileWMC);

        WebMarkupContainer homePhoneWMC = new WebMarkupContainer("homePhoneWMC");
        homePhoneWMC.add(new Label("homePhone"));
        homePhoneWMC.setVisible(!person.isPrivateAttribute("homePhone") &&
                person.getHomePhone() != null);
        add(homePhoneWMC);

        WebMarkupContainer webpageWMC = new WebMarkupContainer("webpageWMC");
        webpageWMC.add(new Label("webpage"));
        webpageWMC.setVisible(!person.isPrivateAttribute("labeledURI") &&
                person.getWebpage() != null);
        add(webpageWMC);

        WebMarkupContainer roomNumberWMC = new WebMarkupContainer("roomNumberWMC");
        roomNumberWMC.add(new DormitoryRoomNumberLinkPanel("roomNumber", person));
//        roomNumberWMC.add(new Label("roomNumber"));
        roomNumberWMC.setVisible(!person.isPrivateAttribute("roomNumber") &&
                person.getRoomNumber() != null);
        add(roomNumberWMC);

        WebMarkupContainer nickNameWMC = new WebMarkupContainer("nickNameWMC");
        nickNameWMC.add(new Label("nickName"));
        nickNameWMC.setVisible(person.getNickName() != null);
        add(nickNameWMC);

        /*        WebMarkupContainer neptunWMC = new WebMarkupContainer("neptunWMC");
        neptunWMC.add(new Label("neptun"));
        neptunWMC.setVisible(!person.isPrivateAttribute("schacPersonalUniqueCode"));
        add(neptunWMC);*/

        WebMarkupContainer homePostalAddressWMC = new WebMarkupContainer("homePostalAddressWMC");
        homePostalAddressWMC.add(new Label("homePostalAddress"));
        homePostalAddressWMC.setVisible(!person.isPrivateAttribute("homePostalAddress") &&
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
                dob = new SimpleDateFormat("yyyyMMdd").parse(person.getDateOfBirth());
                dateOfBirth = new Label("dateOfBirth", new SimpleDateFormat("yyyy. MMMM dd.", new Locale("hu")).format(dob));
            } catch (ParseException ex) {
            }
        }
        WebMarkupContainer dateOfBirthWMC = new WebMarkupContainer("dateOfBirthWMC");
        dateOfBirthWMC.add(dateOfBirth);
        dateOfBirthWMC.setVisible(!person.isPrivateAttribute("schacDateOfBirth") &&
                person.getDateOfBirth() != null);
        add(dateOfBirthWMC);

//        add(new Label("image", person.getImage().getClass().getCanonicalName()));

        NonCachingImage photo = new NonCachingImage("photo", new AbstractReadOnlyModel() {

            @Override
            public Object getObject() {
                // TODO Auto-generated method stub
                return new ImageResource((byte[]) person.getPhoto(), "png");
            }
        });
        photo.setVisible(person.getPhoto() != null);
        add(photo);

        //add(new Label("status"));
        showCsoportTagsagok(person);
    }

    private void showCsoportTagsagok(Person person) {
        addCsoportTagsagTable("aktivcsoptagsagok", person.getActiveMemberships());
        addCsoportTagsagTable("inaktivcsoptagsagok", person.getInactiveMemberships());
    }

    private void addCsoportTagsagTable(String containerID, List<Membership> tagsagList) {
        WebMarkupContainer csopTagsagWMC = new WebMarkupContainer(containerID);
        if (tagsagList.size() == 0) {
            csopTagsagWMC.setVisible(false);
        }

        ListView tagsagok = new ListView("csoptagsag", tagsagList) {

            @Override
            protected void populateItem(ListItem item) {
                Membership membership = (Membership) item.getModelObject();

                item.setModel(new CompoundPropertyModel(item.getModelObject()));
                item.add(new Label("groupName"));
                WebMarkupContainer statusWMC = new WebMarkupContainer("statusWMC");
                if ("Aktív".equals(membership.getStatus()) || "Öregtag".equals(membership.getStatus())) {
                    statusWMC.setVisible(false);
                }
                statusWMC.add(new Label("status"));
                item.add(statusWMC);
            }
        };

        add(csopTagsagWMC);
        csopTagsagWMC.add(tagsagok);
    }

    public ShowPersonPage() {
        add(new FeedbackPanel("feedbackPanel"));

        try {
            setPerson(LDAPPersonManager.getInstance().getPersonByUid(getUid()));
            bindPerson();
        } catch (PersonNotFoundException e) {
        }

        add(new EditPersonMembershipPanel("editPersonMembershipTable").setVisible(false));
    }

    public ShowPersonPage(PageParameters params) {
        add(new FeedbackPanel("feedbackPanel"));

        String uid = params.getString("uid");
        String virid = params.getString("virid");

        if (uid == null && virid == null) {
            setResponsePage(new ErrorPage("A felhasználó nem található!"));
            return;
        } else if (uid != null && virid == null) {
            try {
                setPerson(LDAPPersonManager.getInstance().getPersonByUid(uid));
            } catch (PersonNotFoundException e) {
                setResponsePage(new ErrorPage("A felhasználó nem található!"));
                return;
            }
        } else if (uid == null && virid != null) {
            try {
                setPerson(LDAPPersonManager.getInstance().getPersonByVirId(virid));
            } catch (PersonNotFoundException e) {
                setResponsePage(new ErrorPage("A felhasználó nem található!"));
                return;
            }
        } else {
            setResponsePage(new ErrorPage("A kért oldal nem található!"));
            return;
        }

        try {
            add(new EditPersonMembershipPanel("editPersonMembershipTable", LDAPPersonManager.getInstance().getPersonByUid(getUid()), LDAPPersonManager.getInstance().getPersonByUid(person.getUid())));
        } catch (PersonNotFoundException e) {
        }
        bindPerson();
    }

    public ShowPersonPage(PageParameters params, String s) {
        this(params);
        info(s);
    }

    public ShowPersonPage(String s) {
        add(new FeedbackPanel("feedbackPanel"));
        info(s);

        try {
            setPerson(LDAPPersonManager.getInstance().getPersonByUid(getUid()));
            bindPerson();
        } catch (PersonNotFoundException e) {
            setResponsePage(new ErrorPage("A felhasználó nem található!"));
            return;
        }
    }
}
