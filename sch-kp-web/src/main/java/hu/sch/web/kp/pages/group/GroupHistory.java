/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import org.apache.wicket.markup.html.form.Form;
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
        throw new RestartResponseException(GroupHierarchy.class);
    }

    public GroupHistory(PageParameters parameters) {
        Object p = parameters.get("id");
        try {
            id = Long.parseLong(p.toString());
        } catch (NumberFormatException e) {
            getSession().error("Érvénytelen paraméter");
            throw new RestartResponseException(GroupHierarchy.class);
        }

        setHeaderLabelText("Időszakválasztás");
        //add(new FeedbackPanel("pagemessages"));
        add(new BookmarkablePageLink("simpleView", ShowGroup.class, new PageParameters("id=" + id.toString())));

        group = userManager.findGroupById(id);
        valuationList.clear();
        valuationList.addAll(valuationManager.findApprovedValuations(group));
        final List<Semester> semesters = new ArrayList<Semester>();
        for (Valuation valuation : valuationList) {
            semesters.add(valuation.getSemester());
        }
        Form periodForm = new Form("periodForm") {

            @Override
            public void onSubmit() {
                Iterator<Valuation> iterator = valuationList.iterator();
                while (iterator.hasNext()) {
                    selectedValuation = iterator.next();
                    semester = selectedValuation.getSemester();
                    if (semester.toString().equals(selected)) {
                        setHeaderLabelText("A kör részletes pontozásai");
                        valuationPanel.updateDatas(selectedValuation);
                        valuationPanel.setVisible(true);
                        break;
                    }
                }
            }
        };
        add(new Label("name", group.getName()));
        DropDownChoice<Semester> ddc = new DropDownChoice<Semester>("semesters", semesters);
        ddc.setModel(new PropertyModel<Semester>(this, "selected"));

        periodForm.add(ddc);
        add(periodForm);
        setDefaultModel(new CompoundPropertyModel<Valuation>(selectedValuation));

        valuationPanel = new ValuationDetailPanel("valuationInfo");
        valuationPanel.updateDatas(selectedValuation);
        valuationPanel.setVisible(false);
        add(valuationPanel);
    }
}