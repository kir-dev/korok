package hu.sch.web.kp.admin;

import hu.sch.services.SystemManagerLocal;
import hu.sch.web.PhoenixApplication;
import hu.sch.web.wicket.components.customlinks.CsvExportForKfbLink;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;

class KirDevFragment extends Fragment {
    private final boolean newbieTime;

    public KirDevFragment(String id, String markupId, final SystemManagerLocal systemManager) {
        super(id, markupId, null, null);

        this.newbieTime = systemManager.getNewbieTime();

        add(new BookmarkablePageLink<CreateGroup>("createGroup", CreateGroup.class));
        add(new CsvExportForKfbLink("csvExport"));
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

}
