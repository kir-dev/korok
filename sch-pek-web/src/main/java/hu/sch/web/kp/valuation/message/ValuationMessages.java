package hu.sch.web.kp.valuation.message;

import hu.sch.domain.*;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.kp.valuation.ValuationHistory;
import hu.sch.web.kp.valuation.Valuations;
import hu.sch.web.wicket.components.customlinks.UserLink;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValueConversionException;

/**
 *
 * @author hege
 * @author messo
 */
public class ValuationMessages extends KorokPage {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;
    private Group group = null;
    private Semester semester = null;

    public static BookmarkablePageLink<ValuationMessages> getLink(String id, final Valuation v) {
        return getLink(id, v.getGroupId(), v.getSemester());
    }

    public static BookmarkablePageLink<ValuationMessages> getLink(String id, final Long gId, final Semester s) {
        return new BookmarkablePageLink<ValuationMessages>(id, ValuationMessages.class,
                new PageParameters().add("gid", gId.toString()).add("sid", s.getId()));
    }

    public ValuationMessages(PageParameters params) {
        Long groupId = null;
        try {
            groupId = params.get("gid").toLong();
        } catch (StringValueConversionException ex) {
        }
        String semesterStr = params.get("sid").toString(null);

        if (groupId == null || (group = userManager.findGroupById(groupId)) == null) {
            error("Nincs ilyen csoport!");
            setResponsePage(Valuations.class);
            return;
        }

        if (semesterStr == null || semesterStr.length() == 0 || !(semester = new Semester(semesterStr)).isValid()) {
            error("Nincs ilyen félév!");
            setResponsePage(Valuations.class);
            return;
        }

        setHeaderLabelText("Értékeléshez tartozó üzenetek");

        add(new Label("groupName", group.getName()));
        add(new Label("semester", semester.toString()));

        add(new BookmarkablePageLink("latestVersion", ValuationDetails.class,
                new PageParameters().add("id", valuationManager.findLatestVersionsId(group, semester))));
        add(new BookmarkablePageLink("history", ValuationHistory.class, new PageParameters().add("gid", group.getId()).add("sid", semester.getId())));

        // ok megvan, hogy melyik a csoport és melyik a félév
        List<ValuationMessage> messages = valuationManager.getMessages(group, semester);
        if (messages.isEmpty()) {
            info(getLocalizer().getString("info.NincsUzenet", this));
        }

        ListView<ValuationMessage> uzenetekView = new ListView<ValuationMessage>("uzenetek", messages) {

            @Override
            protected void populateItem(ListItem<ValuationMessage> item) {
                final ValuationMessage vm = item.getModelObject();
                item.setModel(new CompoundPropertyModel<ValuationMessage>(vm));
                WebMarkupContainer header;
                item.add(header = new WebMarkupContainer("messageHeader") {

                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);
                        if (vm.isFromSystem()) {
                            tag.getAttributes().put("class", tag.getAttribute("class") + " fromSystem");
                        }
                    }
                });
                if (vm.isFromSystem()) {
                    header.add(new Label("sender", "Rendszerüzenet"));
                } else {
                    header.add(new UserLink("sender", vm.getSender()));
                }
                header.add(DateLabel.forDatePattern("date", "yyyy. MM. dd. HH:mm"));
                item.add(new MultiLineLabel("message"));
            }
        };
        add(uzenetekView);

        Link ujuzenet = new Link("newMessageLink") {

            @Override
            public void onClick() {
                setResponsePage(new NewMessage(group, semester));
            }
        };
        // csak akkor lehet új üzenetet hozzáadni, ha a jelenlegi félévben vagyunk
        // illetve: leadási időszak van + körvezető VAGY elbírálási időszak van + JETI
        ValuationPeriod vp = systemManager.getErtekelesIdoszak();
        ujuzenet.setVisible(systemManager.getSzemeszter().equals(semester)
                && (vp == ValuationPeriod.ERTEKELESLEADAS || vp == ValuationPeriod.ERTEKELESELBIRALAS)
                && (isUserGroupLeader(group) || isCurrentUserJETI()));
        add(ujuzenet);
    }
}
