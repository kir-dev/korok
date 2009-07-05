/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.user;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.PointRequest;
import hu.sch.domain.Semester;
import hu.sch.domain.User;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author Adam Lantos
 */
public class UserHistory extends SecuredPageTemplate {

    Long id;
    final Long EVERY_GROUP_L = -1L;
    public Long selected = EVERY_GROUP_L;
    final String EVERY_GROUP = "Összes kör";
    public String selected_text = EVERY_GROUP;
    private boolean own_profile = false;

    public UserHistory() {
        own_profile = true;
        initComponents();
    }

    public UserHistory(PageParameters parameters) {
        try {
            id = parameters.getLong("id");
            selected = parameters.getLong("group", EVERY_GROUP_L);
            selected_text = userManager.findGroupById(selected).getName();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        initComponents();
    }

    private void initComponents() {
        User user = getSession().getUser();
        if (id == null) {
            id = user.getId();
        }
        if (user == null) {
            throw new IllegalStateException();
        }

        setHeaderLabelText(user.getName() + " közösségi története");
        if (own_profile) {
            add(new BookmarkablePageLink("simpleView", ShowUser.class));
        } else {
            add(new BookmarkablePageLink("simpleView", ShowUser.class, new PageParameters("id=" + user.getId())));
        }
        add(new ExternalLink("profilelink", "/profile/show/virid/" + id.toString()));
        setDefaultModel(new CompoundPropertyModel(user));

        final List<String> groups = new ArrayList<String>();
        groups.add(EVERY_GROUP);

        List<Membership> ms = user.getMemberships();
        for (Membership csoporttagsag : ms) {
            groups.add(csoporttagsag.getGroup().getName());
        }

        List<PointRequest> pointRequests = userManager.getPontIgenyekForUser(user);

        DropDownChoice ddc = new DropDownChoice("group", new PropertyModel(this, "selected_text"), groups)
        {

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(final Object newSelection)
            {
                PageParameters pp = new PageParameters();

                // Ez van a legördülő menüben kiválasztva
                String Lselected = groups.get(Integer.valueOf(this.getInput()));

                if (Lselected.equals(EVERY_GROUP))
                {
                    // minden kört megjelenítek
                    pp.add("group", EVERY_GROUP_L.toString());
                }
                else
                {
                    // csak a kiválasztott kört jelenítem meg
                    Group group = userManager.findGroupByName(Lselected).get(0);
                    pp.add("group", group.getId().toString());
                }

                pp.add("id", String.valueOf(id));
                setResponsePage(UserHistory.class, pp);
            }
        };

        add(ddc);

        // Szemeszterenkénti pontigények táblázat
        ArrayList<SemesterGroupPoint> sgp = new ArrayList<SemesterGroupPoint>();

        // minden kör minden pontjához hozzáadom az előző évben adott pontot (ha volt előző féléves pont is)
        for (PointRequest pointRequest : pointRequests) {
            sgp.add(new SemesterGroupPoint(pointRequest.getValuation().getSemester(),
                    pointRequest.getPoint(), pointRequest.getValuation().getGroup().getId()));
        }

        for (SemesterGroupPoint sgp2 : sgp) {
            for (SemesterGroupPoint skp2 : sgp) {
                if (sgp2.getGroupId().equals(skp2.getGroupId()) &&
                        sgp2.getSemester().getPrevious().getId().equals(skp2.getSemester().getId())) {
                    sgp2.add(skp2.getPoint());
                }
            }
        }

        // a csak előző félévben pontozott köröket is hozzá kell majd számolni a jelenlegi féléves pontokhoz
        for (SemesterGroupPoint skp : sgp.toArray(new SemesterGroupPoint[sgp.size()])) {
            boolean isNot = true;
            for (SemesterGroupPoint skp2 : sgp) {
                if (skp2.getGroupId().equals(skp.getGroupId()) &&
                        skp2.getSemester().equals(skp.getSemester().getNext())) {
                    isNot = false;
                    break;
                }
            }

            if (isNot) {   // ebből a körből nincs most pont csak az előző félévben,
                // viszont nekem azt is összegeznem kell majd
                if (!skp.getSemester().equals(systemManager.getSzemeszter())) // jövőbe nem pontozunk :)
                {
                    sgp.add(new SemesterGroupPoint(skp.getSemester().getNext(), skp.getPoint(), skp.getGroupId()));
                }
            }
        }

        ArrayList<SemesterPoint> semesterPoints = new ArrayList<SemesterPoint>();

        Semester semester = null;

        // négyzetösszegek...
        for (SemesterGroupPoint skp : sgp) {

            if (!skp.getSemester().equals(semester)) {
                semester = skp.getSemester();
            } else {
                continue;
            }

            // megnézem számoltam-e már ezt a félévet
            boolean next = false;
            for (SemesterPoint semesterPoint : semesterPoints) {
                if (semesterPoint.getSemester().equals(semester)) {
                    next = true;
                    break;
                }
            }

            if (next) {
                continue;   // már számoltam ezt a félévet
            }
            // négyzetösszeg...
            int point = 0;

            for (SemesterGroupPoint p : sgp) {
                if (p.getSemester().equals(semester)) {
                    point = point + p.getPoint() * p.getPoint();
                }
            }

            point = (int) Math.sqrt(point); // nem szabályos kerekítés! (egészrész)

            semesterPoints.add(new SemesterPoint(semester, point));
        }


        // megjelenítés...
        ListView<SemesterPoint> splv = new ListView<SemesterPoint>("semesterPointList", semesterPoints) {

            @Override
            protected void populateItem(ListItem<SemesterPoint> item) {
                SemesterPoint p = item.getModelObject();
                item.add(new Label("semesterPoint.semester", p.getSemester().toString()));
                item.add(new Label("semesterPoint.point", String.valueOf(p.getPoint())));
            }
        };
        add(splv);

        // Pontigények táblázat
        if (!selected.equals(EVERY_GROUP_L)) {
            // szűrés adott csoportra

            ArrayList<PointRequest> obj = new ArrayList<PointRequest>();
            for (PointRequest pontIgeny : pointRequests) {
                if (pontIgeny.getValuation().getGroup().getId().equals(selected)) {
                    obj.add(pontIgeny);
                }
            }
            pointRequests = obj;
        }

        ListView<PointRequest> plv = new ListView<PointRequest>("pointRequestList", pointRequests) {

            @Override
            protected void populateItem(ListItem<PointRequest> item) {
                item.setModel(new CompoundPropertyModel<PointRequest>(item.getModelObject()));

                item.add(new Label("valuation.semester"));
                item.add(new Label("valuation.group.name"));
                item.add(new Label("point"));
            }
        };
        add(plv);

        // Belépő igények táblázat
        List<EntrantRequest> entrantRequests = userManager.getBelepoIgenyekForUser(user);

        if (!selected.equals(EVERY_GROUP_L)) {
            // szűrés adott csoportra

            ArrayList<EntrantRequest> obj = new ArrayList<EntrantRequest>();
            for (EntrantRequest belepoIgeny : entrantRequests) {
                if (belepoIgeny.getValuation().getGroup().getId().equals(selected)) {
                    obj.add(belepoIgeny);
                }
            }
            entrantRequests = obj;
        }

        ListView<EntrantRequest> blv = new ListView<EntrantRequest>("entrantRequestList", entrantRequests) {
            @Override
            protected void populateItem(ListItem<EntrantRequest> item) {
                item.setModel(new CompoundPropertyModel<EntrantRequest>(item.getModelObject()));

                item.add(new Label("valuation.semester"));
                item.add(new Label("valuation.group.name"));
                item.add(new Label("entrantType"));
                item.add(new Label("valuationText"));
            }
        };
        add(blv);
    }
}

class SemesterGroupPoint {

    private Semester semester;
    private Long groupId;
    private Integer point;

    public SemesterGroupPoint(Semester semester, Integer point, Long groupId) {
        this.semester = semester;
        this.point = point;
        this.groupId = groupId;
    }

    public Semester getSemester() {
        return semester;
    }

    public Integer getPoint() {
        return point;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void add(Integer i) {
        point = point + i;
    }
}

class SemesterPoint implements Serializable {

    private Semester semester;
    private Integer point;

    public SemesterPoint(Semester semester, Integer point) {
        this.semester = semester;
        this.point = point;
    }

    public Semester getSemester() {
        return semester;
    }

    public Integer getPoint() {
        return point;
    }
}
