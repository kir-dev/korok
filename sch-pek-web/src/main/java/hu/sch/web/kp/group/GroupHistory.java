package hu.sch.web.kp.group;

import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationStatistic;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.valuation.ValuationDetailPanel;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValueConversionException;

/**
 *
 * @author aldaris
 * @author messo
 */
public class GroupHistory extends KorokPage {

    @EJB(name = "ValuationManagerBean")
    private ValuationManagerLocal valuationManager;
    private final Long id;
    private final Group group;
    private Valuation selected = null;

    public GroupHistory() {
        getSession().error("Túl kevés paraméter!");
        throw new RestartResponseException(getApplication().getHomePage());
    }

    public GroupHistory(PageParameters parameters) {
        try {
            id = parameters.get("id").toLong();
        } catch (StringValueConversionException ex) {
            getSession().error("Érvénytelen paraméter!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        group = userManager.findGroupById(id);
        if (group == null) {
            getSession().error("Hibás paraméter, nincs ilyen kör!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        add(new BookmarkablePageLink<ShowGroup>("simpleView", ShowGroup.class, new PageParameters().add("id", id.toString())));

        List<Valuation> valuationList = valuationManager.findLatestValuationsForGroup(group);

        // nézzük meg, hogy van-e kijelölve értékelés
        Semester semester = new Semester(parameters.get("sid").toString(""));
        if (semester.isValid()) {
            for (Valuation valuation : valuationList) {
                if (valuation.getSemester().equals(semester)) {
                    selected = valuation;
                    break;
                }
            }
        }

        final boolean showSvieColumn = isCurrentUserJETI() || isUserGroupLeader(group);
        final ValuationDetailPanel valuationPanel = new ValuationDetailPanel("valuationInfo", showSvieColumn);
        if (selected != null) {
            setHeaderLabelText("A kör részletes pontozásai");
            ValuationStatistic stat = valuationManager.getStatisticForValuation(selected.getId());
            setTitleText(String.format("%s korábbi értékelései (%s); Szumma: %d Átlag: %.2f Belépők: %d ÁB %d KB",
                    group.getName(), semester.toString(), stat.getSummaPoint(), stat.getAveragePoint(), stat.getGivenAB(), stat.getGivenKB()));
            valuationPanel.updateValuation(selected);
        } else {
            setHeaderLabelText("Időszakválasztás");
            setTitleText(group.getName() + " korábbi értékelései");
            valuationPanel.setVisible(false);
        }
        add(valuationPanel);

        add(new Label("name", group.getName()));
        add(new DropDownChoice<Valuation>("valuations", new PropertyModel(this, "selected"),
                valuationList, new ChoiceRenderer<Valuation>("semester")) {

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(final Valuation selected) {
                PageParameters pp = new PageParameters();
                pp.add("id", id.toString());
                pp.add("sid", selected.getSemester().getId());
                setResponsePage(GroupHistory.class, pp);
            }
        });
    }
}
