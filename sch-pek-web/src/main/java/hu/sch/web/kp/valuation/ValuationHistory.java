package hu.sch.web.kp.valuation;

import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationStatistic;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.valuation.message.ValuationMessages;
import hu.sch.web.kp.valuation.request.entrant.EntrantRequests;
import hu.sch.web.kp.valuation.request.point.PointRequests;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValueConversionException;

/**
 *
 * @author messo
 */
public class ValuationHistory extends KorokPage {

    @EJB(name = "ValuationManagerBean")
    private ValuationManagerLocal valuationManager;
    private int version = 0;

    public ValuationHistory(PageParameters params) {
        Long groupId = null;
        try {
            groupId = params.get("gid").toLong();
        } catch (StringValueConversionException ex) {
        }
        String semesterStr = params.get("sid").toString(null);
        Group group = null;
        Semester semester = null;

        if (groupId == null || (group = groupManager.findGroupById(groupId)) == null) {
            error("Nincs ilyen csoport!");
            setResponsePage(Valuations.class);
            return;
        }

        if (semesterStr == null || semesterStr.length() == 0 || !(semester = new Semester(semesterStr)).isValid()) {
            error("Nincs ilyen félév!");
            setResponsePage(Valuations.class);
            return;
        }

        // keressük az értékeléseket (verziókat) a megadott csoporthoz a megadott félévben.

        add(new BookmarkablePageLink("latestVersion", ValuationDetails.class,
                new PageParameters().add("id", valuationManager.findLatestVersionsId(group, semester))));
        add(ValuationMessages.getLink("messages", group.getId(), semester));

        setHeaderLabelText("Félévi értékelés története");
        add(new Label("groupName", group.getName()));
        add(new Label("semester", semester.toString()));

        List<ValuationStatistic> versions = valuationManager.findValuationStatisticForVersions(group, semester);

        version = versions.size();

        add(new ListView<ValuationStatistic>("versionList", versions) {

            @Override
            protected void populateItem(ListItem<ValuationStatistic> item) {
                final Valuation val = item.getModelObject().getValuation();
                item.setDefaultModel(new CompoundPropertyModel<ValuationStatistic>(item.getModelObject()));

                PageParameters params = new PageParameters().add("vid", val.getId());

                item.add(new BookmarkablePageLink("versionLink", ValuationDetails.class,
                        new PageParameters().add("id", val.getId())).add(
                        new Label("versionLabel", String.valueOf(version--))));

                item.add(DateLabel.forDatePattern("valuation.lastModified", "yyyy. MM. dd. kk:mm"));
                item.add(DateLabel.forDatePattern("valuation.lastConsidered", "yyyy. MM. dd. kk:mm"));

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

//                item.add(new Link("messagesLink") {
//
//                    @Override
//                    public void onClick() {
//                        setResponsePage(new ValuationMessages(val.getId()));
//                    }
//                });

                item.add(new Label("valuation.pointStatus"));
                item.add(new Label("valuation.entrantStatus"));
            }
        });
    }
}
