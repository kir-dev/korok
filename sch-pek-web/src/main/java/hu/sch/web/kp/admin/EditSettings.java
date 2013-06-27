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
package hu.sch.web.kp.admin;

import hu.sch.domain.EntrantType;
import hu.sch.domain.Semester;
import hu.sch.domain.ValuationPeriod;
import hu.sch.services.ImageManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.NoSuchAttributeException;
import hu.sch.web.PhoenixApplication;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.svie.SvieGroupMgmt;
import hu.sch.web.kp.svie.SvieUserMgmt;
import hu.sch.web.wicket.components.customlinks.CsvExportForKfbLink;
import hu.sch.web.wicket.components.customlinks.CsvReportLink;
import hu.sch.web.wicket.util.ByteArrayResourceStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.ejb.EJB;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.validation.validator.RangeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
public class EditSettings extends KorokPage {

    private static Logger logger = LoggerFactory.getLogger(EditSettings.class);

    public EditSettings() {
        //Jogosultságellenőrzés
        if (!(isCurrentUserJETI() || isCurrentUserSVIE() || isCurrentUserAdmin())) {
            getSession().error("Nincs jogod a megadott művelethez");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        setHeaderLabelText("Adminisztráció");

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

        @EJB(name = "ValuationManagerBean")
        protected ValuationManagerLocal valuationManager;
        private Semester semester;
        ValuationPeriod valuationPeriod;

        public ValuationPeriod getValuationPeriod() {
            return valuationPeriod;
        }

        public final void setValuationPeriod(ValuationPeriod period) {
            this.valuationPeriod = period;
        }

        public Semester getSemester() {
            return semester;
        }

        /**
         * Összeállítja a letölthető export fájlnevét a következő mezőkkel:
         * <pre>vir_[entrant]-[sem1st]-[sem2nd]-[osz|tavasz]-[ev]-[honap]-[nap].csv</pre>
         *
         * @param entrant belépő típusa
         * @return
         */
        private String getExportFileName(final EntrantType entrant) {
            StringBuilder sb = new StringBuilder("vir_");
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            sb.append(entrant.toString()).append("-");
            sb.append(semester.getFirstYear().toString()).append("-");
            sb.append(semester.getSecondYear().toString()).append("-");
            if (semester.isAutumn()) {
                sb.append("osz");
            } else {
                sb.append("tavasz");
            }
            sb.append("-");
            sb.append(sdf.format(date));
            sb.append(".csv");

            return sb.toString();
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
                        logger.error("Error while saving settings.", e);
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

                @Override
                public FormComponent<?>[] getDependentFormComponents() {
                    return new FormComponent[]{firstYear, secondYear};
                }

                @Override
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

                @Override
                public Object getDisplayValue(ValuationPeriod object) {
                    return getLocalizer().getString("ertekelesidoszak." + object.toString(), getParent());
                }

                @Override
                public String getIdValue(ValuationPeriod object, int index) {
                    return object.toString();
                }
            });
            beallitasForm.add(ddc1);
            add(beallitasForm);

            // exportok kérése
            // ismétlődés nélkül, neptun kóddal, elsődleges körrel, indoklással, email címmel

            // x vagy annál több kb-t kapott emberek listája (x állítható)
            final RequiredTextField<Integer> howMuchKbInput =
                    new RequiredTextField<Integer>("howMuchKbInput", Model.of(1), Integer.class);

            Form<Void> howMuchKbExportForm = new Form<Void>("howMuchKbExportForm") {

                @Override
                public void onSubmit() {
                    exportEntrantsAsCSV(getExportFileName(EntrantType.KB), EntrantType.KB, howMuchKbInput.getConvertedInput());
                }
            };
            howMuchKbInput.add(new RangeValidator<Integer>(1, 30));
            howMuchKbExportForm.add(howMuchKbInput);
            add(howMuchKbExportForm);

            // áb-s lista
            add(new Link<Void>("givenAbListExportLink") {

                @Override
                public void onClick() {
                    exportEntrantsAsCSV(getExportFileName(EntrantType.AB), EntrantType.AB, 1);
                }
            });
        }

        private void exportEntrantsAsCSV(final String fileName, final EntrantType entrantType, final Integer minEntrantNum) {
            try {
                String content = valuationManager.findApprovedEntrantsForExport(
                        semester, entrantType, minEntrantNum);

                IResourceStream resourceStream = new ByteArrayResourceStream(
                        content.getBytes("UTF-8"), "text/csv");
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, fileName));
            } catch (Exception ex) {
                getSession().error(getLocalizer().getString("err.export", this));
                logger.error("Error while generating CSV export about "
                        + entrantType.toString() + "s with " + minEntrantNum + " min value", ex);
            }
        }
    }

    private static class SvieFragment extends Fragment {

        public SvieFragment(String id, String markupId) {
            super(id, markupId, null, null);
            add(new BookmarkablePageLink<SvieUserMgmt>("userMgmt", SvieUserMgmt.class));
            add(new BookmarkablePageLink<SvieGroupMgmt>("groupMgmt", SvieGroupMgmt.class));
            add(new CsvReportLink("csvReport"));
        }
    }

    private class KirDevFragment extends Fragment {

        @EJB(name = "ImageManagerBean")
        private ImageManagerLocal imageManager;
        private boolean newbieTime = systemManager.getNewbieTime();
        private String spotDir;

        public KirDevFragment(String id, String markupId) {
            super(id, markupId, null, null);
            add(new BookmarkablePageLink<ShowInactive>("showinactive", ShowInactive.class));
            add(new BookmarkablePageLink<CreateGroup>("createGroup", CreateGroup.class));
            add(new BookmarkablePageLink<CreateNewPerson>("createPerson", CreateNewPerson.class));
            add(new CsvExportForKfbLink("csvExport"));

            Form<Void> form = new Form<Void>("kirdevForm", new CompoundPropertyModel(this)) {

                @Override
                protected void onSubmit() {
                    if (spotDir != null && !spotDir.isEmpty()) {
                        try {
                            imageManager.loadImages(spotDir);
                        } catch (Exception ex) {
                            getSession().error(ex.getMessage());
                            logger.error("Unable to load spot images from directory: " + spotDir, ex);
                            return;
                        }
                    }
                    systemManager.setNewbieTime(newbieTime);
                    ((PhoenixApplication) getApplication()).setNewbieTime(newbieTime);
                    getSession().info(getLocalizer().getString("info.BeallitasokMentve", this));
                }
            };
            TextField<String> spotDirTF = new TextField<String>("spotDir");
            CheckBox newbieTimeCB = new CheckBox("newbieTime");
            form.add(spotDirTF, newbieTimeCB);
            add(form);
        }
    }
}
