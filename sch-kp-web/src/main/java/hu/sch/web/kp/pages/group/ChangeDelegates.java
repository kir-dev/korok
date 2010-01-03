/**
 * Copyright (c) 2009, Peter Major
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
package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.web.components.EditDelegatesForm;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.string.StringValueConversionException;

/**
 *
 * @author aldaris
 */
public final class ChangeDelegates extends SecuredPageTemplate {

    private static Logger log = Logger.getLogger(ChangeDelegates.class);

    public ChangeDelegates(final PageParameters params) {
        Long groupId;
        try {
            groupId = new Long(params.getLong("id"));
        } catch (StringValueConversionException svce) {
            error("Hibás paraméter!");
            throw new RestartResponseException(getApplication().getHomePage());
        }
        final Group group = userManager.findGroupById(groupId);
        if (!isUserGroupLeader(group)) {
            getSession().error("Nincsen jogosultságod a művelet végrehajtásához!");
            throw new RestartResponseException(ShowGroup.class, new PageParameters("id=" + group.getId()));
        }

        setHeaderLabelText("Küldöttek beállítása");
        add(new Label("numberOfDelegates",
                (group.getDelegateNumber() == null ? "Nincs beállítva" : Integer.toString(group.getDelegateNumber()))));
        add(new Label("groupName", group.getName()));

        List<User> users = userManager.getUsersWithPrimaryMembership(groupId);
        Iterator<User> it = users.iterator();
        long groupLeaderId = userManager.getGroupLeaderForGroup(groupId).getId();
        if ((getSession()).getUserId() != groupLeaderId) {
            log.warn("Illetéktelen hozzáférési kísérlet a küldöttek állításához, felhasználó: " + getSession().getUserId());
            getSession().error("Ezt az oldalt, csak a kör körvezetője láthatja!");
            throw new RestartResponseException(ShowGroup.class,
                    new PageParameters("id=" + groupId.toString()));
        }

        add(new FeedbackPanel("pagemessages"));

        add(new EditDelegatesForm("form", users) {

            @Override
            protected void onPopulateItem(ListItem<ExtendedUser> item, User user) {
                //Nothing to do. :)
            }

            @Override
            protected void onSubmit() {
                List<ExtendedUser> eu = getLines();
                int selected = 0; // a körvezető eleve küldött, a listától függetlenül.
                List<ExtendedUser> modifications = new ArrayList<ExtendedUser>();
                for (ExtendedUser extendedUser : eu) {
                    if (extendedUser.getSelected()) {
                        selected++;
                    }

                    if (extendedUser.getSelected() != extendedUser.getUser().getDelegated()) {
                        modifications.add(extendedUser);
                    }
                }
                if (group.getDelegateNumber() == null || selected > group.getDelegateNumber()) {
                    getSession().error("Ez a kör nem delegálhat ennyi tagot a küldöttgyűlésre!");
                    setResponsePage(new ChangeDelegates(params));
                    return;
                }

                for (ExtendedUser extendedUser : modifications) {
                    userManager.setUserDelegateStatus(extendedUser.getUser(), extendedUser.getSelected());
                }
                getSession().info("A változások sikeresen mentésre kerültek");
                setResponsePage(ShowGroup.class, new PageParameters("id=" + group.getId()));
                return;
            }
        });
    }
}
