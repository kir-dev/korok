/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.domain.MembershipType;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.components.ValidationStyleBehavior;
import hu.sch.web.kp.util.PatternHolder;
import java.util.Calendar;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
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
public class EditGroupInfo extends SecuredPageTemplate {

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
        add(new FeedbackPanel("pagemessages"));

        group = userManager.findGroupById(id);
        User user = userManager.findUserWithCsoporttagsagokById((getSession()).getUserId());
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
        nameTF.add(new PatternValidator(PatternHolder.groupNameOrPostTypePattern));
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

        add(editInfoForm);
    }
}
