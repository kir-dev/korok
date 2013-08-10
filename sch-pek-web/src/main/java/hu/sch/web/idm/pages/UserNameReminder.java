package hu.sch.web.idm.pages;

import hu.sch.services.exceptions.PekEJBException;
import hu.sch.web.kp.KorokPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;

/**
 *
 * @author aldaris
 */
public class UserNameReminder extends KorokPage {

    private String mail;

    public UserNameReminder() {
        setHeaderLabelText("Felhasználói név emlékeztető");
        if (getRemoteUser() != null) {
            getSession().error(getLocalizer().getString("err.ReminderAlreadySignedIn", this));
            throw new RestartResponseException(getApplication().getHomePage());
        }

        StatelessForm<Void> reminderForm = new StatelessForm<Void>("reminderForm") {
            @Override
            protected void onSubmit() {
                try {
                    if (userManager.sendUserNameReminder(mail)) {
                        info(getString("info.ReminderSent"));
                    } else {
                        error(getString("err.MailError"));
                    }
                } catch (PekEJBException ex) {
                    error(new StringResourceModel(ex.getErrorCode().getMessageKey(), null, ex.getParameters()));
                }
            }
        };

        final RequiredTextField<String> mailTF =
                new RequiredTextField<>("mail", new PropertyModel<String>(this, "mail"));
        mailTF.add(EmailAddressValidator.getInstance());
        reminderForm.add(mailTF);
        add(reminderForm);
    }
}
