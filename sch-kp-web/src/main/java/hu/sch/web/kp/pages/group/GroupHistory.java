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
        final List<String> semesters = new ArrayList<String>();
        for (Valuation valuation : valuationList) {
            semesters.add(valuation.getSemester().toString());
        }
        
        add(new Label("name", group.getName()));
        DropDownChoice ddc = new DropDownChoice("semesters", new PropertyModel(this, "selected"), semesters)
        {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(final Object newSelection)
            {
                Iterator<Valuation> iterator = valuationList.iterator();
                while (iterator.hasNext())
                {
                    selectedValuation = iterator.next();
                    semester = selectedValuation.getSemester();
                    if (semester.toString().equals(selected))
                    {
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

        setDefaultModel(new CompoundPropertyModel(selectedValuation));

        valuationPanel = new ValuationDetailPanel("valuationInfo");
        valuationPanel.updateDatas(selectedValuation);
        valuationPanel.setVisible(false);
        add(valuationPanel);

        try
        {
            Semester s = new Semester();
            s.setId(parameters.getString("sid", ""));
            selected = s.toString();

            Iterator<Valuation> iterator = valuationList.iterator();
            while (iterator.hasNext())
            {
                selectedValuation = iterator.next();
                semester = selectedValuation.getSemester();
                if (semester.toString().equals(selected))
                {
                    setHeaderLabelText("A kör részletes pontozásai");
                    valuationPanel.updateDatas(selectedValuation);
                    valuationPanel.setVisible(true);
                    break;
                }
            }
        }
        catch (Exception e)
        {
        }
    }
}
