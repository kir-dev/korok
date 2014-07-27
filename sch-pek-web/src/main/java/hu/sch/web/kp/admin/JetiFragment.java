package hu.sch.web.kp.admin;

import hu.sch.domain.Semester;
import hu.sch.domain.enums.EntrantType;
import hu.sch.domain.enums.ValuationPeriod;
import hu.sch.services.PointHistoryManagerLocal;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.NoSuchAttributeException;
import hu.sch.web.wicket.util.ByteArrayResourceStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.MissingResourceException;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
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

class JetiFragment extends Fragment {

    private static final Logger logger = LoggerFactory.getLogger(JetiFragment.class);

    private Semester semester;
    private ValuationPeriod valuationPeriod;
    private ValuationPeriod oldValuationPeriod;
    private final ValuationManagerLocal valuationManager;

    public JetiFragment(String id, String markupId, final SystemManagerLocal systemManager, final ValuationManagerLocal valuationManager, final PointHistoryManagerLocal pointHistoryManager) {
        super(id, markupId, null, null);
        this.valuationManager = valuationManager;

        try {
            semester = systemManager.getSzemeszter();
        } catch (NoSuchAttributeException e) {
            semester = new Semester();
        }

        oldValuationPeriod = systemManager.getErtekelesIdoszak();
        setValuationPeriod(oldValuationPeriod);

        Form<Semester> beallitasForm = new Form<Semester>("settingsForm") {
            @Override
            public void onSubmit() {
                try {
                    systemManager.setSzemeszter(getSemester());
                    systemManager.setErtekelesIdoszak(getValuationPeriod());
                    if (hasValuationPeriodChanged() && getValuationPeriod() == ValuationPeriod.NINCSERTEKELES) {
                        pointHistoryManager.generateForSemesterAsync(getSemester());
                    }
                    getSession().info(getLocalizer().getString("info.BeallitasokMentve", this));
                } catch (MissingResourceException e) {
                    getSession().error(getLocalizer().getString("err.BeallitasokFailed", this));
                    logger.error("Error while saving settings.", e);
                } finally {
                    oldValuationPeriod = getValuationPeriod();
                }
            }
        };

        beallitasForm.setModel(new CompoundPropertyModel<>(semester));
        final TextField<Integer> firstYear = new TextField<>("firstYear");
        beallitasForm.add(firstYear.add(new RangeValidator<>(2000, 2030)));
        final TextField<Integer> secondYear = new TextField<>("secondYear");
        beallitasForm.add(secondYear.add(new RangeValidator<>(2000, 2030)));
        beallitasForm.add(new CheckBox("isAutumn"));
        beallitasForm.add(new AbstractFormValidator() {
            @Override
            public FormComponent<?>[] getDependentFormComponents() {
                return new FormComponent[]{firstYear, secondYear};
            }

            @Override
            public void validate(Form<?> form) {
                if (Integer.parseInt(firstYear.getValue()) + 1 != Integer.parseInt(secondYear.getValue())) {
                    error(firstYear, "err.SzemeszterEvKulonbseg");
                }
            }
        });
        DropDownChoice<ValuationPeriod> ddc1 = new DropDownChoice<ValuationPeriod>("periodSelector", Arrays.asList(ValuationPeriod.values()));
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
        final RequiredTextField<Integer> howMuchKbInput = new RequiredTextField<Integer>("howMuchKbInput", Model.of(1), Integer.class);
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

    private void exportEntrantsAsCSV(final String fileName, final EntrantType entrantType, final Integer minEntrantNum) {
        try {
            String content = valuationManager.findApprovedEntrantsForExport(semester, entrantType, minEntrantNum);
            IResourceStream resourceStream = new ByteArrayResourceStream(content.getBytes(StandardCharsets.UTF_8), "text/csv");
            getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, fileName));
        } catch (Exception ex) {
            getSession().error(getLocalizer().getString("err.export", this));
            logger.error("Error while generating CSV export about " + entrantType.toString() + "s with " + minEntrantNum + " min value", ex);
        }
    }

    private boolean hasValuationPeriodChanged() {
        return oldValuationPeriod != getValuationPeriod();
    }
}
