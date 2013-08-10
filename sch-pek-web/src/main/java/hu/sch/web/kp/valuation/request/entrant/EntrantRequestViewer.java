package hu.sch.web.kp.valuation.request.entrant;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.enums.EntrantType;
import hu.sch.domain.user.User;
import hu.sch.domain.Valuation;
import hu.sch.services.GroupManagerLocal;
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
public class EntrantRequestViewer extends Panel {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal ertekelesManager;
    @EJB(name = "GroupManagerBean")
    private GroupManagerLocal groupManager;

    public EntrantRequestViewer(String id, final Valuation ert) {
        super(id);

        add(new ListView<EntrantRequest>("requests", igenyeketElokeszit(ert)) {

            @Override
            protected void populateItem(ListItem<EntrantRequest> item) {
                final EntrantRequest b = item.getModelObject();
                item.setModel(new CompoundPropertyModel<EntrantRequest>(b));

                item.add(new UserLink("userLink", b.getUser()));
                item.add(new Label("user.nickName"));
                item.add(new Label("entrantType"));
                item.add(new Label("valuationText"));
            }
        });
    }

    private List<EntrantRequest> igenyeketElokeszit(Valuation ert) {
        List<User> csoporttagok = groupManager.findActiveMembers(ert.getGroup().getId());
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
}
