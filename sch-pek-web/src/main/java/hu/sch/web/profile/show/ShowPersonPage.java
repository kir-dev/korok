package hu.sch.web.profile.show;

import hu.sch.domain.user.IMAccount;
import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.kp.user.UserHistory;
import hu.sch.web.profile.ProfilePage;
import hu.sch.web.profile.admin.AdminPage;
import hu.sch.web.wicket.components.ProfileImageResource;
import hu.sch.web.wicket.components.customlinks.SearchLink;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
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
        long virid = params.get("virid").toLong(-1);

        if ((uid == null && virid == -1) || (uid != null && virid != -1)) {
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
        setDefaultModel(new CompoundPropertyModel<>(user));
        setHeaderLabelText(user.getFullName());

        PageParameters params = new PageParameters().add("id", user.getId());
        add(new BookmarkablePageLink("simpleView", ShowUser.class, params));
        add(new BookmarkablePageLink("detailView", UserHistory.class, params));

        Link<AdminPage> adminLink =
                new BookmarkablePageLink<>("adminLink", AdminPage.class,
                new PageParameters().add("uid", user.getScreenName()));
        if (!isCurrentUserAdmin()) {
            adminLink.setVisible(false);
        }
        add(adminLink);

        add(new SmartLinkLabel("emailAddress") {
            @Override
            public boolean isVisible() {
                return user.isAttributeVisible(UserAttributeName.EMAIL)
                        && user.getEmailAddress() != null;
            }
        });

        List<IMAccount> imList = new ArrayList<>();
        imList.addAll(user.getImAccounts());

        add(new ListView<IMAccount>("ims", imList) {
            @Override
            protected void populateItem(ListItem<IMAccount> item) {
                final IMAccount acc = item.getModelObject();
                item.add(new Label("imProtocol",
                        new PropertyModel<IMAccount>(acc, "protocol")));
                item.add(new Label("imAccountName",
                        new PropertyModel<IMAccount>(acc, "accountName")));
            }
        });

        add(new Label("cellPhone") {
            @Override
            public boolean isVisible() {
                return user.isAttributeVisible(UserAttributeName.CELL_PHONE)
                        && user.getCellPhone() != null;
            }
        });

        add(new Label("webpage") {
            @Override
            public boolean isVisible() {
                return user.isAttributeVisible(UserAttributeName.WEBPAGE)
                        && user.getWebpage() != null;
            }
        });

        add(new SearchLink("fullRoomNumber", SearchLink.USER_TYPE, user.getFullRoomNumber()) {

            @Override
            public boolean isVisible() {
                return user.isAttributeVisible(UserAttributeName.ROOM_NUMBER)
                        && !StringUtils.isBlank(user.getFullRoomNumber());
            }

        });

        add(new Label("nickName") {

            @Override
            public boolean isVisible() {
                return user.getNickName() != null;
            }

        });

        add(new Label("homeAddress") {
            @Override
            public boolean isVisible() {
                return user.isAttributeVisible(UserAttributeName.HOME_ADDRESS)
                        && user.getHomeAddress()!= null;
            }
        });

        String labelText = user.getDateOfBirth() == null ? null
                : new SimpleDateFormat("yyyy. MMMM dd.", new Locale("hu"))
                .format(user.getDateOfBirth());

        Label dateOfBirth = new Label("dateOfBirth", labelText) {

            @Override
            public boolean isVisible() {
                return user.isAttributeVisible(UserAttributeName.DATE_OF_BIRTH)
                        && user.getDateOfBirth() != null;
            }
        };

        add(dateOfBirth);

        NonCachingImage photo = new NonCachingImage("photo", new AbstractReadOnlyModel<ProfileImageResource>() {
            @Override
            public ProfileImageResource getObject() {
                return new ProfileImageResource(user);
            }
        });
        photo.setVisible(user.getPhotoPath()!= null);
        add(photo);
    }
}
