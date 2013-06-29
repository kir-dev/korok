package hu.sch.web.kp.group;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.wicket.components.EditDelegatesForm;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValueConversionException;

/**
 *
 * @author aldaris
 */
public final class ChangeDelegates extends KorokPage {

    public ChangeDelegates(final PageParameters params) {
        Long groupId;
        try {
            groupId = params.get("id").toLong();
        } catch (StringValueConversionException svce) {
            error("Hibás paraméter!");
            throw new RestartResponseException(getApplication().getHomePage());
        }
        final Group group = userManager.findGroupById(groupId);
        if (!isUserGroupLeader(group)) {
            getSession().error("Nincsen jogosultságod a művelet végrehajtásához!");
            throw new RestartResponseException(ShowGroup.class, new PageParameters().add("id", group.getId()));
        }

        setHeaderLabelText("Küldöttek beállítása");
        add(new Label("numberOfDelegates",
                (group.getDelegateNumber() == null ? "Nincs beállítva" : Integer.toString(group.getDelegateNumber()))));
        add(new Label("groupName", group.getName()));


        List<User> users = userManager.getUsersWithPrimaryMembership(groupId);
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
                setResponsePage(ShowGroup.class, new PageParameters().add("id", group.getId()));
            }
        });
    }
}
