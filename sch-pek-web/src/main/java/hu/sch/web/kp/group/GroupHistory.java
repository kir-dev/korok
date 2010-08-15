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

package hu.sch.web.kp.group;

import hu.sch.domain.Group;
import hu.sch.domain.Valuation;
import hu.sch.domain.Semester;
import hu.sch.web.kp.valuation.ValuationDetailPanel;
import hu.sch.web.kp.KorokPage;
import hu.sch.services.ValuationManagerLocal;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;

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
        id = parameters.getAsLong("id");
        if (id == null) {
            getSession().error("Érvénytelen paraméter!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        group = userManager.findGroupById(id);
        if (group == null) {
            getSession().error("Hibás paraméter, nincs ilyen kör!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        add(new BookmarkablePageLink<ShowGroup>("simpleView", ShowGroup.class, new PageParameters("id=" + id.toString())));

        List<Valuation> valuationList = valuationManager.findErtekeles(group);

        // nézzük meg, hogy van-e kijelölve értékelés
        Semester semester = new Semester(parameters.getString("sid", ""));
        if (semester.isValid()) {
            for (Valuation valuation : valuationList) {
                if (valuation.getSemester().equals(semester)) {
                    selected = valuation;
                    break;
                }
            }
        }

        ValuationDetailPanel valuationPanel = new ValuationDetailPanel("valuationInfo");
        if (selected != null) {
            setHeaderLabelText("A kör részletes pontozásai");
            setTitleText(String.format("%s korábbi értékelései (%s)", group.getName(), semester.toString()));
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
