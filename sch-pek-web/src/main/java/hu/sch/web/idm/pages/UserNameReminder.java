package hu.sch.web.idm.pages;

import hu.sch.domain.profile.Person;
import hu.sch.services.MailManagerLocal;
import hu.sch.web.PhoenixApplication;
import hu.sch.web.kp.KorokPage;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;

/**
 *
 * @author aldaris
 */
public class UserNameReminder extends KorokPage {

    @EJB(name = "MailManagerBean")
    private MailManagerLocal mailManager;
    public String mail;

    public UserNameReminder() {
        setHeaderLabelText("Felhasználói név emlékeztető");
        if (getRemoteUser() != null) {
            getSession().error(getLocalizer().getString("err.ReminderAlreadySignedIn", this));
            throw new RestartResponseException(getApplication().getHomePage());
        }

        StatelessForm<Void> reminderForm = new StatelessForm<Void>("reminderForm") {

            @Override
            protected void onSubmit() {
                List<Person> results = ldapManager.searchMyUid(mail);
                if (results.isEmpty()) {
                    getSession().error(getLocalizer().getString("err.NoSuchEmail", this));
                    return;
                } else if (results.size() > 1) {
                    getSession().error(getLocalizer().getString("err.DuplicatedUsers", this));
                    return;
                } else {
                    Person person = results.get(0);
                    try {
                        StringBuilder msg = new StringBuilder(200);
                        if (((PhoenixApplication) getApplication()).isNewbieTime()) {
                            msg.append("Tisztelt ");
                        } else {
                            msg.append("Kedves ");
                        }
                        msg.append(person.getFirstName()).append("!\n\n").append("Ehhez az e-mail címhez a következő felhasználói név van regisztrálva a rendszerben: '");
                        msg.append(person.getUid()).append("'.\n\nÜdv,\nKir-Dev");

                        mailManager.sendEmail(mail, "Felhasználói név emlékeztető", msg.toString());
                    } catch (Exception e) {
                        getSession().error(getLocalizer().getString("err.MailError", this));
                        return;
                    }
                    getSession().info(getLocalizer().getString("info.ReminderSent", this));
                    return;
                }
            }
        };
        RequiredTextField<String> mailTF = new RequiredTextField<String>("mail", new PropertyModel<String>(this, "mail"));
        mailTF.add(EmailAddressValidator.getInstance());
        reminderForm.add(mailTF);
        add(reminderForm);
    }
}
