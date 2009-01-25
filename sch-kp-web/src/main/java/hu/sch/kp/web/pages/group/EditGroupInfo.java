/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.group;

import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.pages.user.ShowUser;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;

/**
 *
 * @author aldaris
 */
public class EditGroupInfo extends SecuredPageTemplate {

    Long id;

    public EditGroupInfo(PageParameters parameters) {
        Object p = parameters.get("id");
        try {
            id = Long.parseLong(p.toString());
        } catch (NumberFormatException e) {
            setResponsePage(Index.class);
        }
        setHeaderLabelText("Csoport adatlap szerkesztése");

        Form editInfoForm = new Form("editInfoForm") {

            @Override
            protected void onSubmit() {
                setResponsePage(ShowUser.class);
            }
        };
        //editInfoForm.add(new Label("valami").set);
        add(editInfoForm);
    }
}
