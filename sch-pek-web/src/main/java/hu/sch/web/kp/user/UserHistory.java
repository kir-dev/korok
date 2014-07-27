package hu.sch.web.kp.user;

import hu.sch.domain.user.User;
import hu.sch.domain.*;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.group.GroupHierarchy;
import hu.sch.web.profile.show.ShowPersonPage;
import hu.sch.web.wicket.components.tables.ValuationTableForUser;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Adam Lantos
 * @author messo
 */
public class UserHistory extends KorokPage {

    private static final Logger logger = LoggerFactory.getLogger(UserHistory.class);
    private Long id;
    private Long selectedGroupId = null;
    private final String EVERY_GROUP = "Összes kör";
    // DropDownChoiceban használjuk
    protected String selected_text = EVERY_GROUP;
    private boolean own_profile = false;
    @Inject
    private ValuationManagerLocal valuationManager;

    public UserHistory() {
        own_profile = true;
        initComponents();
    }

    public UserHistory(PageParameters parameters) {
        try {
            id = parameters.get("id").toLong();
            selectedGroupId = parameters.get("group").toLong(0l);
            if (selectedGroupId != 0l) {
                Group g = groupManager.findGroupById(selectedGroupId);
                if (g != null) {
                    selected_text = g.getName();
                } else {
                    logger.warn("Group not found with id: {}", id);
                    selected_text = "NOT FOUND";
                }
            } else {
                selectedGroupId = null;
            }
        } catch (Throwable t) {
            logger.warn("Error while loading parameters.", t);
        }
        initComponents();
    }

    private void initComponents() {
        User user = null;
        if (id == null) {
            user = getCurrentUser();
        } else {
            user = userManager.findUserById(id, true);
        }
        if (user == null) {
            logger.warn("Not founded user for UserHistory page with id: " + id);
            error("A megadott felhasználóhoz nem tartozik közösségi történet!");
            throw new RestartResponseException(GroupHierarchy.class);
        }

        setHeaderLabelText(user.getFullName() + " közösségi története");
        setTitleText(user.getFullName() + " közösségi története");
        if (own_profile) {
            add(new BookmarkablePageLink<ShowUser>("simpleView", ShowUser.class));
        } else {
            add(new BookmarkablePageLink<ShowUser>("simpleView", ShowUser.class, new PageParameters().add("id", user.getId())));
        }
        add(new BookmarkablePageLink("profilelink", ShowPersonPage.class,
                new PageParameters().add("virid", id.toString())));
        setDefaultModel(new CompoundPropertyModel<User>(user));

        final List<String> groups = new ArrayList<String>();
        groups.add(EVERY_GROUP);

        List<Membership> ms = user.getMemberships();
        for (Membership csoporttagsag : ms) {
            groups.add(csoporttagsag.getGroup().getName());
        }

        DropDownChoice<String> ddc = new DropDownChoice<String>("group", new PropertyModel<String>(this, "selected_text"), groups) {

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(final String newSelection) {
                PageParameters pp = new PageParameters();

                // Ez van a legördülő menüben kiválasztva
                String Lselected = groups.get(Integer.valueOf(this.getInput()));

                if (Lselected.equals(EVERY_GROUP)) {
                    // minden kört megjelenítek
                } else {
                    // csak a kiválasztott kört jelenítem meg
                    Group group = groupManager.findGroupByName(Lselected);
                    pp.add("group", group.getId().toString());
                }

                pp.add("id", String.valueOf(id));
                setResponsePage(UserHistory.class, pp);
            }
        };

        add(ddc);

        //Gets semesters and points
        List<PointHistory> semesterPoints = userManager.getCommunityPointsForUser(user);

        // megjelenítés...
        ListView<PointHistory> splv = new ListView<PointHistory>("semesterPointList", semesterPoints) {

            @Override
            protected void populateItem(ListItem<PointHistory> item) {
                PointHistory p = item.getModelObject();

                item.add(new Label("semesterPoint.semester", Model.of(p.getSemester())));
                item.add(new Label("semesterPoint.point", Model.of(p.getPoint())));
            }
        };
        add(splv);

        List<ValuationData> list = valuationManager.findRequestsForUser(user, selectedGroupId);

        add(new ValuationTableForUser("table", list).getDataTable());
    }
}
