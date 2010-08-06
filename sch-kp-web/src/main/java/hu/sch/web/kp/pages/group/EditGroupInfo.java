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
package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.web.kp.templates.KorokPageTemplate;
import hu.sch.web.wicket.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.wicket.behaviors.ValidationStyleBehavior;
import hu.sch.domain.util.PatternHolder;
import java.util.Calendar;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.apache.wicket.validation.validator.UrlValidator;

/**
 *
 * @author aldaris
 */
public class EditGroupInfo extends KorokPageTemplate {

    private Long id;
    private Group group;

    public EditGroupInfo(PageParameters parameters) {
        Object p = parameters.get("id");
        try {
            id = Long.parseLong(p.toString());
        } catch (NumberFormatException e) {
            getSession().error("Érvénytelen paraméter");
            throw new RestartResponseException(GroupHierarchy.class);
        }
        setHeaderLabelText("Kör adatlap szerkesztése");

        group = userManager.findGroupById(id);
        User user = userManager.findUserWithMembershipsById(getSession().getUserId());
        if (user == null || !isUserGroupLeader(group)) {
            getSession().error(getLocalizer().getString("err.NincsJog", this));
            throw new RestartResponseException(ShowGroup.class, new PageParameters("id=" + id.toString()));
        }
        IModel<Group> model = new CompoundPropertyModel<Group>(group);
        Form<Group> editInfoForm = new Form<Group>("editInfoForm", model) {

            @Override
            protected void onSubmit() {
                super.onSubmit();
                try {
                    userManager.groupInfoUpdate(group);
                    getSession().info(getLocalizer().getString("info.AdatlapMentve", this));
                } catch (Exception ex) {
                    getSession().error(getLocalizer().getString("err.AdatlapFailed", this));
                }
                setResponsePage(ShowGroup.class, new PageParameters("id=" + id.toString()));
                return;
            }
        };

        RequiredTextField<String> nameTF = new RequiredTextField<String>("name");
        nameTF.add(StringValidator.lengthBetween(2, 255));
        nameTF.add(new PatternValidator(PatternHolder.GROUP_NAME_OR_POSTTYPE_PATTERN));
        nameTF.add(new ValidationStyleBehavior());
        editInfoForm.add(nameTF);
        nameTF.setLabel(new Model<String>("Név *"));
        editInfoForm.add(new ValidationSimpleFormComponentLabel("nameLabel", nameTF));

        TextField<Integer> foundedTF = new TextField<Integer>("founded");
        foundedTF.add(new RangeValidator<Integer>(1960, Calendar.getInstance().get(Calendar.YEAR)));
        foundedTF.add(new ValidationStyleBehavior());
        editInfoForm.add(foundedTF);
        foundedTF.setLabel(new Model<String>("Alapítás éve"));
        editInfoForm.add(new ValidationSimpleFormComponentLabel("foundedLabel", foundedTF));

        TextField<String> webPageTF = new TextField<String>("webPage");
        webPageTF.add(new UrlValidator());
        webPageTF.add(new ValidationStyleBehavior());
        editInfoForm.add(webPageTF);
        webPageTF.setLabel(new Model<String>("Weboldal"));
        editInfoForm.add(new ValidationSimpleFormComponentLabel("webPageLabel", webPageTF));

        TextField<String> mailingListTF = new TextField<String>("mailingList");
        mailingListTF.add(EmailAddressValidator.getInstance());
        mailingListTF.add(new ValidationStyleBehavior());
        editInfoForm.add(mailingListTF);
        mailingListTF.setLabel(new Model<String>("Levelezőlista"));
        editInfoForm.add(new ValidationSimpleFormComponentLabel("mailingListLabel", mailingListTF));

        TextArea<String> introductionTA = new TextArea<String>("introduction");
        editInfoForm.add(introductionTA);
        introductionTA.setLabel(new Model<String>("Bemutatkozás"));
        editInfoForm.add(new SimpleFormComponentLabel("introductionLabel", introductionTA));

        CheckBox usersCanApplyCB = new CheckBox("usersCanApply");
        editInfoForm.add(usersCanApplyCB);
        usersCanApplyCB.setLabel(new Model<String>("Felhasználók jelentkezhetnek a körbe"));
        editInfoForm.add(new SimpleFormComponentLabel("usersCanApplyLabel", usersCanApplyCB));

        add(editInfoForm);
    }
}
