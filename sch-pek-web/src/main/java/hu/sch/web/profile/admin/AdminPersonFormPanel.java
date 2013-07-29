package hu.sch.web.profile.admin;

import hu.sch.domain.user.StudentStatus;
import hu.sch.domain.user.User;
import hu.sch.domain.user.UserStatus;
import hu.sch.util.PatternHolder;
import hu.sch.web.profile.edit.PersonFormPanel;
import hu.sch.web.wicket.behaviors.ValidationStyleBehavior;
import hu.sch.web.wicket.components.ValidationSimpleFormComponentLabel;
import java.util.Arrays;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 *
 * @author tomi
 */
public class AdminPersonFormPanel extends PersonFormPanel {

    public AdminPersonFormPanel(String id, User user) {
        super(id, user);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final TextField neptunTF = (TextField) new TextField("neptunCode").setRequired(false);
        neptunTF.add(new ValidationStyleBehavior());
        form.add(neptunTF);
        neptunTF.setLabel(Model.of("Neptun"));
        form.add(new ValidationSimpleFormComponentLabel("neptunInputLabel", neptunTF));

        DropDownChoice<UserStatus> userStatusChoice =
                new DropDownChoice<>("userStatus", Arrays.asList(UserStatus.values()));
        userStatusChoice.setChoiceRenderer(new EnumChoiceRenderer<UserStatus>(this));
        form.add(userStatusChoice);

        userStatusChoice.setLabel(Model.of("Státusz"));
        form.add(new SimpleFormComponentLabel("statusLabel", userStatusChoice));

        final DropDownChoice<StudentStatus> studentStatusChoice =
                new DropDownChoice<>("studentStatus", Arrays.asList(StudentStatus.values()));
        studentStatusChoice.setChoiceRenderer(new EnumChoiceRenderer<StudentStatus>(this));
        form.add(studentStatusChoice);
        
        studentStatusChoice.setLabel(Model.of("Hallgatói státusz"));
        form.add(new SimpleFormComponentLabel("studentStatusLabel", studentStatusChoice));

        // a neptun kód ne legyen kötelező, ha a hallgatói státusz egyéb
        add(new AbstractFormValidator() {
            @Override
            public FormComponent<?>[] getDependentFormComponents() {
                return new FormComponent[]{neptunTF, studentStatusChoice};
            }

            @Override
            public void validate(Form<?> form) {
                if (neptunTF.getValue().isEmpty()
                        && studentStatusChoice.getValue().equals("active")) {
                    error(neptunTF, "admin.create.person.err.neptunNelkuliAktiv");
                }
            }
        });

        createSvieFields();
    }

    protected void createSvieFields() {
        TextField<String> mothersNameTF = new TextField<>("mothersName");
        mothersNameTF.add(new PatternValidator(PatternHolder.NAME_PATTERN));
        mothersNameTF.add(new ValidationStyleBehavior());
        form.add(mothersNameTF);
        mothersNameTF.setLabel(Model.of("Anyja neve"));
        form.add(new ValidationSimpleFormComponentLabel("mothersNameLabel", mothersNameTF));

        TextField<String> estGradTF = new TextField<>("estimatedGraduationYear");
        estGradTF.add(new PatternValidator(PatternHolder.GRADUATION_YEAR_PATTERN));
        estGradTF.add(new ValidationStyleBehavior());
        form.add(estGradTF);
        estGradTF.setLabel(Model.of("Egyetem várható befejezési ideje"));
        form.add(new ValidationSimpleFormComponentLabel("estGradLabel", estGradTF));
    }
}
