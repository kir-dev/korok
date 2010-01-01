/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.valuation;

import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationPeriod;
import hu.sch.domain.ValuationStatistic;
import hu.sch.domain.ValuationStatus;
import hu.sch.web.components.customlinks.UserLink;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class ValuationDetails extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    private ValuationManagerLocal valuationManager;

    public ValuationDetails(Valuation valuation) {
        this(valuation, null);
    }

    public ValuationDetails(final Valuation valuation, final Page prevPage) {
        setHeaderLabelText("Leadott értékelés - részletes nézet");
        IModel<Valuation> model = new CompoundPropertyModel<Valuation>(valuation);

        setDefaultModel(model);
        /*Link backlink = new Link("backlink") {

        @Override
        public void onClick() {
        setResponsePage(prevPage);
        }
        };
        if (prevPage == null) {
        backlink.setVisible(false);
        }
        add(backlink);*/
        add(new Label("group.name"));
        if (valuation.getSender() != null) {
            add(new UserLink("sender", valuation.getSender()));
        } else {
            add(new Label("sender", "Nincs megadva"));
        }
        add(new Label("semester"));
        add(new Label("entrantStatus"));
        add(new Label("pointStatus"));

        add(new Link<EntrantRequestViewer>("entrantLink") {

            @Override
            public void onClick() {
                setResponsePage(new EntrantRequestViewer(valuation));
            }
        });
        add(new Link<PointRequestViewer>("pointLink") {

            @Override
            public void onClick() {
                setResponsePage(new PointRequestViewer(valuation));
            }
        });

        List<Long> ids = new ArrayList<Long>();
        ids.add(valuation.getId());
        List<ValuationStatistic> statList = valuationManager.getStatisztikaForErtekelesek(ids);
        ValuationStatistic stat = statList.iterator().next();
        add(new Label("stat.averagePont", new Model<Double>(stat.getAveragePoint())));
        add(new Label("stat.summaPoint", new Model<Long>(stat.getSummaPoint())));
        add(new Label("stat.givenKDO", new Model<Long>(stat.getGivenKDO())));
        add(new Label("stat.givenKB", new Model<Long>(stat.getGivenKB())));
        add(new Label("stat.givenAB", new Model<Long>(stat.getGivenAB())));

        final WebMarkupContainer container = new WebMarkupContainer("container");
        final MultiLineLabel textLabel = new MultiLineLabel("valuationText");
        container.add(textLabel);
        final Form<Valuation> form = new Form<Valuation>("textForm", new CompoundPropertyModel<Valuation>(valuation)) {

            @Override
            protected void onSubmit() {
                valuationManager.updateValuation(valuation.getId(), valuation.getValuationText());
                getSession().info("A féléves értékelés sikeresen frissítve.");
                setResponsePage(Valuations.class);
                return;
            }
        };
        //mivel nem lehet két azonos wicket:id
        TextArea<String> text = new TextArea<String>("valuationText2", new PropertyModel<String>(valuation, "valuationText"));
        text.setRequired(true);
        form.add(text);
        form.setVisible(false);

        container.add(form);
        container.setOutputMarkupId(true);
        add(container);

        AjaxFallbackLink ajaxLink = new AjaxFallbackLink("modifyText") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                textLabel.setVisible(false);
                form.setVisible(true);
                if (target != null) {
                    target.addComponent(container);
                }
            }
        };
        container.add(ajaxLink);
        if ((!systemManager.getErtekelesIdoszak().equals(ValuationPeriod.ERTEKELESLEADAS)
                || !systemManager.getSzemeszter().equals(valuation.getSemester())
                || (valuation.getPointStatus().equals(ValuationStatus.ELFOGADVA)
                && valuation.getEntrantStatus().equals(ValuationStatus.ELFOGADVA)))) {
            ajaxLink.setVisible(false);
        }

        add(DateLabel.forDatePattern("lastModified", "yyyy.MM.dd. kk:mm"));
        add(DateLabel.forDatePattern("lastConsidered", "yyyy.MM.dd. kk:mm"));
    }
}
