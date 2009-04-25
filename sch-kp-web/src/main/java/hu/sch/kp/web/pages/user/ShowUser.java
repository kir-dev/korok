/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.user;

import hu.sch.domain.Csoport;
import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.web.pages.group.GroupHierarchy;
import hu.sch.kp.web.pages.group.ShowGroup;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class ShowUser extends SecuredPageTemplate {

    Long id;
    private boolean own_profile = false;
    private Csoport addToCsoportSelected;

    public ShowUser() {
        own_profile = true;
        initComponents();
    }

    public void initComponents() {
        try {
            if (id == null) {
                id = getSession().getUser().getId();
            }
        } catch (Exception e) {
            id = null;
        }
        if (id == null) {
            info("Egy körben sem vagy tag");
            setResponsePage(GroupHierarchy.class);
            return;
        }

//        List<Csoport> csoports = userManager.findGroupByName("%KIR%");
//        if (csoports != null) {
//            for (Csoport csoport : csoports) {
//                System.out.println(csoport.getNev());
//            }
//        }

        final Felhasznalo user = userManager.findUserWithCsoporttagsagokById(id);
        if (user == null) {
            info("Egy körben sem vagy tag");
            setResponsePage(GroupHierarchy.class);
            return;
        }
        setModel(new CompoundPropertyModel(user));
        setHeaderLabelText(user.getNev() + " felhasználó lapja");
        if (own_profile) {
            add(new BookmarkablePageLink("detailView", UserHistory.class));
        } else {
            add(new BookmarkablePageLink("detailView", UserHistory.class,
                    new PageParameters("id=" + user.getId().toString())));
        }

        /* add(new BookmarkablePageLink(
        "historylink", UserHistory.class,
        new PageParameters("id=" + id.toString())));*/

        add(new ExternalLink("profilelink",
                "https://idp.sch.bme.hu/profile/show/virid/" + id.toString()));
        user.sortCsoporttagsagok();
        ListView csoptagsagok = new ListView("csoptagsag", user.getCsoporttagsagok()) {

            @Override
            protected void populateItem(ListItem item) {
                Csoporttagsag cs = (Csoporttagsag) item.getModelObject();
                item.setModel(new CompoundPropertyModel(cs));
                BookmarkablePageLink csoplink =
                        new BookmarkablePageLink("csoplink", ShowGroup.class,
                        new PageParameters("id=" +
                        cs.getCsoport().getId().toString()));
                csoplink.add(new Label("csoport.nev"));
                item.add(csoplink);
                item.add(new Label("jogok", getConverter(TagsagTipus.class).convertToString(cs.getJogokString(), getLocale())));
                item.add(DateLabel.forDatePattern("kezdet", "yyyy.MM.dd."));
                item.add(DateLabel.forDatePattern("veg", "yyyy.MM.dd."));
            }
        };
        add(csoptagsagok);

        List<Csoport> csoportok = getFelhasznalo().getCsoportok();
        List<Csoport> korvezetoicsoportok = new LinkedList<Csoport>();
        for (Csoport cs : csoportok) {
            if (hasUserRoleInGroup(cs, TagsagTipus.KORVEZETO) &&
                    !user.getCsoportok().contains(cs)) {
                korvezetoicsoportok.add(cs);
            }
        }

        final DropDownChoice csoport = new DropDownChoice("csoport",
                new PropertyModel(this, "addToCsoportSelected"), korvezetoicsoportok);
        Form csoportbaFelvetel = new Form("csoportbaFelvetel") {

            @Override
            protected void onSubmit() {
                userManager.addUserToGroup(user, addToCsoportSelected, new Date(), null);
                getSession().info("A felhasználó a csoportba felvéve");
                setResponsePage(ShowGroup.class, new PageParameters("id=" +
                        addToCsoportSelected.getId()));
            }
        };

        csoportbaFelvetel.add(csoport);
        add(csoportbaFelvetel);
        csoportbaFelvetel.setVisible(korvezetoicsoportok.size() > 0 &&
                hasUserRoleInSomeGroup(TagsagTipus.KORVEZETO));
    }

    public ShowUser(PageParameters parameters) {
        try {
            id = parameters.getLong("id");
        } catch (Throwable t) {
            t.printStackTrace();
        }
        initComponents();
    }

    public Csoport getAddToCsoportSelected() {
        return addToCsoportSelected;
    }

    public void setAddToCsoportSelected(Csoport addToCsoportSelected) {
        this.addToCsoportSelected = addToCsoportSelected;
    }
}
