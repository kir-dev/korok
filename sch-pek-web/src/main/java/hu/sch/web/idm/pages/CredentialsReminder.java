package hu.sch.web.idm.pages;

import hu.sch.services.AccountManager;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.web.kp.KorokPage;
import javax.inject.Inject;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.EmailAddressValidator;

/**
 * Page for password and username reminder.
 *
 * @author aldaris
 * @author balo
 */
public class CredentialsReminder extends KorokPage {

    //
    public static final String PAGE_PARAM = "p";
    @Inject
    protected AccountManager accountManager;
    private final CredentialReminderType currentPage;
    //
    private String mail;

    public CredentialsReminder() {
        this(new PageParameters().add(PAGE_PARAM, CredentialReminderType.USERNAME.lowercase()));
    }

    public CredentialsReminder(final PageParameters params) {
        if (getRemoteUser() != null) {
            getSession().error(getString("err.ReminderAlreadySignedIn"));
            throw new RestartResponseException(getApplication().getHomePage());
        }

        CredentialReminderType p;
        try {
            p = CredentialReminderType.valueOf(params.get(PAGE_PARAM).toString().toUpperCase());
        } catch (IllegalArgumentException ex) {
            p = CredentialReminderType.USERNAME; //default
        }

        currentPage = p;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setHeaderLabelText(getString("headerLabel." + currentPage.lowercase()));
        add(new Label("notifyLabel", getString("notifyLabel." + currentPage.lowercase())));

        Form<Void> reminderForm = new StatelessForm<Void>("reminderForm") {
            @Override
            protected void onSubmit() {
                try {
                    if (sendReminder(currentPage)) {
                        info(getString("info.ReminderSent"));
                    } else {
                        error(getString("err.MailError"));
                    }
                } catch (PekEJBException ex) {
                    parametrizedErrorMessage(ex);
                }
            }
        };

        final TextField<String> mailTF
                = new RequiredTextField<>("mail", new PropertyModel<String>(this, "mail"));
        mailTF.add(EmailAddressValidator.getInstance());
        reminderForm.add(mailTF);
        add(reminderForm);
    }

    private boolean sendReminder(final CredentialReminderType currentPage) throws PekEJBException {
        switch (currentPage) {
            case PASSWORD:
                return accountManager.sendLostPasswordChangeLink(mail);
            default:
                return accountManager.sendUserNameReminder(mail);
        }
    }

    @Override
    protected boolean needsLogin() {
        return false;
    }
}
