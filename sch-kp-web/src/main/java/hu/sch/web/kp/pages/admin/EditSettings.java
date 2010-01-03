/**
 * Copyright (c) 2009, Peter Major
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
package hu.sch.web.kp.pages.admin;

import hu.sch.domain.ValuationPeriod;
import hu.sch.domain.Semester;
import hu.sch.services.exceptions.NoSuchAttributeException;
import hu.sch.web.components.customlinks.CsvReportLink;
import hu.sch.web.kp.pages.svie.SvieGroupMgmt;
import hu.sch.web.kp.pages.svie.SvieUserMgmt;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.util.Arrays;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;

/**
 *
 * @author aldaris
 */
public class EditSettings extends SecuredPageTemplate {

    public EditSettings() {
        //Jogosultságellenőrzés
        if (!(isCurrentUserJETI() || isCurrentUserSVIE() || isCurrentUserAdmin())) {
            getSession().error("Nincs jogod a megadott művelethez");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        setHeaderLabelText("Adminisztráció");
        add(new FeedbackPanel("pagemessages"));

        JetiFragment jetiFragment = new JetiFragment("jetifragment", "jetipanel");
        SvieFragment svieFragment = new SvieFragment("sviefragment", "sviepanel");
        KirDevFragment kirDevFragment = new KirDevFragment("kirdevfragment", "kirdevpanel");
        jetiFragment.setVisible(false);
        svieFragment.setVisible(false);
        kirDevFragment.setVisible(false);
        add(jetiFragment, svieFragment, kirDevFragment);

        if (isCurrentUserJETI()) {
            jetiFragment.setVisible(true);
        }

        if (isCurrentUserSVIE()) {
            svieFragment.setVisible(true);
        }
        if (isCurrentUserAdmin()) {
            kirDevFragment.setVisible(true);
        }
    }

    private class JetiFragment extends Fragment {

        private Semester semester;
        ValuationPeriod valuationPeriod;

        public ValuationPeriod getValuationPeriod() {
            return valuationPeriod;
        }

        public void setValuationPeriod(ValuationPeriod period) {
            this.valuationPeriod = period;
        }

        public Semester getSemester() {
            return semester;
        }

        public JetiFragment(String id, String markupId) {
            super(id, markupId, null, null);

            try {
                semester = systemManager.getSzemeszter();
            } catch (NoSuchAttributeException e) {
                semester = new Semester();
            }

            setValuationPeriod(systemManager.getErtekelesIdoszak());

            Form<Semester> beallitasForm = new Form<Semester>("settingsForm") {

                @Override
                public void onSubmit() {
                    try {
                        systemManager.setSzemeszter(getSemester());
                        systemManager.setErtekelesIdoszak(getValuationPeriod());
                        getSession().info(getLocalizer().getString("info.BeallitasokMentve", this));
                    } catch (Exception e) {
                        getSession().error(getLocalizer().getString("err.BeallitasokFailed", this));
                        e.printStackTrace();
                    }
                }
            };
            beallitasForm.setModel(new CompoundPropertyModel<Semester>(semester));
            final TextField<Integer> firstYear = new TextField<Integer>("firstYear");
            beallitasForm.add(firstYear.add(new RangeValidator<Integer>(2000, 2030)));
            final TextField<Integer> secondYear = new TextField<Integer>("secondYear");
            beallitasForm.add(secondYear.add(new RangeValidator<Integer>(2000, 2030)));
            beallitasForm.add(new CheckBox("isAutumn"));
            beallitasForm.add(new AbstractFormValidator() {

                public FormComponent<?>[] getDependentFormComponents() {
                    return new FormComponent[]{firstYear, secondYear};
                }

                public void validate(Form<?> form) {
                    if (Integer.parseInt(firstYear.getValue()) + 1
                            != Integer.parseInt(secondYear.getValue())) {
                        error(firstYear, "err.SzemeszterEvKulonbseg");
                    }
                }
            });
            DropDownChoice<ValuationPeriod> ddc1 = new DropDownChoice<ValuationPeriod>("periodSelector",
                    Arrays.asList(ValuationPeriod.values()));
            ddc1.setRequired(true);
            ddc1.setModel(new PropertyModel<ValuationPeriod>(this, "valuationPeriod"));

            ddc1.setChoiceRenderer(new IChoiceRenderer<ValuationPeriod>() {

                public Object getDisplayValue(ValuationPeriod object) {
                    return getLocalizer().getString("ertekelesidoszak." + object.toString(), getParent());
                }

                public String getIdValue(ValuationPeriod object, int index) {
                    return object.toString();
                }
            });
            beallitasForm.add(ddc1);
            add(beallitasForm);
        }
    }

    private class SvieFragment extends Fragment {

        public SvieFragment(String id, String markupId) {
            super(id, markupId, null, null);
            add(new BookmarkablePageLink<SvieUserMgmt>("userMgmt", SvieUserMgmt.class));
            add(new BookmarkablePageLink<SvieGroupMgmt>("groupMgmt", SvieGroupMgmt.class));
            add(new CsvReportLink("csvPanel"));
        }
    }

    private class KirDevFragment extends Fragment {

        public KirDevFragment(String id, String markupId) {
            super(id, markupId, null, null);
            add(new BookmarkablePageLink<ShowInactive>("showinactive", ShowInactive.class));
            add(new BookmarkablePageLink<CreateGroup>("createGroup", CreateGroup.class));
            add(new BookmarkablePageLink<CreateNewPerson>("createPerson", CreateNewPerson.class));
        }
    }
}
