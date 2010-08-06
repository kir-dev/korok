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

import hu.sch.domain.ConsideredValuation;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationData;
import hu.sch.domain.ValuationPeriod;
import hu.sch.domain.ValuationStatistic;
import hu.sch.domain.ValuationStatus;
import hu.sch.web.wicket.components.customlinks.UserLink;
import hu.sch.web.kp.KorokPage;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.consider.ConsiderExplainPanel;
import hu.sch.web.kp.consider.ConsiderPage;
import hu.sch.web.wicket.components.TinyMCEContainer;
import hu.sch.web.wicket.components.tables.ValuationTableForGroup;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;
import wicket.contrib.tinymce.settings.TinyMCESettings;

/**
 * Egy csoporthoz tartozó adott félévhez köthető értékelés teljes megtekintése,
 * statisztikától, pont- és belépőkérelmektől kezdve az elbírálásig.
 *
 * @author  hege
 * @author  messo
 */
public class ValuationDetails extends KorokPage {

    @EJB(name = "ValuationManagerBean")
    private ValuationManagerLocal valuationManager;
    private AjaxFallbackLink<Void> ajaxLinkForValuationText;
    private AjaxFallbackLink<Void> ajaxLinkForPrinciple;

    public ValuationDetails(PageParameters params) {
        Valuation valuation = null;
        Long id = params.getAsLong("id");
        if (id == null || (valuation = valuationManager.findErtekelesById(id)) == null) {
            getSession().error("Nincs ilyen értékelés!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        // jogosultság ellenőrzés; akkor láthatja ezt a lapot, ha
        // 1. a csoport körvezetője, vagy
        // 2. JETI és elbírálási időszak van
        if (isUserGroupLeader(valuation.getGroup())
                || (isCurrentUserJETI() && systemManager.getErtekelesIdoszak() == ValuationPeriod.ERTEKELESELBIRALAS)) {
            // lássuk a medvét
            init(valuation);
        } else {
            getSession().error(getLocalizer().getString("err.NincsJog", null));
            throw new RestartResponseException(getApplication().getHomePage());
        }
    }

    public ValuationDetails(final Valuation valuation, final Page prevPage) {
        init(valuation);
    }

    private void init(final Valuation valuation) {
        setHeaderLabelText("Leadott értékelés - részletes nézet");
        setTitleText(String.format("Értékelések - %s (%s)", valuation.getGroup().getName(), valuation.getSemester()));

        setDefaultModel(new CompoundPropertyModel<Valuation>(valuation));

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

        addValuationText(valuation);
        addPrinciple(valuation);

        //TinyMCE csak akkor működik AJAXszal, ha már az oldal betöltődésekor be
        //van töltve a js :(
        add(new HeaderContributor(new IHeaderContributor() {

            @Override
            public void renderHead(IHeaderResponse response) {
                response.renderJavascriptReference(TinyMCESettings.javaScriptReference());
            }
        }));

        // Nem szerkeszthet az értékelés szövegén/pontozási elveken, ha
        // 1. már nincsen értékelés leadás
        // 2. egy régebbi félévről származik
        // 3. ha a pontozást és belépőkérelmeket elfogadták
        if ((!systemManager.getErtekelesIdoszak().equals(ValuationPeriod.ERTEKELESLEADAS)
                || !systemManager.getSzemeszter().equals(valuation.getSemester())
                || (valuation.getPointStatus().equals(ValuationStatus.ELFOGADVA)
                && valuation.getEntrantStatus().equals(ValuationStatus.ELFOGADVA)))) {
            ajaxLinkForValuationText.setVisible(false);
            ajaxLinkForPrinciple.setVisible(false);
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
            add(new EmptyPanel("jeti"));
        }
    }

    private void addValuationText(final Valuation valuation) {
        // TODO -- ehhez és a principle-hez generálni egy AjaxEditableMultiLineLabelUsingTinyMCE-t.
        final WebMarkupContainer container = new WebMarkupContainer("valuationTextContainer");
        container.setOutputMarkupId(true);
        add(container);

        // Szöveges értékelés
        final MultiLineLabel label = new MultiLineLabel("valuationText");
        label.setEscapeModelStrings(false);
        container.add(label);

        final Form<Valuation> form = new Form<Valuation>("valuationTextForm", new CompoundPropertyModel<Valuation>(valuation));
        form.setVisible(false);
        container.add(form);

        form.add(new AjaxButton("saveValuationText", form) {

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                add(new TinyMceAjaxSubmitModifier());
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Valuation valuation = (Valuation) form.getModelObject();
                valuationManager.updateValuationText(valuation);
                label.setVisible(true);
                form.setVisible(false);
                if (target != null) {
                    target.addComponent(container);
                }
            }
        });

        container.add(ajaxLinkForValuationText = new AjaxFallbackLink<Void>("modifyValuationText") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                label.setVisible(false);
                form.setVisible(true);
                if (target != null) {
                    target.addComponent(container);
                }
            }
        });

        // Szöveges értékelése TinyMCE doboza
        final TinyMCEContainer tinyMce = new TinyMCEContainer("valuationText2", new PropertyModel<String>(valuation, "valuationText"), true);
        form.add(tinyMce);
    }

    private void addPrinciple(final Valuation valuation) {
        final WebMarkupContainer container = new WebMarkupContainer("principleContainer");
        container.setOutputMarkupId(true);
        add(container);

        // Szöveges értékelés
        final MultiLineLabel label = new MultiLineLabel("principle");
        label.setEscapeModelStrings(false);
        container.add(label);

        final Form<Valuation> form = new Form<Valuation>("principleForm", new CompoundPropertyModel<Valuation>(valuation));
        form.setVisible(false);
        container.add(form);

        form.add(new AjaxButton("savePrinciple", form) {

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                add(new TinyMceAjaxSubmitModifier());
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Valuation valuation = (Valuation) form.getModelObject();
                valuationManager.updatePrinciple(valuation);
                label.setVisible(true);
                form.setVisible(false);
                if (target != null) {
                    target.addComponent(container);
                }
            }
        });

        container.add(ajaxLinkForPrinciple = new AjaxFallbackLink<Void>("modifyPrinciple") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                label.setVisible(false);
                form.setVisible(true);
                if (target != null) {
                    target.addComponent(container);
                }
            }
        });

        // Szöveges értékelése TinyMCE doboza
        final TinyMCEContainer tinyMce = new TinyMCEContainer("principle2", new PropertyModel<String>(valuation, "principle"), true);
        form.add(tinyMce);
    }
}
