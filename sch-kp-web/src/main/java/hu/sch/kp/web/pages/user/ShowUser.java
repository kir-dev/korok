/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.user;

import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.pages.group.GroupHierarchy;
import hu.sch.kp.web.pages.group.ShowGroup;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.session.VirSession;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class ShowUser extends SecuredPageTemplate {

    @EJB(name = "ejb/UserManagerLocal")
    UserManagerLocal userManager;
    Long id;

    public ShowUser() {
        initComponents();
    }

    public void initComponents() {
        if (id == null) {
            id = ((VirSession) getSession()).getUser().getId();
        }
        if (id == null) {
            setResponsePage(Index.class);
            return;
        }

        Felhasznalo user = userManager.findUserWithCsoporttagsagokById(id);
        if (user == null) {
            info("Egy körben sem vagy tag");
            setResponsePage(GroupHierarchy.class);
            return;
        }
        System.out.println(user.toString());
        setModel(new CompoundPropertyModel(user));
        setHeaderLabelText(user.getNev() + " felhasználó lapja");

        /* add(new BookmarkablePageLink(
        "historylink", UserHistory.class,
        new PageParameters("id=" + id.toString())));

        add(new ExternalLink("profilelink",
        "https://idp.sch.bme.hu/profile/show/virid/" + id.toString()));*/
        user.sortCsoporttagsagok();
        ListView csoptagsagok = new ListView("csoptagsag", user.getCsoporttagsagok()) {

            @Override
            protected void populateItem(ListItem item) {
                Csoporttagsag cs = (Csoporttagsag) item.getModelObject();
                item.setModel(new CompoundPropertyModel(cs));
                BookmarkablePageLink csoplink =
                        new BookmarkablePageLink("csoplink", ShowGroup.class,
                        new PageParameters("id=" + cs.getCsoport().getId().toString()));
                csoplink.add(new Label("csoport.nev"));
                item.add(csoplink);
                item.add(new Label("jogok", getConverter(TagsagTipus.class).convertToString(cs.getJogokString(), getLocale())));
                item.add(DateLabel.forDatePattern("kezdet", "yyyy.MM.dd."));
                item.add(DateLabel.forDatePattern("veg", "yyyy.MM.dd."));
            }
        };
        add(csoptagsagok);
    }

    public ShowUser(PageParameters parameters) {
        try {
            id = parameters.getLong("id");
        } catch (Throwable t) {
            t.printStackTrace();
        }

        initComponents();
    }
}
