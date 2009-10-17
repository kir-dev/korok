/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.svie;

import hu.sch.domain.Membership;
import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.SvieStatus;
import hu.sch.domain.User;
import hu.sch.services.SvieManagerLocal;
import hu.sch.web.components.ConfirmationBoxRenderer;
import hu.sch.web.components.customlinks.SvieRegPdfLink;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.util.List;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 *
 * @author aldaris
 */
public final class SvieAccount extends SecuredPageTemplate {

    @EJB(name = "SvieManagerBean")
    SvieManagerLocal svieManager;
    private static Logger log = Logger.getLogger(SvieAccount.class);
    private final User user = getUser();

    public SvieAccount() {

        createNavbarWithSupportId(34);
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

        add(new Label("sviestatusLabel", user.getSvieStatus().toString()));
        OrdinalFragment ordFragment = new OrdinalFragment("ordinalFragment", "ordinalPanel");
        AdvocateFragment advFragment = new AdvocateFragment("advocateFragment", "advocatePanel");
        ordFragment.setVisible(false);
        advFragment.setVisible(false);
        add(ordFragment, advFragment);

        if (user.getSvieMembershipType().equals(SvieMembershipType.RENDESTAG)) {
            ordFragment.setVisible(true);
        } else if (user.getSvieMembershipType().equals(SvieMembershipType.PARTOLOTAG)) {
            advFragment.setVisible(true);
        }

        SvieRegPdfLink regPdfLink = new SvieRegPdfLink("pdfLink", user);
        if (!user.getSvieStatus().equals(SvieStatus.FELDOLGOZASALATT)) {
            regPdfLink.setVisible(false);
        }
        add(regPdfLink);

        Link<Void> deleteSvieMs = new Link<Void>("deleteSvieMembership") {

            @Override
            public void onClick() {
                svieManager.endMembership(user);
                getSession().info("A SVIE tagságodat sikeresen megszüntetted");
                setResponsePage(getApplication().getHomePage());
            }
        };
        deleteSvieMs.add(new ConfirmationBoxRenderer("Biztosan meg szeretnéd szüntetni a SVIE tagságod?"));
        add(deleteSvieMs);
    }

    private class OrdinalFragment extends Fragment {

        public OrdinalFragment(String id, String markupId) {
            super(id, markupId, null, null);
            add(new Label("formLabel", "Elsődleges kör kiválasztása"));
            Form<User> form = new Form<User>("form") {

                @Override
                protected void onSubmit() {
                    if (user.getSviePrimaryMembership() != null) {
                        svieManager.updatePrimaryMembership(user);
                        getSession().info("Elsődleges kör sikeresen kiválasztva");
                        setResponsePage(SvieAccount.class);
                    }
                }
            };
            form.setModel(new CompoundPropertyModel<User>(user));

            IModel<List<Membership>> groupNames = new LoadableDetachableModel<List<Membership>>() {

                @Override
                protected List<Membership> load() {
                    return svieManager.getSvieMembershipsForUser(user);
                }
            };

            ListChoice listChoice = new ListChoice("sviePrimaryMembership", groupNames);
            listChoice.setChoiceRenderer(new GroupNameChoices());
            listChoice.setNullValid(false);
            listChoice.setRequired(true);
            form.add(listChoice);
            add(form);

            Link<Void> ordinalToAdvocate = new Link<Void>("ordinalToAdvocate") {

                @Override
                public void onClick() {
                    svieManager.OrdinalToAdvocate(user);
                    getSession().info("Sikeresen pártoló taggá váltál");
                    setResponsePage(SvieAccount.class);
                }
            };
            ordinalToAdvocate.add(new ConfirmationBoxRenderer("Biztosan pártoló taggá szeretnél válni?"));
            add(ordinalToAdvocate);
            if (!user.getSvieStatus().equals(SvieStatus.ELFOGADVA)) {
                ordinalToAdvocate.setVisible(false);
            }
        }
    }

    private class AdvocateFragment extends Fragment {

        public AdvocateFragment(String id, String markupId) {
            super(id, markupId, null, null);

            List<Membership> ms = svieManager.getSvieMembershipsForUser(user);
            Link<Void> advocateToOrdinal = new Link<Void>("advocateToOrdinal") {

                @Override
                public void onClick() {
                    svieManager.advocateToOrdinal(user);
                    getSession().info("Rendes taggá válás kezdeményezése sikeresen megtörtént");
                    setResponsePage(SvieAccount.class);
                }
            };
            if (ms.isEmpty()) {
                advocateToOrdinal.setVisible(false);
            }
            add(advocateToOrdinal);
        }
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
