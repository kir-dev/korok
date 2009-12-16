/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.valuation;

import hu.sch.domain.ConsideredValuation;
import hu.sch.domain.PointRequest;
import hu.sch.domain.User;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationStatus;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.kp.util.ListDataProviderCompoundPropertyModelImpl;
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
                    ConsideredValuation cv = new ConsideredValuation(val, val.getPointStatus(), val.getEntrantStatus());
                    cv.setPointStatus(ValuationStatus.ELFOGADVA);
                    setResponsePage(new ConsiderExplainPage(list));
                }
            };

            Link rejectLink = new Link("reject") {

                @Override
                public void onClick() {
                    List<ConsideredValuation> list = new ArrayList<ConsideredValuation>();
                    ConsideredValuation cv = new ConsideredValuation(val, val.getPointStatus(), val.getEntrantStatus());
                    cv.setPointStatus(ValuationStatus.ELUTASITVA);
                    setResponsePage(new ConsiderExplainPage(list));
                }
            };
            add(acceptLink);
            add(rejectLink);

        }
    }
}

