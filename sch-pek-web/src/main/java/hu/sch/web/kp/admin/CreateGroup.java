package hu.sch.web.kp.admin;

import hu.sch.domain.Group;
import hu.sch.domain.GroupStatus;
import hu.sch.domain.user.User;
import hu.sch.web.error.NotFound;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.wicket.behaviors.ValidationStyleBehavior;
import hu.sch.web.wicket.components.ValidationSimpleFormComponentLabel;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public class CreateGroup extends KorokPage {

    private Group group = new Group();
    private User user;
    private String leaderName = "Körvezető neve";

    public CreateGroup() {
        if (!isCurrentUserAdmin()) {
            throw new RestartResponseException(NotFound.class);
        }
        setHeaderLabelText("Új kör létrehozása");
        Form<Group> createGroupForm = new Form<Group>("createGroupForm", new CompoundPropertyModel<Group>(group)) {

            @Override
            protected void onSubmit() {
                try {
                    group.setStatus(GroupStatus.akt);
                    group.setIsSvie(Boolean.FALSE);
                    groupManager.createGroup(group, user);
                } catch (Exception ex) {
                    getSession().error("Hiba a kör létrehozásakor: " + ex.getMessage());
                    setResponsePage(CreateGroup.class);
                    return;
                }
                getSession().info("A kör sikeresen létrehozva.");
                setResponsePage(CreateGroup.class);
            }
        };

        RequiredTextField<String> groupNameTF = new RequiredTextField<String>("name");
        groupNameTF.setLabel(new Model<String>("Név"));
        groupNameTF.add(new ValidationStyleBehavior());
        createGroupForm.add(groupNameTF);
        createGroupForm.add(new ValidationSimpleFormComponentLabel("groupNameLabel", groupNameTF));

        RequiredTextField<String> groupTypeTF = new RequiredTextField<String>("type");
        groupTypeTF.setLabel(new Model<String>("Típus"));
        groupTypeTF.add(new ValidationStyleBehavior());
        createGroupForm.add(groupTypeTF);
        createGroupForm.add(new ValidationSimpleFormComponentLabel("groupTypeLabel", groupTypeTF));

        List<Group> groups = groupManager.getAllGroups();

        ListChoice listChoice = new ListChoice("parent", groups);
        listChoice.setChoiceRenderer(new GroupNameChoices());
        listChoice.setNullValid(true);
        createGroupForm.add(listChoice);

        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        final ListChoice leaderChoice = new ListChoice("leader", new PropertyModel<User>(this, "user"), new ArrayList<User>());
        leaderChoice.setChoiceRenderer(new LeaderChoices());
        leaderChoice.setNullValid(false);
        leaderChoice.setRequired(true);
        wmc.add(leaderChoice);
        wmc.setOutputMarkupId(true);
        createGroupForm.add(wmc);

        final TextField<String> groupLeaderNameTF = new TextField<String>("leaderTF", new PropertyModel<String>(this, "leaderName"));
        OnChangeAjaxBehavior onChangeBehavior = new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (leaderName != null && leaderName.length() > 4) {
                    List<User> users = userManager.findUsersByName(leaderName);
                    leaderChoice.setChoices(users);
                }
                if (target != null) {
                    target.add(wmc);
                }
            }
        };
        groupLeaderNameTF.add(onChangeBehavior);
        createGroupForm.add(groupLeaderNameTF);

        add(createGroupForm);
    }

    private static class GroupNameChoices implements IChoiceRenderer<Group> {

        @Override
        public Object getDisplayValue(Group group) {
            return group.getName();
        }

        @Override
        public String getIdValue(Group group, int index) {
            return group.getId().toString();
        }
    }

    private static class LeaderChoices implements IChoiceRenderer<User> {

        @Override
        public Object getDisplayValue(User user) {
            return user.getFullName() + " [" + user.getEmailAddress() + ", " + user.getNeptunCode() + "]";
        }

        @Override
        public String getIdValue(User user, int index) {
            return user.getId().toString();
        }
    }
}
