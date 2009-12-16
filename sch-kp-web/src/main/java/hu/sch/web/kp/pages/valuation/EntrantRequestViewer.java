 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.valuation;

import hu.sch.domain.ConsideredValuation;
import hu.sch.domain.EntrantRequest;
import hu.sch.domain.EntrantType;
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
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class EntrantRequestViewer extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
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
                    ConsideredValuation cv = new ConsideredValuation(val, val.getPointStatus(), val.getEntrantStatus());
                    cv.setEntrantStatus(ValuationStatus.ELFOGADVA);
                    setResponsePage(new ConsiderExplainPage(list));
                }
            };

            Link rejectLink = new Link("reject") {

                @Override
                public void onClick() {
                    List<ConsideredValuation> list = new ArrayList<ConsideredValuation>();
                    ConsideredValuation cv = new ConsideredValuation(val, val.getPointStatus(), val.getEntrantStatus());
                    cv.setEntrantStatus(ValuationStatus.ELUTASITVA);
                    setResponsePage(new ConsiderExplainPage(list));
                }
            };
            add(acceptLink);
            add(rejectLink);

        }
    }
}
