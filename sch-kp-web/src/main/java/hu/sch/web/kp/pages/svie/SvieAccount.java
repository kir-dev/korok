/**
 * Copyright (c) 2009, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
