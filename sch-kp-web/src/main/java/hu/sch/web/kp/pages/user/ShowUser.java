/**
 * Copyright (c) 2009-2010, Peter Major
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

package hu.sch.web.kp.pages.user;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.Post;
import hu.sch.domain.PostType;
import hu.sch.domain.User;
import hu.sch.web.wicket.behaviors.ConfirmationBehavior;
import hu.sch.web.kp.pages.group.GroupHierarchy;
import hu.sch.web.kp.pages.group.ShowGroup;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
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
            getSession().info("Egy körben sem vagy tag");
            throw new RestartResponseException(getApplication().getHomePage());
        }
        add(new FeedbackPanel("pagemessages"));
        final User user = userManager.findUserWithCsoporttagsagokById(id);
        if (user == null) {
            info("A felhasználó nem található");
            throw new RestartResponseException(GroupHierarchy.class);
        }
        setDefaultModel(new CompoundPropertyModel<User>(user));
        setHeaderLabelText(user.getName() + " felhasználó lapja");
        if (ownProfile) {
            add(new BookmarkablePageLink<UserHistory>("detailView", UserHistory.class));
        } else {
            add(new BookmarkablePageLink<UserHistory>("detailView", UserHistory.class,
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
                        new BookmarkablePageLink<ShowGroup>("csoplink", ShowGroup.class,
                        new PageParameters("id="
                        + ms.getGroup().getId().toString()));
                csoplink.add(new Label("group.name"));
                item.add(csoplink);
                if (ms.getEnd() != null) {
                    item.add(new Label("rights", "öregtag"));
                } else {
                    item.add(new Label("rights", getConverter(List.class).convertToString(ms.getPosts(), getLocale())));
                }
                item.add(DateLabel.forDatePattern("start", "yyyy.MM.dd."));
                item.add(DateLabel.forDatePattern("end", "yyyy.MM.dd."));

                Link<Void> oldBoyLink = new Link<Void>("oldBoyLink") {

                    @Override
                    public void onClick() {
                        for (Post post : ms.getPosts()) {
                            if (post.getPostType().getPostName().equals(PostType.KORVEZETO)) {
                                getSession().error("Körvezetőként nem teheted magad öregtaggá!");
                                setResponsePage(ShowUser.class);
                                return;
                            }
                        }
                        userManager.setMemberToOldBoy(ms);
                        getSession().info("Az öregtaggá válás sikeresen megtörtént");
                        setResponsePage(ShowUser.class);
                        return;
                    }
                };
                oldBoyLink.add(new ConfirmationBehavior("Biztosan öregtaggá szeretnél válni?"));
                if (!ownProfile) {
                    oldBoyLink.setVisible(false);
                }
                item.add(oldBoyLink);
            }
        };
        add(csoptagsagok);
        List<Group> groups;

        if (getUser() == null) {
            groups = new ArrayList<Group>();
        } else {
            groups = getUser().getGroups();
        }
        List<Group> korvezetoicsoportok = new ArrayList<Group>();
        for (Group cs : groups) {
            if (isUserGroupLeader(cs) && !user.getGroups().contains(cs)) {
                korvezetoicsoportok.add(cs);
            }
        }

        final DropDownChoice<Group> csoport = new DropDownChoice<Group>("group",
                new PropertyModel<Group>(this, "addToCsoportSelected"), korvezetoicsoportok);
        Form<User> csoportbaFelvetel = new Form<User>("csoportbaFelvetel") {

            @Override
            protected void onSubmit() {
                userManager.addUserToGroup(user, addToCsoportSelected, new Date(), null);
                getSession().info("A felhasználó a " + addToCsoportSelected + " körbe felvéve");
                setResponsePage(ShowUser.class, new PageParameters("id=" + user.getId()));
            }
        };
        csoportbaFelvetel.add(csoport);
        add(csoportbaFelvetel);
        csoportbaFelvetel.setVisible(!korvezetoicsoportok.isEmpty()
                && isUserGroupLeaderInSomeGroup());
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
