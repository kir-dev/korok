/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.svie;

import hu.sch.domain.Group;
import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.User;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public final class SvieAccount extends SecuredPageTemplate {

    private Group selectedGroup;

    public SvieAccount() {
        final User user = getUser();
        //ha még nem SVIE tag, akkor továbbítjuk a SVIE regisztrációs oldalra.
        if (user.getSvieMembershipType().equals(SvieMembershipType.NEMTAG)) {
            throw new RestartResponseAtInterceptPageException(new SvieRegistration(user));
        }

        setHeaderLabelText("SVIE tagság beállításai");

        add(new FeedbackPanel("pagemessages"));
        Form form = new Form("form") {

            @Override
            protected void onSubmit() {
                System.out.println("kiválasztott csoport:" + selectedGroup);
            }
        };

        IModel<List<Group>> groupNames = new LoadableDetachableModel<List<Group>>() {

            @Override
            protected List<Group> load() {
                List<Group> l = new ArrayList<Group>();
                Group group = new Group();
                group.setName("valami");
                group.setId(1L);
                l.add(group);
                return l;
            }
        };

        ListChoice listChoice = new ListChoice("listchoice", new PropertyModel<Group>(this, "selectedGroup"), groupNames);
        listChoice.setChoiceRenderer(new GroupNameChoices());
        listChoice.setMaxRows(5);
        listChoice.setNullValid(false);
        form.add(listChoice);
        add(form);
    }

    private class GroupNameChoices implements IChoiceRenderer<Object> {

        public Object getDisplayValue(Object object) {
            Group group = (Group) object;
            return group.getName();
        }

        public String getIdValue(Object object, int index) {
            return object.toString();
        }
    }
}
