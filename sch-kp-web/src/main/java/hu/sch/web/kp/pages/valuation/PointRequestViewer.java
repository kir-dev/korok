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
package hu.sch.web.kp.pages.valuation;

import hu.sch.domain.ConsideredValuation;
import hu.sch.domain.PointRequest;
import hu.sch.domain.User;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationStatus;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.wicket.util.ListDataProviderCompoundPropertyModelImpl;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.pages.consider.ConsiderExplainPage;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class PointRequestViewer extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ValuationManagerLocal ertekelesManager;

    public PointRequestViewer(final Valuation val) {

        setHeaderLabelText("Kiosztott pontok");
        final List<PointRequest> pointRequests = prepareRequests(val);

        setDefaultModel(new CompoundPropertyModel<Valuation>(val));
        add(new Label("group.name"));
        add(new Label("semester"));

        IDataProvider<PointRequest> provider = new ListDataProviderCompoundPropertyModelImpl<PointRequest>(pointRequests);
        DataView<PointRequest> dview = new DataView<PointRequest>("requests", provider) {

            @Override
            protected void populateItem(Item<PointRequest> item) {
                final PointRequest p = item.getModelObject();
                Link felhasznaloLink = new Link("userLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(ShowUser.class,
                                new PageParameters("id=" + p.getUser().getId().toString()));
                    }
                };
                Label nicknameLabel = new Label("user.nickName");
                item.add(nicknameLabel);
                felhasznaloLink.add(new Label("user.name"));
                item.add(felhasznaloLink);
                item.add(new Label("point"));
            }
        };
//        Form considerForm = new ConsiderForm("considerForm") {
//
//            @Override
//            public void doSave() {
//                super.doSave();
//                List<ElbiraltErtekeles> list = new ArrayList<ElbiraltErtekeles>();
//                setResponsePage(new ElbiralasIndoklas(list));
//            }
//
//            @Override
//            public void doRefuse() {
//                super.doRefuse();
//            }
//        };

        if (isCurrentUserJETI()) {
            Fragment jetifragment = new JETIFragment("jetifragment", "jetipanel", val);
            add(jetifragment);
        } else {
            add(new Label("jetifragment", ""));
        }
        add(dview);
//        add(considerForm);

    }

    private List<PointRequest> prepareRequests(Valuation ert) {
        List<User> activeMembers = userManager.getCsoporttagokWithoutOregtagok(ert.getGroup().getId());
        List<PointRequest> requests = ertekelesManager.findPontIgenyekForErtekeles(ert.getId());

        //tagok és igények összefésülése
        if (requests.isEmpty()) {
            for (User f : activeMembers) {
                requests.add(new PointRequest(f, 0));
            }
        }
        return requests;
    }

    private class JETIFragment extends Fragment {

        public JETIFragment(String id, String markupId, final Valuation val) {
            super(id, markupId, null, null);

            Link acceptLink = new Link("accept") {

                @Override
                public void onClick() {
                    List<ConsideredValuation> list = new ArrayList<ConsideredValuation>();
                    ConsideredValuation cv = new ConsideredValuation(val, ValuationStatus.ELFOGADVA, val.getEntrantStatus());
                    list.add(cv);
                    setResponsePage(new ConsiderExplainPage(list));
                }
            };

            Link rejectLink = new Link("reject") {

                @Override
                public void onClick() {
                    List<ConsideredValuation> list = new ArrayList<ConsideredValuation>();
                    ConsideredValuation cv = new ConsideredValuation(val, ValuationStatus.ELUTASITVA, val.getEntrantStatus());
                    list.add(cv);
                    setResponsePage(new ConsiderExplainPage(list));
                }
            };

            add(acceptLink);
            add(rejectLink);
        }
    }
}

