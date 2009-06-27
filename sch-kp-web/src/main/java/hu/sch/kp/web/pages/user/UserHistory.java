/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.user;

import hu.sch.domain.BelepoIgeny;
import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.PontIgeny;
import hu.sch.domain.Szemeszter;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author Adam Lantos
 */
public class UserHistory extends SecuredPageTemplate {

    Long id;
    final String OSSZES_KOR = "Összes kör";
    public String selected = OSSZES_KOR;
    private boolean own_profile = false;

    

    public UserHistory() {
        own_profile = true;
        initComponents();
    }

    public UserHistory(PageParameters parameters) {
        try {
            id = parameters.getLong("id");
            selected = parameters.getString("csoport", "");
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
        add(new ExternalLink("profilelink", "/profile/show/virid/" + id.toString()));
        setModel(new CompoundPropertyModel(user));

        final List<String> groups = new ArrayList<String>();
        groups.add(OSSZES_KOR);

        List<Csoporttagsag> cst = user.getCsoporttagsagok();
        for (Csoporttagsag csoporttagsag : cst)
        {
            groups.add(csoporttagsag.getCsoport().getNev());
        }

        List<PontIgeny> pontIgenyek = userManager.getPontIgenyekForUser(user);

        DropDownChoice ddc = new DropDownChoice("group", new PropertyModel(this, "selected"), groups)
        {
            @Override
            protected boolean wantOnSelectionChangedNotifications()
            {
                return true;
            }

            @Override
            protected void onSelectionChanged(final Object newSelection)
            {
                String selected = groups.get(Integer.valueOf(this.getInput()));

                PageParameters pp = new PageParameters();
                pp.add("csoport", selected);
                pp.add("id", String.valueOf(id));
                setResponsePage(UserHistory.class, pp);
            }
        };

        add(ddc);

        // Szemeszterenkénti pontigények táblázat
        ArrayList<SzemeszterKorPont> Skp = new ArrayList<SzemeszterKorPont>();

        // minden kör minden pontjához hozzáadom az előző évben adott pontot (ha volt előző féléves pont is)
        for (PontIgeny pontIgeny : pontIgenyek)
            Skp.add(new SzemeszterKorPont(pontIgeny.getErtekeles().getSzemeszter(), pontIgeny.getPont(), pontIgeny.getErtekeles().getCsoport().getId()));

        for (SzemeszterKorPont skp1 : Skp)
        {
            for (SzemeszterKorPont skp2 : Skp)
            {
                if (skp1.getKorId().equals(skp2.getKorId()) &&
                    skp1.getSzemeszter().getElozo().getId().equals(skp2.getSzemeszter().getId()))
                {
                    skp1.Add(skp2.getPont());
                }
            }
        }

        // a csak előző félévben pontozott köröket is hozzá kell majd számolni a jelenlegi féléves pontokhoz
        for (SzemeszterKorPont skp : (ArrayList<SzemeszterKorPont>) Skp.clone())
        {
            boolean nincs = true;
            for (SzemeszterKorPont skp2 : Skp)
            {
                if (skp2.getKorId().equals(skp.getKorId()) &&
                    skp2.getSzemeszter().equals(skp.getSzemeszter().getKovetkezo()))
                {
                    nincs = false;
                    break;
                }
            }

            if (nincs)
            {   // ebből a körből nincs most pont csak az előző félévben,
                // viszont nekem azt is összegeznem kell majd
                if (!skp.getSzemeszter().equals(systemManager.getSzemeszter())) // jövőbe nem pontozunk :)
                    Skp.add(new SzemeszterKorPont(skp.getSzemeszter().getKovetkezo(), skp.getPont(), skp.getKorId()));
            }
        }

        ArrayList<SzemeszterPont> szemeszterPontok = new ArrayList<SzemeszterPont>();

        Szemeszter szemeszter = null;
        
        // négyzetösszegek...
        for (SzemeszterKorPont skp : Skp)
        {

            if (!skp.getSzemeszter().equals(szemeszter))
                szemeszter = skp.getSzemeszter();
            else
                continue;

            // megnézem számoltam-e már ezt a félévet
            boolean next = false;
            for (SzemeszterPont szemeszterPont : szemeszterPontok)
            {
                if (szemeszterPont.getSzemeszter().equals(szemeszter))
                {
                    next = true;
                    break;
                }
            }

            if (next)
                continue;   // már számoltam ezt a félévet

            // négyzetösszeg...
            int pont = 0;

            for (SzemeszterKorPont p : Skp)
            {
                if (p.getSzemeszter().equals(szemeszter))
                    pont = pont + p.getPont()*p.getPont();
            }

            pont = (int) java.lang.Math.sqrt(pont); // nem szabályos kerekítés! (egészrész)

            szemeszterPontok.add(new SzemeszterPont(szemeszter, pont));
        }


        // megjelenítés...
        ListView splv = new ListView("szemeszterPont", szemeszterPontok)
        {
            @Override
            protected void populateItem(ListItem item)
            {
                SzemeszterPont p = (SzemeszterPont) item.getModelObject();
                item.add(new Label("szemeszterPont.szemeszter", p.getSzemeszter().toString()));
                item.add(new Label("szemeszterPont.pont", String.valueOf(p.getPont())));
            }
        };
        add(splv);

        // Pontigények táblázat
        if (!selected.equals(OSSZES_KOR))
        {
            // szűrés adott csoportra

            ArrayList<PontIgeny> obj = new ArrayList<PontIgeny>();
            for (PontIgeny pontIgeny : pontIgenyek)
            {
                if (pontIgeny.getErtekeles().getCsoport().getNev().equals(selected))
                {
                    obj.add(pontIgeny);
                }
            }
            pontIgenyek = obj;
        }

        ListView plv = new ListView("pontigeny", pontIgenyek)
        {
            @Override
            protected void populateItem(ListItem item)
            {
                item.setModel(new CompoundPropertyModel(item.getModelObject()));

                item.add(new Label("ertekeles.szemeszter"));
                item.add(new Label("ertekeles.csoport.nev"));
                item.add(new Label("pont"));
            }
        };
        add(plv);

        // Belépő igények táblázat
        List<BelepoIgeny> belepoIgenyek = userManager.getBelepoIgenyekForUser(user);

        if (!selected.equals(OSSZES_KOR))
        {
            // szűrés adott csoportra
            
            ArrayList<BelepoIgeny> obj = new ArrayList<BelepoIgeny>();
            for (BelepoIgeny belepoIgeny : belepoIgenyek)
            {
                if (belepoIgeny.getErtekeles().getCsoport().getNev().equals(selected))
                {
                    obj.add(belepoIgeny);
                }
            }
            belepoIgenyek = obj;
        }

        ListView blv = new ListView("belepoigeny", belepoIgenyek)
        {
            @Override
            protected void populateItem(ListItem item)
            {
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

class SzemeszterKorPont
{
    private Szemeszter szemeszter;
    private Long korId;
    private Integer pont;

    public SzemeszterKorPont(Szemeszter szemeszter, Integer pont, Long korId)
    {
        this.szemeszter = szemeszter;
        this.pont = pont;
        this.korId = korId;
    }

    public Szemeszter getSzemeszter()
    {
        return szemeszter;
    }

    public Integer getPont()
    {
        return pont;
    }

    public Long getKorId()
    {
        return korId;
    }

    public void Add(Integer i)
    {
        pont = pont + i;
    }
}

class SzemeszterPont implements Serializable
{
    private Szemeszter szemeszter;
    private Integer pont;

    public SzemeszterPont(Szemeszter szemeszter, Integer pont)
    {
        this.szemeszter = szemeszter;
        this.pont = pont;
    }

    public Szemeszter getSzemeszter()
    {
        return szemeszter;
    }

    public Integer getPont()
    {
        return pont;
    }
}