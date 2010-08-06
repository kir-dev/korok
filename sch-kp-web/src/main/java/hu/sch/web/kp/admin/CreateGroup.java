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
package hu.sch.web.kp.admin;

import hu.sch.domain.Group;
import hu.sch.domain.GroupStatus;
import hu.sch.domain.User;
import hu.sch.web.wicket.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.wicket.behaviors.ValidationStyleBehavior;
import hu.sch.web.error.NotFound;
import hu.sch.web.kp.KorokPageTemplate;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public class CreateGroup extends KorokPageTemplate {

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
                    userManager.createNewGroupWithLeader(group, user);
                } catch (Exception ex) {
                    getSession().error("Hiba a kör létrehozásakor: " + ex.getMessage());
                    setResponsePage(CreateGroup.class);
                    return;
                }
                getSession().info("A kör sikeresen létrehozva.");
                setResponsePage(CreateGroup.class);
                return;
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

        List<Group> groups = userManager.getAllGroups();

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
                    List<User> users = userManager.searchForUserByName(leaderName);
                    leaderChoice.setChoices(users);
                }
                if (target != null) {
                    target.addComponent(wmc);
                }
            }
        };
        groupLeaderNameTF.add(onChangeBehavior);
        createGroupForm.add(groupLeaderNameTF);

        add(createGroupForm);
    }

    public class GroupNameChoices implements IChoiceRenderer<Group> {

        @Override
        public Object getDisplayValue(Group group) {
            return group.getName();
        }

        @Override
        public String getIdValue(Group group, int index) {
            return group.getId().toString();
        }
    }

    public class LeaderChoices implements IChoiceRenderer<User> {

        @Override
        public Object getDisplayValue(User user) {
            return user.getName() + " [" + user.getEmailAddress() + ", " + user.getNeptunCode() + "]";
        }

        @Override
        public String getIdValue(User user, int index) {
            return user.getId().toString();
        }
    }
}
