/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        }
    }
}
