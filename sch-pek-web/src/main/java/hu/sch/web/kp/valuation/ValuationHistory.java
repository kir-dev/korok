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
package hu.sch.web.kp.valuation;

import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationStatistic;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.valuation.request.entrant.EntrantRequests;
import hu.sch.web.kp.valuation.request.point.PointRequests;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author messo
 */
public class ValuationHistory extends KorokPage {

    @EJB(name = "ValuationManagerBean")
    private ValuationManagerLocal valuationManager;
    private int version = 0;

    public ValuationHistory(PageParameters params) {
        Long groupId = params.getAsLong("gid");
        String semesterStr = params.getString("sid", null);
        Group group = null;
        Semester semester = null;

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

        // keressük az értékeléseket (verziókat) a megadott csoporthoz a megadott félévben.

        add(new BookmarkablePageLink("latestVersion", ValuationDetails.class,
                new PageParameters("id=" + valuationManager.findLatestVersionsId(group, semester))));

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

                PageParameters params = new PageParameters("vid=" + val.getId());

                item.add(new BookmarkablePageLink("versionLink", ValuationDetails.class,
                        new PageParameters("id="+val.getId())).add(
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

                /*item.add(new Link("messagesLink") {

                @Override
                public void onClick() {
                setResponsePage(new ValuationMessages(val.getId()));
                }
                });*/

                item.add(new Label("valuation.pointStatus"));
                item.add(new Label("valuation.entrantStatus"));
            }
        });
    }
}
