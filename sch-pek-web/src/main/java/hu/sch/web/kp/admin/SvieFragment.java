package hu.sch.web.kp.admin;

import hu.sch.web.kp.svie.SvieGroupMgmt;
import hu.sch.web.kp.svie.SvieUserMgmt;
import hu.sch.web.wicket.components.customlinks.CsvReportLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;

class SvieFragment extends Fragment {

    public SvieFragment(String id, String markupId) {
        super(id, markupId, null, null);
        add(new BookmarkablePageLink<SvieUserMgmt>("userMgmt", SvieUserMgmt.class));
        add(new BookmarkablePageLink<SvieGroupMgmt>("groupMgmt", SvieGroupMgmt.class));
        add(new CsvReportLink("csvReport"));
    }

}
