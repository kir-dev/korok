package hu.sch.web.kp.valuation.request.point;

import hu.sch.domain.PointRequest;
import hu.sch.domain.User;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationStatistic;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.wicket.components.customlinks.UserLink;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 * @author messo
 */
public class PointRequestViewer extends Panel {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public PointRequestViewer(String id, final Valuation val) {
        super(id);

        // TODO(messo): ehhez valami rendesebb query-t pakoljunk ide, nem kell két query.
        final List<User> activeMembers = userManager.getCsoporttagokWithoutOregtagok(val.getGroup().getId());
        final List<PointRequest> requests = valuationManager.findPontIgenyekForErtekeles(val.getId());

        /*/tagok és igények összefésülése
        if (requests.isEmpty()) {
            for (User f : activeMembers) {
                requests.add(new PointRequest(f, 0));
            }
        }*/

        add(new ListView<PointRequest>("requests", requests) {

            @Override
            protected void populateItem(ListItem<PointRequest> item) {
                final PointRequest p = item.getModelObject();
                item.setModel(new CompoundPropertyModel<PointRequest>(p));
                item.add(new UserLink("userLink", p.getUser()));
                item.add(new Label("user.nickName"));
                item.add(new Label("point"));
            }
        });

        ValuationStatistic stat = valuationManager.getStatisticForValuation(val.getId());
        add(new Label("stat.averagePoint", stat.getAveragePoint().toString()));
        add(new Label("stat.sumPoint", stat.getSummaPoint().toString()));

        if (requests.isEmpty())
            getSession().info(getLocalizer().getString("info.NincsErtekeles", this.getParent() ));
    }
}
