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
package hu.sch.web.kp.pages.valuation;

import hu.sch.domain.ConsideredValuation;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationData;
import hu.sch.domain.ValuationPeriod;
import hu.sch.domain.ValuationStatistic;
import hu.sch.domain.ValuationStatus;
import hu.sch.web.wicket.components.customlinks.UserLink;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.pages.consider.ConsiderExplainPanel;
import hu.sch.web.kp.pages.consider.ConsiderPage;
import hu.sch.web.wicket.components.TinyMCEContainer;
import hu.sch.web.wicket.components.tables.ValuationTableForGroup;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * Egy csoporthoz tartozó adott félévhez köthető értékelés teljes megtekintése,
 * statisztikától, pont- és belépőkérelmektől kezdve az elbírálásig.
 *
 * @author  hege
 * @author  messo
 */
public class ValuationDetails extends SecuredPageTemplate {

    @EJB(name = "ValuationManagerBean")
    private ValuationManagerLocal valuationManager;

    public ValuationDetails(Valuation valuation) {
        this(valuation, null);
    }

    public ValuationDetails(final Valuation valuation, final Page prevPage) {
        setHeaderLabelText("Leadott értékelés - részletes nézet");
        setTitleText(String.format("Értékelések - %s (%s)", valuation.getGroup().getName(), valuation.getSemester()));
        IModel<Valuation> model = new CompoundPropertyModel<Valuation>(valuation);

        setDefaultModel(model);

        // Főbb adatok
        add(new Label("group.name"));
        add(new Label("semester"));
        if (valuation.getSender() != null) {
            add(new UserLink("sender", valuation.getSender()));
        } else {
            add(new Label("sender", "Nincs megadva"));
        }
        add(DateLabel.forDatePattern("lastModified", "yyyy. MM. dd. kk:mm"));
        add(DateLabel.forDatePattern("lastConsidered", "yyyy. MM. dd. kk:mm"));
        add(new Label("entrantStatus"));
        add(new Label("pointStatus"));

        // Staisztika
        ValuationStatistic stat = valuationManager.getStatisticForValuation(valuation.getId());
        add(new Label("stat.averagePont", new Model<Double>(stat.getAveragePoint())));
        add(new Label("stat.summaPoint", new Model<Long>(stat.getSummaPoint())));
        add(new Label("stat.givenKDO", new Model<Long>(stat.getGivenKDO())));
        add(new Label("stat.givenKB", new Model<Long>(stat.getGivenKB())));
        add(new Label("stat.givenAB", new Model<Long>(stat.getGivenAB())));

        final List<ValuationData> igenylista = valuationManager.findRequestsForValuation(valuation.getId());
        add(new ValuationTableForGroup("requests", igenylista).getDataTable());

        // Szöveges értékelés
        final WebMarkupContainer container = new WebMarkupContainer("container");
        final MultiLineLabel textLabel = new MultiLineLabel("valuationText");
        textLabel.setEscapeModelStrings(false);
        container.add(textLabel);
        final Form<Valuation> form = new Form<Valuation>("textForm", new CompoundPropertyModel<Valuation>(valuation)) {

            @Override
            protected void onSubmit() {
                valuationManager.updateValuation(valuation.getId(), valuation.getValuationText());
                getSession().info("A féléves értékelés sikeresen frissítve.");
                setResponsePage(Valuations.class);
            }
        };

        //TinyMCE csak akkor működik AJAXszal, ha már az oldal betöltődésekor be
        //van töltve a js :(
        add(new HeaderContributor(new IHeaderContributor() {

            @Override
            public void renderHead(IHeaderResponse response) {
                response.renderJavascriptReference(TinyMCESettings.javaScriptReference());
            }
        }));
        //mivel nem lehet két azonos wicket:id
        TinyMCEContainer tinyMce = new TinyMCEContainer("valuationText2", new PropertyModel<String>(valuation, "valuationText"), true);
        form.add(tinyMce);
        form.setVisible(false);

        container.add(form);
        container.setOutputMarkupId(true);
        add(container);

        AjaxFallbackLink<Void> ajaxLink;
        container.add(ajaxLink = new AjaxFallbackLink<Void>("modifyText") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                textLabel.setVisible(false);
                form.setVisible(true);
                if (target != null) {
                    target.addComponent(container);
                }
            }
        });

        // Nem szerkeszthet az értékelés szövegén, ha
        // 1. már nincsen értékelés leadás
        // 2. egy régebbi félévről származik
        // 3. ha a pontozást és belépőkérelmeket elfogadták
        if ((!systemManager.getErtekelesIdoszak().equals(ValuationPeriod.ERTEKELESLEADAS)
                || !systemManager.getSzemeszter().equals(valuation.getSemester())
                || (valuation.getPointStatus().equals(ValuationStatus.ELFOGADVA)
                && valuation.getEntrantStatus().equals(ValuationStatus.ELFOGADVA)))) {
            ajaxLink.setVisible(false);
        }

        // Elbírálás
        if (isCurrentUserJETI() && systemManager.getErtekelesIdoszak() == ValuationPeriod.ERTEKELESELBIRALAS) {
            ConsideredValuation cv = new ConsideredValuation(valuation);
            Panel jetifragment = new ConsiderExplainPanel("jeti", cv) {

                @Override
                public void onSubmit(ConsideredValuation underConsider) {
                    ArrayList<ConsideredValuation> list = new ArrayList<ConsideredValuation>(1);
                    list.add(underConsider);
                    if (valuationManager.ertekeleseketElbiral(list, getUser())) {
                        getSession().info("Az elbírálás sikeres volt.");
                        setResponsePage(ConsiderPage.class);
                    } else {
                        getSession().error("Minden elutasított értékeléshez kell indoklást mellékelni!");
                    }
                }
            };
            add(jetifragment);
        } else {
            add(new Label("jeti", ""));
        }
    }
}
