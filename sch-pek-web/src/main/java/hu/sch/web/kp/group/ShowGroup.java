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

package hu.sch.web.kp.group;

import hu.sch.services.exceptions.MembershipAlreadyExistsException;
import hu.sch.web.wicket.components.tables.DateIntervalPropertyColumn;
import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.User;
import hu.sch.web.kp.group.admin.AdminMembershipsPanel;
import hu.sch.web.kp.group.admin.AdminOldBoysPanel;
import hu.sch.web.wicket.behaviors.ConfirmationBehavior;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.wicket.components.MembershipTablePanel;
import hu.sch.web.wicket.components.tables.DatePropertyColumn;
import hu.sch.web.wicket.components.tables.MembershipTable;
import java.util.Date;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

/**
 * Az egyes körökről ezen az oldalon jelenítünk meg részletes adatokat. A
 * körvezetők számára lehetőség van a kör egyes feladatait kezelni.
 *
 * @author  hege
 * @author  messo
 */
public class ShowGroup extends KorokPage {

    /**
     * Paraméter nélküli konstruktor, hogy a nem létező paraméter is le legyen
     * kezelve.
     */
    public ShowGroup() {
        error("Hibás paraméter!");
        throw new RestartResponseException(getApplication().getHomePage());
    }

    /**
     * A kör adatlapját itt állítjuk össze.
     * @param parameters A megjelenítendő kör azonosítója
     */
    public ShowGroup(PageParameters parameters) {
        //az oldal paraméterének dekódolása
        Long id = null;
        try {
            id = parameters.getLong("id");
        } catch (NumberFormatException e) {
            error("Hibás paraméter!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        final Group group = userManager.findGroupWithMembershipsById(id);
        final User user = getUser();
        //ha a kör nem létezik
        if (group == null) {
            error("A megadott kör nem létezik!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        // TODO: elegánsabb megoldást szerkesszünk ide (rövidített név mező?)
        // headercímke szövegének megadása, csalni kell MAVE hosszú neve miatt..
        if (group.getName().contains("Informatikus-hallgatók")) {
            setHeaderLabelText("MAVE adatlapja");
            setTitleText("MAVE");
        } else {
            setHeaderLabelText(group.getName());
            setTitleText(group.getName());
        }

        //A jobb oldali leugró menühöz előállítjuk a csoporttörténetes linket.
        add(new BookmarkablePageLink<GroupHistory>("detailView", GroupHistory.class,
                new PageParameters("id=" + group.getId().toString())));
        //A kör admin felületéhez szükséges link jogosultságellenőrzéssel
        Link<EditGroupInfo> editPageLink = new BookmarkablePageLink<EditGroupInfo>("editPage", EditGroupInfo.class,
                new PageParameters("id=" + group.getId().toString()));
        if (user != null && isUserGroupLeader(group)) {
            editPageLink.setVisible(true);
        } else {
            editPageLink.setVisible(false);
        }
        add(editPageLink);

        //A kör küldöttjeinek beállításához szükséges link jogosultságellenőrzéssel
        Link<ChangeDelegates> editDelegates =
                new BookmarkablePageLink<ChangeDelegates>("editDelegates", ChangeDelegates.class,
                new PageParameters("id=" + group.getId().toString()));

        if (user != null && isUserGroupLeader(group) && group.getIsSvie()) {
            editDelegates.setVisible(true);
        } else {
            editDelegates.setVisible(false);
        }
        add(editDelegates);

        //A kör adatlapjának előállítása (kis táblázat)
        setDefaultModel(new CompoundPropertyModel<Group>(group));
        add(new Label("name"));
        add(new Label("founded"));
        add(new Label("svieMs", (group.getIsSvie() ? "igen" : "nem")));
        add(new SmartLinkLabel("webPage"));
        add(new SmartLinkLabel("mailingList"));
        add(new MultiLineLabel("introduction"));

        //körbe jelentkezéshez a link, JS-es kérdezéssel
        Link<Void> applyLink = new Link<Void>("applyToGroup") {

            @Override
            public void onClick() {
                try {
                    userManager.addUserToGroup(user, group, new Date(), null, false);
                    getSession().info("Sikeres jelentkezés");
                    setResponsePage(ShowUser.class);
                } catch (MembershipAlreadyExistsException ex) {
                    getSession().error("Már tagja vagy a körnek!");
                }
            }
        };
        applyLink.add(new ConfirmationBehavior("Biztosan szeretnél jelentkezni a körbe?"));
        if (user == null || user.getGroups().contains(group) || !group.getUsersCanApply()) {
            applyLink.setVisible(false);
        }
        add(applyLink);

        //az egyes paneleket elő kell állítani, ez a jogosultságtól függ.
        List<Membership> activeMembers = group.getActiveMemberships();
        List<Membership> inactiveMembers = group.getInactiveMemberships();
        Panel adminOrActivePanel;
        Panel adminOrOldBoysPanel;

        if (isUserGroupLeader(group) || hasUserDelegatedPostInGroup(user, group)) {
            adminOrActivePanel = new AdminMembershipsPanel("adminOrActive", activeMembers);
            adminOrOldBoysPanel = new AdminOldBoysPanel("adminOrOldBoy", inactiveMembers);
        } else {
            adminOrActivePanel = new MembershipTablePanel("adminOrActive", new MembershipTable<Membership>("table",
                    activeMembers, Membership.class) {

                @Override
                public void onPopulateColumns(List<IColumn<Membership>> columns) {
                    columns.add(new DatePropertyColumn<Membership>(new Model<String>("Tagság kezdete"),
                            MembershipTable.SORT_BY_MEMBERSHIP_DURATION, "start"));
                }
            });

            adminOrOldBoysPanel = new MembershipTablePanel("adminOrOldBoy", new MembershipTable<Membership>("table",
                    inactiveMembers, Membership.class) {

                @Override
                public void onPopulateColumns(List<IColumn<Membership>> columns) {
                    columns.add(new DateIntervalPropertyColumn<Membership>(new Model<String>("Tagság ideje"),
                            MembershipTable.SORT_BY_MEMBERSHIP_DURATION, "start", "end"));
                }
            });
        }
        add(adminOrActivePanel);
        add(adminOrOldBoysPanel);
    }
}