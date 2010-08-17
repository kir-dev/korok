/**
 * Copyright (c) 2008-2010, Peter Major
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
package hu.sch.web.kp.valuation.message;

import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationMessage;
import hu.sch.domain.ValuationPeriod;
import hu.sch.web.wicket.components.customlinks.UserLink;
import hu.sch.web.kp.KorokPage;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.kp.valuation.ValuationHistory;
import hu.sch.web.kp.valuation.Valuations;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
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
                new PageParameters(new HashMap<String, String>() {

            {
                put("gid", gId.toString());
                put("sid", s.getId());
            }
        }));
    }

    public ValuationMessages(PageParameters params) {
        Long groupId = params.getAsLong("gid");
        String semesterStr = params.getString("sid", null);

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
                new PageParameters("id=" + valuationManager.findLatestVersionsId(group, semester))));
        add(new BookmarkablePageLink("history", ValuationHistory.class, new PageParameters(new HashMap() {

            {
                put("gid", group.getId());
                put("sid", semester.getId());
            }
        })));

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
                header.add(DateLabel.forDatePattern("date", "yyyy. MM. dd. kk:mm"));
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
                && ((vp == ValuationPeriod.ERTEKELESLEADAS && isUserGroupLeader(group))
                || (vp == ValuationPeriod.ERTEKELESELBIRALAS && isCurrentUserJETI())));
        add(ujuzenet);
    }
}
