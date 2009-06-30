/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.domain.MembershipType;
import hu.sch.web.kp.pages.index.Index;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.components.ValidationSimpleFormComponentLabel;
import hu.sch.web.components.ValidationStyleBehavior;
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
import org.apache.wicket.validation.validator.NumberValidator;
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
        User user = userManager.findUserWithCsoporttagsagokById((getSession()).getUser().getId());
        if (user == null || !hasUserRoleInGroup(group, MembershipType.KORVEZETO)) {
            getSession().error(getLocalizer().getString("err.NincsJog", this));
            throw new RestartResponseException(ShowGroup.class, new PageParameters("id=" + id.toString()));
        }
        IModel model = new CompoundPropertyModel(group);
        Form editInfoForm = new Form("editInfoForm", model) {

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

        RequiredTextField nameTF = new RequiredTextField("name");
        nameTF.add(StringValidator.lengthBetween(2, 255));
        nameTF.add(new PatternValidator("[^|:]*"));
        nameTF.add(new ValidationStyleBehavior());
        editInfoForm.add(nameTF);
        nameTF.setLabel(new Model("Név *"));
        editInfoForm.add(new ValidationSimpleFormComponentLabel("nameLabel", nameTF));

        TextField<Integer> foundedTF = new TextField<Integer>("founded");
        foundedTF.add(new RangeValidator(1960, Calendar.getInstance().get(java.util.Calendar.YEAR)));
        foundedTF.add(new ValidationStyleBehavior());
        editInfoForm.add(foundedTF);
        foundedTF.setLabel(new Model("Alapítás éve"));
        editInfoForm.add(new ValidationSimpleFormComponentLabel("foundedLabel", foundedTF));

        TextField webPageTF = new TextField("webPage");
        webPageTF.add(new UrlValidator());
        webPageTF.add(new ValidationStyleBehavior());
        editInfoForm.add(webPageTF);
        webPageTF.setLabel(new Model("Weboldal"));
        editInfoForm.add(new ValidationSimpleFormComponentLabel("webPageLabel", webPageTF));

        TextField mailingListTF = new TextField("mailingList");
        mailingListTF.add(EmailAddressValidator.getInstance());
        mailingListTF.add(new ValidationStyleBehavior());
        editInfoForm.add(mailingListTF);
        mailingListTF.setLabel(new Model("Levelezőlista"));
        editInfoForm.add(new ValidationSimpleFormComponentLabel("mailingListLabel", mailingListTF));

        TextArea introductionTA = new TextArea("introduction");
        editInfoForm.add(introductionTA);
        introductionTA.setLabel(new Model("Bemutatkozás"));
        editInfoForm.add(new SimpleFormComponentLabel("introductionLabel", introductionTA));

        add(editInfoForm);
    }
}
