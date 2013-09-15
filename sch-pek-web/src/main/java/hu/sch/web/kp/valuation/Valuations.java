package hu.sch.web.kp.valuation;

import hu.sch.domain.enums.ValuationPeriod;
import hu.sch.domain.enums.ValuationStatus;
import hu.sch.domain.user.User;
import hu.sch.domain.*;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.group.GroupHierarchy;
import hu.sch.web.kp.valuation.message.ValuationMessages;
import hu.sch.web.kp.valuation.request.entrant.EntrantRequests;
import hu.sch.web.kp.valuation.request.point.PointRequests;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A körvezetők ezen lap segítségével adhatnak be értékeléseket a köreikhez.
 *
 * @author hege
 * @author messo
 */
public class Valuations extends KorokPage {

    @Inject
    ValuationManagerLocal valuationManager;
    private static Logger log = LoggerFactory.getLogger(Valuations.class);
    private static final String HEADER_TEXT = "Értékelések";
    private final Group group;

    public Valuations() {
        setHeaderLabelText(HEADER_TEXT);
        setTitleText(HEADER_TEXT);
        group = null;
        init();
    }

    public Valuations(PageParameters params) {
        try {
            Long groupId = params.get("id").toLong();
            group = groupManager.findGroupById(groupId);
        } catch (StringValueConversionException svce) {
            getSession().error("Érvénytelen paraméter!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        if (!isUserGroupLeader(group)) {
            log.warn("Paraméterátírásos próbálkozás! " + getUser().getId());
            getSession().error("Nincs jogod a művelethez! A próbálkozásod naplózásra került!");
            throw new RestartResponseException(getApplication().getHomePage());
        }
        setHeaderLabelText(HEADER_TEXT);
        init();
    }

    private void init() {
        User user = userManager.findUserById(getSession().getUserId(), true);
        if (user == null || !isUserGroupLeaderInSomeGroup()) {
            getSession().error(getLocalizer().getString("err.NincsJog", this));
            throw new RestartResponseException(GroupHierarchy.class);
        }
        user.sortMemberships();

        final List<Membership> ms = user.getMemberships();
        final List<Group> groups = new ArrayList<Group>();
        //TODO simplify this
        for (Membership m : ms) {
            if (isUserGroupLeader(m.getGroup())) {
                groups.add(m.getGroup());
            }
        }

        // megkeresem mire nem adott még le értékelést vagy belépőigényt az aktuális félévben
        final Map<Group, Valuation> valuationsForGroup = new HashMap<Group, Valuation>();
        Semester semester = systemManager.getSzemeszter();
        ValuationPeriod valuationPeriod = systemManager.getErtekelesIdoszak();
        for (Membership m : ms) {
            Group cs = m.getGroup();

            Valuation ert = valuationManager.findLatestValuation(cs, semester);
            if ((ert == null || ert.getPointStatus() == ValuationStatus.NINCS
                    || ert.getEntrantStatus() == ValuationStatus.NINCS)
                    && isUserGroupLeader(cs)
                    && valuationPeriod == ValuationPeriod.ERTEKELESLEADAS) {
                // kelleni fog majd az értékelés objektum a táblázatnál, ezért
                // mentsük el egy Mapben.
                valuationsForGroup.put(cs, ert);
            }
        }

        // ha van olyan csoport, ahol van dolga a körvezetőnek, akkor azt mutassuk
        // meg egy szép kis táblázatban
        if (!valuationsForGroup.isEmpty()) {
            add(new WarningPanel("warningPanel", valuationsForGroup));
        } else {
            add(new EmptyPanel("warningPanel"));
        }

        add(new DropDownChoice<Group>("groups", new PropertyModel<Group>(this, "group"),
                groups, new ChoiceRenderer<Group>("name")) {

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(final Group selected) {
                setResponsePage(Valuations.class, new PageParameters().add("id", selected.getId()));
            }
        });

        // ha van kiválasztva csoport, akkor mehet a panel, különben ne jelenítsünk
        // meg üres táblázatot
        if (group != null) {
            add(new ValuationListPanel("listPanel"));
        } else {
            add(new EmptyPanel("listPanel"));
        }
    }

    private class ValuationListPanel extends Panel {

        public ValuationListPanel(String id) {
            super(id);

            setTitleText(HEADER_TEXT + " - " + group.getName());

            Label groupName = new Label("name", group.getName());
            add(groupName);

            final boolean nincsErtekelesLeadas =
                    systemManager.getErtekelesIdoszak() != ValuationPeriod.ERTEKELESLEADAS;

            WebMarkupContainer table = new WebMarkupContainer("ertekelesektabla");
            add(table);

            List<Valuation> valuationList = valuationManager.findLatestValuationsForGroup(group);
            table.add(new ListView<Valuation>("valuationList", valuationList) {

                @Override
                protected void populateItem(ListItem<Valuation> item) {
                    final Valuation v = item.getModelObject();
                    item.setModel(new CompoundPropertyModel<Valuation>(v));

                    Link<ValuationDetails> ert = new BookmarkablePageLink<ValuationDetails>("valuationLink", ValuationDetails.class, new PageParameters().add("id", v.getId()));
                    ert.add(new Label("semester"));
                    item.add(ert);

                    item.add(ValuationMessages.getLink("messagesLink", v));

                    item.add(new BookmarkablePageLink("pointLink",
                            PointRequests.class,
                            new PageParameters().add("vid", v.getId())).add(new Label("pointStatus")));

                    item.add(new BookmarkablePageLink("entrantLink",
                            EntrantRequests.class,
                            new PageParameters().add("vid", v.getId())).add(new Label("entrantStatus")));

                    item.add(DateLabel.forDatePattern("lastModified", "yyyy.MM.dd. kk:mm"));
                    item.add(DateLabel.forDatePattern("lastConsidered", "yyyy.MM.dd. kk:mm"));
                }
            });

            Link<NewValuation> newValuation = new BookmarkablePageLink<NewValuation>("newValuation", NewValuation.class, new PageParameters().add("id", group.getId()));
            add(newValuation);

            if (valuationList.isEmpty()
                    || !valuationManager.isErtekelesLeadhato(group)) {
                newValuation.setVisible(false);
            }
        }
    }

    private class WarningPanel extends Panel {

        public WarningPanel(String id, final Map<Group, Valuation> left) {
            super(id);

            add(new ListView<Group>("warningList", new ArrayList<Group>(left.keySet())) {

                @Override
                protected void populateItem(ListItem<Group> item) {
                    final Group group = item.getModelObject();
                    final Valuation val = left.get(group);

                    item.add(new Label("warningGroupName", group.getName()));

                    item.add(new Link("warningLink") {

                        @Override
                        protected void onBeforeRender() {
                            super.onBeforeRender();

                            // a link akkor látható, ha nincs még a körhöz értékelés
                            // vagy, ha nincs pontozás leadva
                            setVisible(val == null || val.getPointStatus() == ValuationStatus.NINCS);
                        }

                        @Override
                        public void onClick() {
                            // group kiválasztása (mert nem feltétlen volt legördülővel...)
                            if (val == null) {
                                // ha egyáltalán nincs még értékelés az adott csoporthoz
                                // és szemeszterhez, akkor először szöveges értékelés kell
                                setResponsePage(NewValuation.class, new PageParameters().add("id", group.getId()));
                            } else {
                                // pontigény leadása a szöveges értékelés mellé
                                setResponsePage(PointRequests.class, new PageParameters().add("vid", val.getId()));
                            }
                        }
                    });

                    // belépőigénylés kijelzés
                    item.add(new Link("warningEntrantLink") {

                        @Override
                        protected void onBeforeRender() {
                            super.onBeforeRender();

                            // a link akkor látható, ha nincs még a körhöz értékelés
                            // vagy, ha nincs pontozás leadva
                            setVisible(val == null || val.getEntrantStatus() == ValuationStatus.NINCS);
                        }

                        @Override
                        public void onClick() {
                            // group kiválasztása (mert nem feltétlen volt legördülővel...)
                            if (val == null) {
                                // ha egyáltalán nincs még értékelés az adott csoporthoz
                                // és szemeszterhez, akkor először szöveges értékelés kell
                                setResponsePage(NewValuation.class, new PageParameters().add("id", group.getId()));
                            } else {
                                // belépőigény leadása a szöveges értékelés mellé
                                setResponsePage(EntrantRequests.class, new PageParameters().add("vid", val.getId()));
                            }
                        }
                    });
                }
            });
        }
    }
}
