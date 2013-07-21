package hu.sch.web.profile.show;

import hu.sch.domain.user.IMAccount;
import hu.sch.domain.user.User;
import hu.sch.services.exceptions.NotImplementedException;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.kp.user.UserHistory;
import hu.sch.web.profile.ProfilePage;
import hu.sch.web.profile.admin.AdminPage;
import hu.sch.web.wicket.components.ImageResource;
import hu.sch.web.wicket.components.customlinks.SearchLink;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Homepage
 */
public class ShowPersonPage extends ProfilePage {

    private User user;

    public ShowPersonPage() {
        user = userManager.findUserByScreenName(getRemoteUser());
        bindPerson();

    }

    public ShowPersonPage(PageParameters params) {
        String uid = params.get("uid").toString();
        Long virid = params.get("virid").toLong();

        if ((uid == null && virid == null) || (uid != null && virid != null)) {
            // ha se uid se virid, vagy mindkettő meg van adva, akkor nem játszunk
            getSession().error("A felhasználó nem található!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        // vagy uid alapján vagy virid alapján keresünk usert
        if (uid != null) {
            user = userManager.findUserByScreenName(uid);
        } else {
            user = userManager.findUserById(virid);
        }
        if (user != null) {
            bindPerson();
        } else {
            getSession().error("A felhasználó nem található!");
            throw new RestartResponseException(getApplication().getHomePage());
        }
    }

    private void bindPerson() {
        throw new NotImplementedException();
//        setDefaultModel(new CompoundPropertyModel<Person>(user));
//        setHeaderLabelText(user.getFullName());
//        //add(new Label("uid"));
//        //add(new Label("fullName"));
//
//        if (user.getVirId() != null) {
//            PageParameters params = new PageParameters().add("id", user.getVirId());
//            add(new BookmarkablePageLink("simpleView", ShowUser.class, params));
//            add(new BookmarkablePageLink("detailView", UserHistory.class, params));
//            add(new Label("createCommunityProfile").setVisible(false));
//        } else {
//            add(new Label("simpleView").setVisible(false));
//            add(new Label("detailView").setVisible(false));
//            //hogy ne lehessen könyvjelzőzni a linket
//            Link pageLink = new Link("createCommunityProfile") {
//                @Override
//                public void onClick() {
//                    // TODO: remove link! it will unnecessary after the db merge
//                    // setResponsePage(CreateCommunityProfile.class);
//                }
//            };
//
//            add(pageLink);
//            //Ha nem a saját profilunkat nézzük, akkor ne jelenjen meg a készítős link
//            if (!user.getUid().equalsIgnoreCase(getRemoteUser())) {
//                pageLink.setVisible(false);
//            }
//        }
//
//        Link<AdminPage> adminLink =
//                new BookmarkablePageLink<AdminPage>("adminLink", AdminPage.class,
//                new PageParameters().add("uid", user.getUid()));
//        if (!isCurrentUserAdmin()) {
//            adminLink.setVisible(false);
//        }
//        add(adminLink);
//
//        WebMarkupContainer mailWMC = new WebMarkupContainer("mailWMC");
//
//        mailWMC.add(new SmartLinkLabel("mail"));
//        mailWMC.setVisible(
//                !user.isPrivateAttribute("mail")
//                && user.getMail() != null);
//        add(mailWMC);
//
//        add(new ListView<IMAccount>("ims", user.getIMAccounts()) {
//            @Override
//            protected void populateItem(ListItem<IMAccount> item) {
//                final IMAccount acc = item.getModelObject();
//                item.add(new Label("imProtocol",
//                        new PropertyModel<IMAccount>(acc, "protocol")));
//                item.add(new Label("imPresenceID",
//                        new PropertyModel<IMAccount>(acc, "presenceID")));
//            }
//        });
//
//        WebMarkupContainer mobileWMC = new WebMarkupContainer("mobileWMC");
//
//        mobileWMC.add(new Label("mobile"));
//        mobileWMC.setVisible(
//                !user.isPrivateAttribute("mobile")
//                && user.getMobile() != null);
//        add(mobileWMC);
//
//        WebMarkupContainer homePhoneWMC = new WebMarkupContainer("homePhoneWMC");
//
//        homePhoneWMC.add(new Label("homePhone"));
//        homePhoneWMC.setVisible(
//                !user.isPrivateAttribute("homePhone")
//                && user.getHomePhone() != null);
//        add(homePhoneWMC);
//
//        WebMarkupContainer webpageWMC = new WebMarkupContainer("webpageWMC");
//
//        webpageWMC.add(new SmartLinkLabel("webpage"));
//        webpageWMC.setVisible(
//                !user.isPrivateAttribute("labeledURI")
//                && user.getWebpage() != null);
//        add(webpageWMC);
//
//        WebMarkupContainer roomNumberWMC =
//                new WebMarkupContainer("roomNumberWMC");
//
//        roomNumberWMC.add(new SearchLink("roomNumber", SearchLink.USER_TYPE, user.getRoomNumber()));
//        roomNumberWMC.setVisible(
//                !user.isPrivateAttribute("roomNumber")
//                && user.getRoomNumber() != null);
//        add(roomNumberWMC);
//
//        WebMarkupContainer nickNameWMC = new WebMarkupContainer("nickNameWMC");
//
//        nickNameWMC.add(new Label(
//                "nickName"));
//        nickNameWMC.setVisible(user.getNickName() != null);
//        add(nickNameWMC);
//
//        WebMarkupContainer homePostalAddressWMC =
//                new WebMarkupContainer("homePostalAddressWMC");
//
//        homePostalAddressWMC.add(new Label("homePostalAddress"));
//        homePostalAddressWMC.setVisible(
//                !user.isPrivateAttribute("homePostalAddress")
//                && user.getHomePostalAddress() != null);
//        add(homePostalAddressWMC);
//
//        String labelText = user.getDateOfBirth() == null ? null
//                : new SimpleDateFormat("yyyy. MMMM dd.", new Locale("hu"))
//                .format(user.getDateOfBirth());
//
//        Label dateOfBirth = new Label("dateOfBirth", labelText);
//
//        WebMarkupContainer dateOfBirthWMC = new WebMarkupContainer("dateOfBirthWMC");
//
//        dateOfBirthWMC.add(dateOfBirth);
//
//        dateOfBirthWMC.setVisible(
//                !user.isPrivateAttribute("schacDateOfBirth")
//                && user.getDateOfBirth() != null);
//        add(dateOfBirthWMC);
//
//        NonCachingImage photo = new NonCachingImage("photo", new AbstractReadOnlyModel<ImageResource>() {
//            @Override
//            public ImageResource getObject() {
//                return new ImageResource(user.getPhoto(), "png");
//            }
//        });
//        photo.setVisible(user.getPhoto() != null);
//        add(photo);
    }
}
