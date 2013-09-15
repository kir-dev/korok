package hu.sch.web.kp.svie;

import hu.sch.domain.Membership;
import hu.sch.domain.enums.SvieMembershipType;
import hu.sch.domain.enums.SvieStatus;
import hu.sch.domain.user.User;
import hu.sch.services.SvieManagerLocal;
import hu.sch.web.wicket.behaviors.ConfirmationBehavior;
import hu.sch.web.wicket.components.customlinks.SvieRegPdfLink;
import hu.sch.web.kp.KorokPage;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
public final class SvieAccount extends KorokPage {

    @Inject
    SvieManagerLocal svieManager;
    private static Logger log = LoggerFactory.getLogger(SvieAccount.class);
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

        Fragment reginfoFragment = new Fragment("reginfoFragment", "reginfoPanel", null, null);
        reginfoFragment.setVisible(true);

        SvieRegPdfLink regPdfLink = new SvieRegPdfLink("pdfLink", user);
        if (!user.getSvieStatus().equals(SvieStatus.FELDOLGOZASALATT)) {
            regPdfLink.setVisible(false);
            reginfoFragment.setVisible(false);
        }
        add(regPdfLink);
        add(reginfoFragment);

        Link<Void> deleteSvieMs = new Link<Void>("deleteSvieMembership") {

            @Override
            public void onClick() {
                svieManager.endMembership(user);
                getSession().info("A SVIE tagságodat sikeresen megszüntetted");
                setResponsePage(getApplication().getHomePage());
            }
        };
        deleteSvieMs.add(new ConfirmationBehavior("Biztosan meg szeretnéd szüntetni a SVIE tagságod?"));
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
                    svieManager.ordinalToAdvocate(user);
                    getSession().info("Sikeresen pártoló taggá váltál");
                    setResponsePage(SvieAccount.class);
                }
            };
            ordinalToAdvocate.add(new ConfirmationBehavior("Biztosan pártoló taggá szeretnél válni?"));
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

    private static class GroupNameChoices implements IChoiceRenderer<Membership> {

        @Override
        public Object getDisplayValue(Membership ms) {
            return ms.getGroup().getName();
        }

        @Override
        public String getIdValue(Membership ms, int index) {
            return ms.getId().toString();
        }
    }
}
