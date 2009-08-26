/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.user;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.User;
import hu.sch.web.components.ConfirmationBoxRenderer;
import hu.sch.web.components.customlinks.PdfLink;
import hu.sch.web.kp.pages.group.GroupHierarchy;
import hu.sch.web.kp.pages.group.ShowGroup;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class ShowUser extends SecuredPageTemplate {

    private Long id;
    private boolean ownProfile = false;
    private Group addToCsoportSelected;

    public ShowUser() {
        ownProfile = true;
        initComponents();
    }

    public void initComponents() {
        try {
            if (id == null) {
                id = getSession().getUserId();
            }
        } catch (Exception e) {
            id = null;
        }
        if (id == null) {
            info("Egy körben sem vagy tag");
            setResponsePage(GroupHierarchy.class);
            return;
        }
        add(new FeedbackPanel("pagemessages"));
        final User user = userManager.findUserWithCsoporttagsagokById(id);
        if (user == null) {
            info("Egy körben sem vagy tag");
            setResponsePage(GroupHierarchy.class);
            return;
        }
        setDefaultModel(new CompoundPropertyModel(user));
        setHeaderLabelText(user.getName() + " felhasználó lapja");
        if (ownProfile) {
            add(new BookmarkablePageLink("detailView", UserHistory.class));
        } else {
            add(new BookmarkablePageLink("detailView", UserHistory.class,
                    new PageParameters("id=" + user.getId().toString())));
        }

        add(new ExternalLink("profilelink",
                "/profile/show/virid/" + id.toString()));
        user.sortMemberships();
        ListView<Membership> csoptagsagok = new ListView<Membership>("csoptagsag", user.getMemberships()) {

            @Override
            protected void populateItem(ListItem<Membership> item) {
                final Membership ms = item.getModelObject();
                item.setModel(new CompoundPropertyModel<Membership>(ms));
                BookmarkablePageLink csoplink =
                        new BookmarkablePageLink("csoplink", ShowGroup.class,
                        new PageParameters("id=" +
                        ms.getGroup().getId().toString()));
                csoplink.add(new Label("group.name"));
                item.add(csoplink);
                item.add(new Label("rights", getConverter(List.class).convertToString(ms.getPosts(), getLocale())));
                item.add(DateLabel.forDatePattern("start", "yyyy.MM.dd."));
                item.add(DateLabel.forDatePattern("end", "yyyy.MM.dd."));
                Link eraseLink = new Link("deleteMembership") {

                    @Override
                    public void onClick() {
                        userManager.deleteMembership(ms);
                        getSession().info("A tagság törlése sikeresen megtörtént");
                        setResponsePage(ShowUser.class);
                        return;
                    }
                };
                eraseLink.add(new ConfirmationBoxRenderer("Biztosan meg szeretnéd szüntetni a tagságodat?"));
                if (!ownProfile) {
                    eraseLink.setVisible(false);
                }
                item.add(eraseLink);
            }
        };
        add(csoptagsagok);
        List<Group> csoportok;

        if (getUser() == null) {
            csoportok = new ArrayList<Group>();
        } else {
            csoportok = getUser().getGroups();
        }
        List<Group> korvezetoicsoportok = new ArrayList<Group>();
        for (Group cs : csoportok) {
            if (isUserGroupLeader(cs) &&
                    !user.getGroups().contains(cs)) {
                korvezetoicsoportok.add(cs);
            }
        }

        final DropDownChoice csoport = new DropDownChoice("group",
                new PropertyModel(this, "addToCsoportSelected"), korvezetoicsoportok);
        Form csoportbaFelvetel = new Form("csoportbaFelvetel") {

            @Override
            protected void onSubmit() {
                userManager.addUserToGroup(user, addToCsoportSelected, new Date(), null);
                getSession().info("A felhasználó a " + addToCsoportSelected + " körbe felvéve");
                setResponsePage(ShowUser.class, new PageParameters("id=" + user.getId()));
            }
        };
        csoportbaFelvetel.add(csoport);
        add(csoportbaFelvetel);
        csoportbaFelvetel.setVisible(!korvezetoicsoportok.isEmpty() &&
                isUserGroupLeaderInSomeGroup());

        add(new PdfLink("pdfLink"));
    }

    public ShowUser(PageParameters parameters) {
        try {
            id = parameters.getLong("id");
        } catch (Throwable t) {
            t.printStackTrace();
        }
        initComponents();
    }

    public Group getAddToCsoportSelected() {
        return addToCsoportSelected;
    }

    public void setAddToCsoportSelected(Group addToCsoportSelected) {
        this.addToCsoportSelected = addToCsoportSelected;
    }
}
