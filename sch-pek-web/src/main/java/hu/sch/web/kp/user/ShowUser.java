/**
 * Copyright (c) 2008-2010, Peter Major
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
package hu.sch.web.kp.user;

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

        final User user = userManager.findUserWithMembershipsById(id);
        if (user == null) {
            info("A felhasználó nem található");
            throw new RestartResponseException(GroupHierarchy.class);
        }
        setDefaultModel(new CompoundPropertyModel<User>(user));
        setTitleText(user.getName());
        setHeaderLabelText(user.getName() + " felhasználó lapja");
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
                userManager.setMemberToOldBoy(ms);
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
                    userManager.addUserToGroup(user, addToCsoportSelected, new Date(), null, isUserGroupLeader(addToCsoportSelected));
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
