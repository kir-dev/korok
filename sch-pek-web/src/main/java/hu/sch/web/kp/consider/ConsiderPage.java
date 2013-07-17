package hu.sch.web.kp.consider;

import hu.sch.domain.user.User;
import hu.sch.domain.*;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.group.GroupHierarchy;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.kp.valuation.message.ValuationMessages;
import hu.sch.web.kp.valuation.request.entrant.EntrantRequests;
import hu.sch.web.kp.valuation.request.point.PointRequests;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import hu.sch.web.wicket.components.choosers.ValuationStatusChooser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author hege
 */
//TODO
public class ConsiderPage extends KorokPage {

    @EJB(name = "ValuationManagerBean")
    private ValuationManagerLocal valuationManager;
    Map<Long, ConsideredValuation> underConsidering = new HashMap<Long, ConsideredValuation>();

    public Map<Long, ConsideredValuation> getUnderConsidering() {
        return underConsidering;
    }

    public void setUnderConsidering(Map<Long, ConsideredValuation> underConsidering) {
        this.underConsidering = underConsidering;
    }

    public ConsiderPage() {
        // jogosultság ellenőrzés
        if (!isCurrentUserJETI()) {
            getSession().error("Nincs jogod a megadott művelethez");
            throw new RestartResponseException(GroupHierarchy.class);
        }

        // időszak ellenőrzés
        if (systemManager.getErtekelesIdoszak() != ValuationPeriod.ERTEKELESELBIRALAS) {
            getSession().error("Nincs elbírálási időszak!");
            throw new RestartResponseException(GroupHierarchy.class);
        }

        setHeaderLabelText("Leadott értékelések elbírálása");
        add(new Label("semester", getSemester().toString()));
        IDataProvider<ValuationStatistic> dp = new ValuationStatisticDataProvider(
                valuationManager.findValuationStatisticForSemester());

        final User user = getUser();

        Form form = new Form("considerForm") {

            @Override
            protected void onSubmit() {
                List<ConsideredValuation> list = new ArrayList<ConsideredValuation>();

                for (ConsideredValuation consideredValuation : getUnderConsidering().values()) {
                    //if ((elbiraltertekeles.getPointStatus().equals(ValuationStatus.ELBIRALATLAN) && (elbiraltertekele)

                    // Ha valtozott valamelyik belepokerelemhez vagy pontkerelemhez tartozo legordulo,
                    if ((consideredValuation.getPointStatus() != consideredValuation.getValuation().getPointStatus())
                            || (consideredValuation.getEntrantStatus() != consideredValuation.getValuation().getEntrantStatus())) {
                        list.add(consideredValuation);
                    }
                }

                if (!hasError() && list.isEmpty()) {
                    error("Nem bíráltál el egy értékelést sem!");
                }
                if (!hasError()) {
                    setResponsePage(new ConsiderExplainPage(list));
                }
            }
        };
        form.add(new KeepAliveBehavior());
        add(form);

        form.add(new DataView<ValuationStatistic>("valuationList", dp) {

            @Override
            protected void populateItem(Item<ValuationStatistic> item) {
                final Valuation val = item.getModelObject().getValuation();

                ConsideredValuation cv = null;
                if (!getUnderConsidering().containsKey(val.getId())) {
                    cv = new ConsideredValuation(val, val.getPointStatus(), val.getEntrantStatus(), user);
                    getUnderConsidering().put(val.getId(), cv);
                } else {
                    cv = getUnderConsidering().get(val.getId());
                }

                Link valuationLink = new BookmarkablePageLink("valuationLink", ValuationDetails.class, new PageParameters().add("id", val.getId()));
                valuationLink.add(new Label("valuation.group.name"));
                item.add(valuationLink);

                PageParameters params = new PageParameters().add("vid", val.getId());

                Link givenKDOLink = new BookmarkablePageLink("givenKDOLink", EntrantRequests.class, params);
                givenKDOLink.add(new Label("givenKDO"));
                item.add(givenKDOLink);

                Link givenKBLink = new BookmarkablePageLink("givenKBLink", EntrantRequests.class, params);
                givenKBLink.add(new Label("givenKB"));
                item.add(givenKBLink);

                Link givenABLink = new BookmarkablePageLink("givenABLink", EntrantRequests.class, params);
                givenABLink.add(new Label("givenAB"));
                item.add(givenABLink);

                Link pointLink = new BookmarkablePageLink("pointLink", PointRequests.class, params);
                pointLink.add(new Label("averagePoint"));
                item.add(pointLink);

                Link summaPointLink = new BookmarkablePageLink("summaPointLink", PointRequests.class, params);
                summaPointLink.add(new Label("summaPoint"));
                item.add(summaPointLink);

                item.add(ValuationMessages.getLink("messagesLink", val));

                Component pointStatus = new ValuationStatusChooser("pointStatus");
                Component entrantStatus = new ValuationStatusChooser("entrantStatus");
                pointStatus.setVisible(!val.getPointStatus().equals(ValuationStatus.NINCS));
                //if (belepoStatusz != null && ert != null && ert.getEntrantStatus() != null) { // null check always false
                if (val.getEntrantStatus() != null) {
                    entrantStatus.setVisible(!val.getEntrantStatus().equals(ValuationStatus.NINCS));
                }
                pointStatus.setDefaultModel(new PropertyModel<ConsideredValuation>(cv, "pointStatus"));
                entrantStatus.setDefaultModel(new PropertyModel<ConsideredValuation>(cv, "entrantStatus"));
                item.add(pointStatus);
                item.add(entrantStatus);
            }
        });
    }

    @SuppressWarnings("unused")
    private class OrderByBorderImpl extends OrderByBorder {

        public OrderByBorderImpl(String id, String property, ISortStateLocator stateLocator) {
            super(id, property, stateLocator);
        }

        @Override
        protected void onSortChanged() {
            getUnderConsidering().clear();
        }
    }
}
