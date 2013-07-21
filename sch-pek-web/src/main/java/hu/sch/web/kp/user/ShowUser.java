package hu.sch.web.kp.user;

import hu.sch.domain.user.User;
import hu.sch.domain.*;
import hu.sch.services.exceptions.MembershipAlreadyExistsException;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.group.GroupHierarchy;
import hu.sch.web.profile.show.ShowPersonPage;
import hu.sch.web.wicket.components.tables.UsersMembershipTable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hege
 */
public class ShowUser extends KorokPage {

    private static Logger logger = LoggerFactory.getLogger(ShowUser.class);
    private Long id;
    private boolean ownProfile = false;
    private Group addToCsoportSelected;

    public ShowUser() {
        initComponents();
    }

    public ShowUser(PageParameters parameters) {
        try {
            id = parameters.get("id").toLong();
            // ha az adott ID a mi ID-nk, akkor ez a mi profilunk.
            if (id.equals(getSession().getUserId())) {
                ownProfile = true;
            }
        } catch (StringValueConversionException ex) {
            logger.warn("Could not interpret pageparameter: " + parameters);
        }
        initComponents();
    }

    private void initComponents() {
        try {
            if (id == null) {
                id = getSession().getUserId();
                ownProfile = true;
            }
        } catch (Exception e) {
            id = null;
        }
        if (id == null) {
            getSession().info("Egy körben sem vagy tag");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        final User user = userManager.findUserById(id, true);
        if (user == null) {
            info("A felhasználó nem található");
            throw new RestartResponseException(GroupHierarchy.class);
        }
        setDefaultModel(new CompoundPropertyModel<User>(user));
        setTitleText(user.getFullName());
        setHeaderLabelText(user.getFullName() + " felhasználó lapja");
        if (ownProfile) {
            add(new BookmarkablePageLink<UserHistory>("detailView", UserHistory.class));
        } else {
            add(new BookmarkablePageLink<UserHistory>("detailView", UserHistory.class,
                    new PageParameters().add("id", user.getId().toString())));
        }

        add(new BookmarkablePageLink("profilelink", ShowPersonPage.class,
                new PageParameters().add("virid", id.toString())));
        user.sortMemberships();

        add(new UsersMembershipTable("csoptagsag", user.getMemberships(), ownProfile, 20) {

            @Override
            protected void onWannabeOldBoy(Membership ms) {
                for (Post post : ms.getPosts()) {
                    if (post.getPostType().getPostName().equals(PostType.KORVEZETO)) {
                        getSession().error("Körvezetőként nem teheted magad öregtaggá!");
                        return;
                    }
                }
                membershipManager.inactivateMembership(ms);
                getSession().info("Az öregtaggá válás sikeresen megtörtént");
            }
        }.getDataTable());

        // Nézzük meg, hogy milyen csoportokba hívhatjuk meg a felhasználót.
        List<Group> groups;
        User currentUser = getUser();
        if (currentUser == null) {
            groups = new ArrayList<Group>();
        } else {
            groups = currentUser.getGroups();
        }

        List<Group> korvezetoicsoportok = new ArrayList<Group>();
        for (Group cs : groups) {
            if (isUserGroupLeader(cs) && !user.getGroups().contains(cs)) {
                korvezetoicsoportok.add(cs);
            }
        }

        Form<User> addToGroupForm = new Form<User>("addToGroupForm") {

            @Override
            protected void onSubmit() {
                try {
                    membershipManager.joinGroup(addToCsoportSelected, user, new Date(), null, isUserGroupLeader(addToCsoportSelected));
                    getSession().info("A felhasználó a <b>" + addToCsoportSelected + "</b> körbe felvéve");
                    setResponsePage(ShowUser.class, new PageParameters().add("id", user.getId()));
                } catch (MembershipAlreadyExistsException ex) {
                    getSession().error("A felhasználó már tagja a körnek!");
                }
            }
        };
        final DropDownChoice<Group> groupDdc = new DropDownChoice<Group>("groupDdc",
                new PropertyModel<Group>(this, "addToCsoportSelected"), korvezetoicsoportok);
        addToGroupForm.add(groupDdc);
        groupDdc.setRequired(true);
        add(addToGroupForm);
        addToGroupForm.setVisible(!korvezetoicsoportok.isEmpty()
                && isUserGroupLeaderInSomeGroup());
    }
}
