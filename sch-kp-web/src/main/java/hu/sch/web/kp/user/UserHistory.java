/**
 * Copyright (c) 2009-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hu.sch.web.kp.user;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.Semester;
import hu.sch.domain.User;
import hu.sch.domain.ValuationData;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.group.GroupHierarchy;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.wicket.components.tables.ValuationTableForUser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
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
 * @author  Adam Lantos
 * @author  messo
 */
public class UserHistory extends KorokPage {

    private static final Logger log = Logger.getLogger(UserHistory.class);
    private Long id;
    private Long selectedGroupId = null;
    private final String EVERY_GROUP = "Összes kör";
    // DropDownChoiceban használjuk
    protected String selected_text = EVERY_GROUP;
    private boolean own_profile = false;
    @EJB(name = "ValuationManagerBean")
    private ValuationManagerLocal valuationManager;

    public UserHistory() {
        own_profile = true;
        initComponents();
    }

    public UserHistory(PageParameters parameters) {
        try {
            id = parameters.getLong("id");
            selectedGroupId = parameters.getLong("group", 0l);
            if (selectedGroupId != 0l) {
                selected_text = userManager.findGroupById(selectedGroupId).getName();
            } else {
                selectedGroupId = null;
            }
        } catch (Throwable t) {
            log.warn("Error while loading parameters.", t);
        }
        initComponents();
    }

    private void initComponents() {
        User user;
        if (id == null) {
            id = getSession().getUserId();
        }
        user = userManager.findUserWithMembershipsById(id);
        if (user == null) {
            log.warn("Not founded user for UserHistory page with id: " + id);
            error("A megadott felhasználóhoz nem tartozik közösségi történet!");
            throw new RestartResponseException(GroupHierarchy.class);
        }

        setHeaderLabelText(user.getName() + " közösségi története");
        setTitleText(user.getName() + " közösségi története");
        if (own_profile) {
            add(new BookmarkablePageLink<ShowUser>("simpleView", ShowUser.class));
        } else {
            add(new BookmarkablePageLink<ShowUser>("simpleView", ShowUser.class, new PageParameters("id=" + user.getId())));
        }
        add(new ExternalLink("profilelink", "/profile/show/virid/" + id.toString()));
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
                    Group group = userManager.findGroupByName(Lselected).get(0);
                    pp.add("group", group.getId().toString());
                }

                pp.add("id", String.valueOf(id));
                setResponsePage(UserHistory.class, pp);
            }
        };

        add(ddc);

        List<SemesterPoint> semesterPoints = new ArrayList<SemesterPoint>();
        for (Semester s : userManager.getAllValuatedSemesterForUser(user)) {
            semesterPoints.add(new SemesterPoint(s, userManager.getSemesterPointForUser(user, s)));
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

        List<ValuationData> list = valuationManager.findRequestsForUser(user, selectedGroupId);

        add(new ValuationTableForUser("table", list).getDataTable());
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
