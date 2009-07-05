/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.User;
import hu.sch.web.kp.pages.index.Index;
import hu.sch.web.kp.session.VirSession;
import hu.sch.web.kp.templates.SecuredPageTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class SelectGroup extends SecuredPageTemplate {

    Long id;
    public String selected = "";

    public SelectGroup() {
        super();
        setHeaderLabelText("Csoportválasztás");
        add(new FeedbackPanel("feedback"));

        /*        List<Group> csoportok =
        getSession().getUser().getGroups();*/

        if (id == null) {
            id = getSession().getUser().getId();
        }
        if (id == null) {
            setResponsePage(Index.class);
            return;
        }

        User user = userManager.findUserWithCsoporttagsagokById(id);
        user.sortMemberships();

        //ListView groups = new ListView("groupList",csoportok){
//        ListView groups = new ListView("groupList", user.getMemberships()) {
//
//            @Override
//            protected void populateItem(ListItem item) {
//                final Group cs = ((Membership) item.getModelObject()).getGroup();
//                Link l = new Link("selectgrouplink") {
//
//                    @Override
//                    public void onClick() {
//                        ((VirSession) getSession()).setGroup(cs);
//                        if (!continueToOriginalDestination()) {
//                            setResponsePage(Index.class);
//                        } else {
//                            return;
//                        }
//                    }
//                };
//                l.add(new Label("groupname", new PropertyModel(cs, "nev")));
//                item.add(l);
//            }
//        };
//        add(groups);

        final List<Membership> cstag = user.getMemberships();
        final ArrayList<String> csoportok = new ArrayList<String>();
        for (Membership tagsag : cstag) {
            csoportok.add(tagsag.getGroup().getName());
        }
        Form csoportForm = new Form("csoportform") {

            @Override
            protected void onSubmit() {
                Iterator<Membership> iterator = cstag.iterator();
                Group cs = null;
                while (iterator.hasNext()) {
                    cs = (iterator.next()).getGroup();
                    if (cs.getName().equals(selected)) {
                        ((VirSession)getSession()).setCsoport(cs);
                        break;
                    }
                }
                if (!continueToOriginalDestination()) {
                    setResponsePage(ShowGroup.class, new PageParameters("id=" + cs.getId().toString()));
                } else {
                    return;
                }
            }
        };
        DropDownChoice<String> ddc = new DropDownChoice<String>("groups", csoportok);

        ddc.setModel(new PropertyModel<String>(this, "selected"));
        csoportForm.add(ddc);
        add(csoportForm);

    }
}
