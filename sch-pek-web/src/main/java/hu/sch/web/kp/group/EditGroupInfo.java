package hu.sch.web.kp.group;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import hu.sch.domain.util.PatternHolder;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.wicket.behaviors.ValidationStyleBehavior;
import hu.sch.web.wicket.components.ValidationSimpleFormComponentLabel;
import java.util.Calendar;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.*;

/**
 *
 * @author aldaris
 */
public class EditGroupInfo extends KorokPage {

    private Long id;
    private Group group;

    public EditGroupInfo(PageParameters parameters) {
        try {
            id = parameters.get("id").toLong();
        } catch (NumberFormatException e) {
            getSession().error("Érvénytelen paraméter");
            throw new RestartResponseException(GroupHierarchy.class);
        }
        setHeaderLabelText("Kör adatlap szerkesztése");

        group = userManager.findGroupById(id);
        User user = userManager.findUserWithMembershipsById(getSession().getUserId());
        if (user == null || !isUserGroupLeader(group)) {
            getSession().error(getLocalizer().getString("err.NincsJog", this));
            throw new RestartResponseException(ShowGroup.class, new PageParameters().add("id", id.toString()));
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
                setResponsePage(ShowGroup.class, new PageParameters().add("id", id.toString()));
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
        foundedTF.add(new RangeValidator<Integer>(1950, Calendar.getInstance().get(Calendar.YEAR)));
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
