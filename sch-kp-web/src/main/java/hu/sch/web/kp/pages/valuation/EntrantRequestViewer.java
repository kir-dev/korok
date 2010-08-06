/**
 * Copyright (c) 2009-2010, Peter Major
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
import hu.sch.domain.EntrantRequest;
import hu.sch.domain.EntrantType;
import hu.sch.domain.User;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationStatus;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.kp.templates.KorokPageTemplate;
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
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class EntrantRequestViewer extends KorokPageTemplate {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal ertekelesManager;

    public EntrantRequestViewer(final Valuation ert) {

        setHeaderLabelText("Kiosztott belépők");
        final List<EntrantRequest> igenylista = igenyeketElokeszit(ert);

        setDefaultModel(new CompoundPropertyModel<Valuation>(ert));
        add(new Label("group.name"));
        add(new Label("semester"));

        IDataProvider<EntrantRequest> provider = new ListDataProviderCompoundPropertyModelImpl<EntrantRequest>(igenylista);
        DataView<EntrantRequest> dview = new DataView<EntrantRequest>("requests", provider) {

            @Override
            protected void populateItem(Item<EntrantRequest> item) {
                final EntrantRequest b = item.getModelObject();
                Link felhasznaloLink = new Link("userLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(ShowUser.class,
                                new PageParameters("id=" + b.getUser().getId().toString()));
                    }
                };
                felhasznaloLink.add(new Label("userName", new PropertyModel<String>(b, "user.name")));
                item.add(felhasznaloLink);
                item.add(new Label("nickName", new PropertyModel<String>(b, "user.nickName")));
                item.add(new Label("entrantType"));
                item.add(new Label("valuationText"));
            }
        };

        add(dview);

        if (isCurrentUserJETI()) {
            Fragment jetifragment = new JETIFragment("jetifragment", "jetipanel", ert);
            add(jetifragment);
        } else {
            add(new Label("jetifragment", ""));
        }
    }

    private List<EntrantRequest> igenyeketElokeszit(Valuation ert) {
        List<User> csoporttagok = userManager.getCsoporttagokWithoutOregtagok(ert.getGroup().getId());
        List<EntrantRequest> igenyek = ertekelesManager.findBelepoIgenyekForErtekeles(ert.getId());

        //tagok és igények összefésülése
        if (igenyek.isEmpty()) {
            for (User f : csoporttagok) {
                igenyek.add(new EntrantRequest(f, EntrantType.KDO));
            }
        } else {

            //tényleges összefésülés
            boolean szerepel = false;
            if (igenyek.size() != csoporttagok.size()) {
                for (User csoporttag : csoporttagok) {
                    szerepel = false;
                    for (EntrantRequest igeny : igenyek) {
                        if (igeny.getUser().getId().equals(csoporttag.getId())) {
                            szerepel = true;
                            break;
                        }
                    }
                    if (!szerepel) {
                        igenyek.add(new EntrantRequest(csoporttag, EntrantType.KDO));
                    }
                }
            }
        }

        return igenyek;
    }

    private class JETIFragment extends Fragment {

        public JETIFragment(String id, String markupId, final Valuation val) {
            super(id, markupId, null, null);

            Link acceptLink = new Link("accept") {

                @Override
                public void onClick() {
                    List<ConsideredValuation> list = new ArrayList<ConsideredValuation>();
                    ConsideredValuation cv = new ConsideredValuation(val, val.getPointStatus(), ValuationStatus.ELFOGADVA);
                    list.add(cv);
                    setResponsePage(new ConsiderExplainPage(list));
                }
            };

            Link rejectLink = new Link("reject") {

                @Override
                public void onClick() {
                    List<ConsideredValuation> list = new ArrayList<ConsideredValuation>();
                    ConsideredValuation cv = new ConsideredValuation(val, val.getPointStatus(), ValuationStatus.ELUTASITVA);
                    list.add(cv);
                    setResponsePage(new ConsiderExplainPage(list));
                }
            };

            Link messagesLink = new Link("messages") {
                @Override
                public void onClick() {
                    setResponsePage(new ValuationMessages(val.getId()));
                }
            };


            add(acceptLink);
            add(rejectLink);
            add(messagesLink);

        }
    }
}
