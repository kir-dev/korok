package hu.sch.web.kp.admin;

import hu.sch.domain.Semester;
import hu.sch.services.PointHistoryManagerLocal;
import hu.sch.services.SystemManagerLocal;
import hu.sch.web.PhoenixApplication;
import hu.sch.web.wicket.components.customlinks.CsvExportForKfbLink;
import javax.inject.Inject;
import org.apache.wicket.cdi.CdiContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;

class KirDevFragment extends Fragment {
    private final boolean newbieTime;

    @Inject
    private PointHistoryManagerLocal pointHistoryManager;
    @Inject
    private SystemManagerLocal systemManager;

    private Semester semester;

    public KirDevFragment(String id, String markupId) {
        super(id, markupId, null, null);
        CdiContainer.get().getNonContextualManager().inject(this);

        this.newbieTime = systemManager.getNewbieTime();
        this.semester = systemManager.getSzemeszter();

        add(new BookmarkablePageLink<CreateGroup>("createGroup", CreateGroup.class));
        add(new CsvExportForKfbLink("csvExport"));
        addNewbieTimeForm(systemManager);
        addPointHistoryGenerationForm();
    }

    private void addNewbieTimeForm(final SystemManagerLocal systemManager) {
        Form<Void> form = new Form<Void>("kirdevForm", new CompoundPropertyModel(this)) {
            @Override
            protected void onSubmit() {
                systemManager.setNewbieTime(newbieTime);
                ((PhoenixApplication) getApplication()).setNewbieTime(newbieTime);
                getSession().info(getLocalizer().getString("info.BeallitasokMentve", this));
            }
        };
        CheckBox newbieTimeCB = new CheckBox("newbieTime");
        form.add(newbieTimeCB);
        add(form);
    }

    private void addPointHistoryGenerationForm() {
        SemesterForm form = new SemesterForm("semesterForm") {

            @Override
            protected void onSubmit() {
                pointHistoryManager.generateForSemesterAsync(this.getSemester());
                getSession().info("Újragenerálás elindult!");
            }

        };
        form.setSemester(semester);
        add(form);
    }

}
