/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.group;

import hu.sch.domain.Csoport;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.web.components.ValidationSimpleFormComponentLabel;
import hu.sch.kp.web.components.ValidationStyleBehavior;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.session.VirSession;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.Calendar;
import org.apache.wicket.PageParameters;
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
import org.apache.wicket.validation.validator.StringValidator.LengthBetweenValidator;
import org.apache.wicket.validation.validator.UrlValidator;

/**
 *
 * @author aldaris
 */
public class EditGroupInfo extends SecuredPageTemplate {

    private Long id;
    private Csoport csoport;

    public EditGroupInfo(PageParameters parameters) {
        Object p = parameters.get("id");
        try {
            id = Long.parseLong(p.toString());
        } catch (NumberFormatException e) {
            setResponsePage(Index.class);
        }
        setHeaderLabelText("Csoport adatlap szerkesztése");
        add(new FeedbackPanel("pagemessages"));

        csoport = userManager.findGroupById(id);
        Felhasznalo user = userManager.findUserWithCsoporttagsagokById(((VirSession) getSession()).getUser().getId());
        if (true || user == null || !hasUserRoleInGroup(csoport, TagsagTipus.KORVEZETO)) {
            getSession().error(getLocalizer().getString("err.NincsJog", this));
            setResponsePage(ShowGroup.class, new PageParameters("id=" + id.toString()));
            return;
        }
        IModel model = new CompoundPropertyModel(csoport);
        Form editInfoForm = new Form("editInfoForm", model) {

            @Override
            protected void onSubmit() {
                super.onSubmit();
                try {
                    userManager.groupInfoUpdate(csoport);
                    getSession().info(getLocalizer().getString("info.AdatlapMentve", this));
                } catch (Exception ex) {
                    getSession().error(getLocalizer().getString("err.AdatlapFailed", this));
                }
                setResponsePage(ShowGroup.class, new PageParameters("id=" + id.toString()));
                return;
            }
        };

        RequiredTextField nevTF = new RequiredTextField("nev");
        nevTF.add(LengthBetweenValidator.lengthBetween(2, 255));
        nevTF.add(new ValidationStyleBehavior());
        editInfoForm.add(nevTF);
        nevTF.setLabel(new Model("Név *"));
        editInfoForm.add(new ValidationSimpleFormComponentLabel("nevLabel", nevTF));

        TextField alapEvTF = new TextField("alapitasEve", Integer.class);
        alapEvTF.add(new NumberValidator.RangeValidator(1960, Calendar.getInstance().get(java.util.Calendar.YEAR)));
        alapEvTF.add(new ValidationStyleBehavior());
        editInfoForm.add(alapEvTF);
        alapEvTF.setLabel(new Model("Alapítás éve"));
        editInfoForm.add(new ValidationSimpleFormComponentLabel("alapevLabel", alapEvTF));

        TextField webPageTF = new TextField("webpage");
        webPageTF.add(new UrlValidator());
        webPageTF.add(new ValidationStyleBehavior());
        editInfoForm.add(webPageTF);
        webPageTF.setLabel(new Model("Weboldal"));
        editInfoForm.add(new ValidationSimpleFormComponentLabel("webpageLabel", webPageTF));

        TextField levlistTF = new TextField("levelezoLista");
        levlistTF.add(EmailAddressValidator.getInstance());
        levlistTF.add(new ValidationStyleBehavior());
        editInfoForm.add(levlistTF);
        levlistTF.setLabel(new Model("Levelezőlista"));
        editInfoForm.add(new ValidationSimpleFormComponentLabel("levlistaLabel", levlistTF));

        TextArea descriptionTA = new TextArea("leiras");
        editInfoForm.add(descriptionTA);
        descriptionTA.setLabel(new Model("Bemutatkozás"));
        editInfoForm.add(new SimpleFormComponentLabel("descrLabel", descriptionTA));

        add(editInfoForm);
    }
}
