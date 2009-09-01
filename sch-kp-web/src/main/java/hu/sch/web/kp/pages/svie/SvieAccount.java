/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.svie;

import hu.sch.domain.Membership;
import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.User;
import hu.sch.web.components.customlinks.SvieRegPdfLink;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 *
 * @author aldaris
 */
public final class SvieAccount extends SecuredPageTemplate {

    private static Logger log = Logger.getLogger(SvieAccount.class);
    private final User user = getUser();

    public SvieAccount() {
        //ha még nem SVIE tag, akkor továbbítjuk a SVIE regisztrációs oldalra.
        if (user == null) {
            getSession().error("A SVIE tagsághoz először létre kell hoznod egy közösségi profilt");
            throw new RestartResponseException(getApplication().getHomePage());
        }
        if (user.getSvieMembershipType().equals(SvieMembershipType.NEMTAG)) {
            throw new RestartResponseAtInterceptPageException(new SvieRegistration(user));
        }

        setHeaderLabelText("SVIE tagság beállításai");

        add(new FeedbackPanel("pagemessages"));
        WebMarkupContainer wmc = new WebMarkupContainer("primarySelector");
        wmc.add(new Label("formLabel", "Elsődleges kör kiválasztása"));
        Form<User> form = new Form<User>("form") {

            @Override
            protected void onSubmit() {
                if (user.getSviePrimaryMembership() == null) {
                } else {
                    userManager.updateUser(user);
                    getSession().info("Elsődleges kör sikeresen kiválasztva");
                    setResponsePage(SvieAccount.class);
                }
            }
        };
        form.setModel(new CompoundPropertyModel<User>(user));

        IModel<List<Membership>> groupNames = new LoadableDetachableModel<List<Membership>>() {

            @Override
            protected List<Membership> load() {
                return userManager.getSvieMembershipsForUser(user);
            }
        };

        ListChoice listChoice = new ListChoice("sviePrimaryMembership", groupNames);
        listChoice.setChoiceRenderer(new GroupNameChoices());
        listChoice.setNullValid(false);
        form.add(listChoice);
        wmc.add(form);
        add(wmc);

        if (!user.getSvieMembershipType().equals(SvieMembershipType.RENDESTAG)) {
            wmc.setVisible(false);
        }
        add(new SvieRegPdfLink<User>("pdfLink", user));
    }

    private class GroupNameChoices implements IChoiceRenderer<Object> {

        public Object getDisplayValue(Object object) {
            Membership ms = (Membership) object;
            return ms.getGroup().getName();
        }

        public String getIdValue(Object object, int index) {
            Membership ms = (Membership) object;
            return ms.getId().toString();
        }
    }
}
