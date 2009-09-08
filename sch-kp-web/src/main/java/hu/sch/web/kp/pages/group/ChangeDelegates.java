/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

        add(new Label("groupName", group.getName()));

        List<User> users = userManager.getUsersWithPrimaryMembership(groupId);
        Iterator<User> it = users.iterator();
        long groupLeaderId = userManager.getGroupLeaderForGroup(groupId).getId();
        if ((getSession()).getUserId() != groupLeaderId) {
            getSession().error("Ezt az oldalt, csak a kör körvezetője láthatja!");
            setResponsePage(ShowGroup.class, new PageParameters("id=" + groupId.toString()));
            return;
        }


        while (it.hasNext()) {
            User u = it.next();

            if (u.getId() == groupLeaderId) {
                it.remove();
            }
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
                int selected = 1; // a körvezető eleve küldött, a listától függetlenül.
                List<ExtendedUser> modifications = new ArrayList<ExtendedUser>();
                for (ExtendedUser extendedUser : eu) {
                    if (extendedUser.getSelected()) {
                        selected++;
                    }

                    if (selected > group.getDelegateNumber()) {
                        getSession().error("Ez a kör nem delegálhat ennyi tagot a küldöttgyűlésre!");
                        setResponsePage(new ChangeDelegates(params));
                        return;
                    }

                    if (extendedUser.getSelected() != extendedUser.getUser().getDelegated()) {
                        modifications.add(extendedUser);
                    }
                }

                for (ExtendedUser extendedUser : modifications) {

                    userManager.setUserDelegateStatus(extendedUser.getUser(), extendedUser.getSelected());

                }
                getSession().
                        info("A változások sikeresen mentésre kerültek");
                setResponsePage(new ChangeDelegates(params));
                return;
            }
        });




    }
}
