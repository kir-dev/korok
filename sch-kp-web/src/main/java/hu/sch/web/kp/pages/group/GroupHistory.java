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
package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
import hu.sch.domain.Valuation;
import hu.sch.domain.Semester;
import hu.sch.web.kp.pages.valuation.ValuationDetailPanel;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public class GroupHistory extends SecuredPageTemplate {

    @EJB(name = "ValuationManagerBean")
    private ValuationManagerLocal valuationManager;
    private List<Valuation> valuationList = new ArrayList<Valuation>();
    private Long id;
    private Group group;
    private Semester semester = null;
    private String selected = "";
    private Valuation selectedValuation = null;
    private ValuationDetailPanel valuationPanel;

    public GroupHistory() {
        getSession().error("Túl kevés paraméter!");
        throw new RestartResponseException(getApplication().getHomePage());
    }

    public GroupHistory(PageParameters parameters) {
        Object p = parameters.get("id");
        try {
            id = Long.parseLong(p.toString());
        } catch (NumberFormatException e) {
            getSession().error("Érvénytelen paraméter");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        setHeaderLabelText("Időszakválasztás");
        //add(new FeedbackPanel("pagemessages"));
        add(new BookmarkablePageLink<ShowGroup>("simpleView", ShowGroup.class, new PageParameters("id=" + id.toString())));

        group = userManager.findGroupById(id);
        if (group == null) {
            getSession().error("Hibás paraméter, nincs ilyen kör!");
            throw new RestartResponseException(getApplication().getHomePage());
        }
        setTitleText(group.getName() + " korábbi értékelései");

        valuationList.clear();
        valuationList.addAll(valuationManager.findErtekeles(group));
        final List<String> semesters = new ArrayList<String>();
        for (Valuation valuation : valuationList) {
            semesters.add(valuation.getSemester().toString());
        }

        add(new Label("name", group.getName()));
        DropDownChoice ddc = new DropDownChoice("semesters", new PropertyModel(this, "selected"), semesters) {

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(final Object newSelection) {
                Iterator<Valuation> iterator = valuationList.iterator();
                while (iterator.hasNext()) {
                    selectedValuation = iterator.next();
                    semester = selectedValuation.getSemester();
                    if (semester.toString().equals(selected)) {
//                        setHeaderLabelText("A kör részletes pontozásai");
//                        valuationPanel.updateDatas(selectedValuation);
//                        valuationPanel.setVisible(true);
                        break;
                    }
                }

                PageParameters pp = new PageParameters();
                pp.add("id", id.toString());
                pp.add("sid", semester.getId());
                setResponsePage(GroupHistory.class, pp);
            }
        };

        add(ddc);

        setDefaultModel(new CompoundPropertyModel<Valuation>(selectedValuation));

        valuationPanel = new ValuationDetailPanel("valuationInfo");
        valuationPanel.updateValuation(selectedValuation);
        valuationPanel.setVisible(false);
        add(valuationPanel);

        try {
            Semester s = new Semester();
            s.setId(parameters.getString("sid", ""));
            selected = s.toString();

            Iterator<Valuation> iterator = valuationList.iterator();
            while (iterator.hasNext()) {
                selectedValuation = iterator.next();
                semester = selectedValuation.getSemester();
                if (semester.toString().equals(selected)) {
                    setHeaderLabelText("A kör részletes pontozásai");
                    valuationPanel.updateValuation(selectedValuation);
                    valuationPanel.setVisible(true);
                    break;
                }
            }
        } catch (Exception e) {
        }
    }
}
