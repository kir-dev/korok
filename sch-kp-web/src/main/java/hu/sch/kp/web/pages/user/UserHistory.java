/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.user;

import hu.sch.domain.BelepoIgeny;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.PontIgeny;
import hu.sch.domain.Szemeszter;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author Adam Lantos
 */
public class UserHistory extends SecuredPageTemplate {

    Long id;
    private boolean own_profile = false;

    public UserHistory() {
        own_profile = true;
        initComponents();
    }

    public UserHistory(PageParameters parameters) {
        try {
            id = parameters.getLong("id");
        } catch (Throwable t) {
            t.printStackTrace();
        }
        initComponents();
    }

    private void initComponents() {
        if (id == null) {
            id = getSession().getUser().getId();
        }
        if (id == null) {
            setResponsePage(Index.class);
            return;
        }

        Felhasznalo user = userManager.findUserById(id);
        setHeaderLabelText(user.getNev() + " közösségi története");
        if (own_profile) {
            add(new BookmarkablePageLink("simpleView", ShowUser.class));
        } else {
            add(new BookmarkablePageLink("simpleView", ShowUser.class, new PageParameters("id=" + user.getId().toString())));
        }
        add(new ExternalLink("profilelink",
                "/profile/show/virid/" + id.toString()));
        setModel(new CompoundPropertyModel(user));

        List<PontIgeny> pontIgenyek = userManager.getPontIgenyekForUser(user);

        // Szemeszterenkénti pontigények táblázat
        ArrayList<SzemeszterPont> szemeszterPontok = new ArrayList<SzemeszterPont>();

        // szummázás félévente...
        Szemeszter szemeszter = null;

        for (PontIgeny pontIgeny : pontIgenyek) {
            szemeszter = pontIgeny.getErtekeles().getSzemeszter(); // ezt a szemesztert fogom most számolni

            // megnézem megszámoltam-e már ezt a szemesztert
            boolean next = false;
            for (SzemeszterPont szemeszterPont : szemeszterPontok) {
                if (szemeszterPont.getSzemeszter().toString().equals(szemeszter.toString())) {
                    next = true;
                    break;
                }
            }
            if (next) {
                continue;
            }

            // az aktuális szemeszterre kapott pontokat összegzem
            int pont = 0;
            for (PontIgeny szemeszterPont : pontIgenyek) {
                if (szemeszterPont.getErtekeles().getSzemeszter().toString().equals(szemeszter.toString())) {
                    pont = pont + szemeszterPont.getPont();
                }
            }

            // az eredményt berakom egy listába
            szemeszterPontok.add(new SzemeszterPont(szemeszter, pont));
        }

        // megjelenítés...
        ListView splv = new ListView("szemeszterPont", szemeszterPontok) {

            @Override
            protected void populateItem(ListItem item) {
                SzemeszterPont p = (SzemeszterPont) item.getModelObject();
                item.add(new Label("szemeszterPont.szemeszter", p.getSzemeszter().toString()));
                item.add(new Label("szemeszterPont.pont", String.valueOf(p.getPont())));
            }
        };
        add(splv);

        // Pontigények táblázat
        ListView plv = new ListView("pontigeny", pontIgenyek) {

            @Override
            protected void populateItem(ListItem item) {
                item.setModel(new CompoundPropertyModel(item.getModelObject()));
                item.add(new Label("ertekeles.szemeszter"));
                item.add(new Label("ertekeles.csoport.nev"));
                item.add(new Label("pont"));
            }
        };
        add(plv);

        // Belépő igények táblázat
        List<BelepoIgeny> belepoIgenyek = userManager.getBelepoIgenyekForUser(user);
        ListView blv = new ListView("belepoigeny", belepoIgenyek) {

            @Override
            protected void populateItem(ListItem item) {
                item.setModel(new CompoundPropertyModel(item.getModelObject()));
                item.add(new Label("ertekeles.szemeszter"));
                item.add(new Label("ertekeles.csoport.nev"));
                item.add(new Label("belepotipus"));
                item.add(new Label("szovegesErtekeles"));
            }
        };
        add(blv);
    }
}

class SzemeszterPont {

    private Szemeszter szemeszter;
    private int pont;

    public SzemeszterPont(Szemeszter szemeszter, int pont) {
        this.szemeszter = szemeszter;
        this.pont = pont;
    }

    public int getPont() {
        return pont;
    }

    public Szemeszter getSzemeszter() {
        return szemeszter;
    }
}
