/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.ertekeles;

import hu.sch.domain.Csoport;
import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.Ertekeles;
import hu.sch.domain.ErtekelesStatusz;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.web.pages.belepoigenyles.BelepoIgenylesLeadas;

import hu.sch.kp.web.pages.group.GroupHierarchy;
import hu.sch.kp.web.pages.index.Index;

import hu.sch.kp.web.pages.pontigenyles.PontIgenylesLeadas;
import hu.sch.kp.web.session.VirSession;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class Ertekelesek extends SecuredPageTemplate {

    /*    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;
    
    public Ertekelesek() {
    super();
    //TODO: csak korvezetonek menjen az oldal.
    WebMarkupContainer table = new WebMarkupContainer("ertekelesektabla");
    
    Csoport csoport = getCsoport();
    List<Ertekeles> list = ertekelesManager.findErtekeles(csoport);
    ListView ertekelesek = new ListView("ertekeles", list) {
    
    @Override
    protected void populateItem(ListItem item) {
    final Ertekeles e = (Ertekeles) item.getModelObject();
    Link ert = new Link("ertekeleslink") {
    
    @Override
    public void onClick() {
    setResponsePage(new ErtekelesReszletek(e));
    }
    };
    ert.add(new Label("ertekelesszemeszter", new PropertyModel(e, "szemeszter")));
    item.add(ert);
    IModel model = new CompoundPropertyModel(e);
    item.setModel(model);
    
    Link uzenetekLink = new Link("uzeneteklink") {
    
    @Override
    public void onClick() {
    setResponsePage(new ErtekelesUzenetek(e.getId()));
    }
    };
    item.add(uzenetekLink);
    
    Link pontkerelemLink = new Link("pontkerelemlink", model) {
    
    @Override
    public void onClick() {
    setResponsePage(new PontIgenylesLeadas(e));
    }
    };
    pontkerelemLink.add(new Label("pontStatusz"));
    item.add(pontkerelemLink);
    
    Link belepokerelemLink = new Link("belepokerelemlink", model) {
    
    @Override
    public void onClick() {
    setResponsePage(new BelepoIgenylesLeadas((e)));
    }
    };
    item.add(belepokerelemLink);
    belepokerelemLink.add(new Label("belepoStatusz"));
    
    item.add(DateLabel.forDatePattern("utolsoModositas", "yyyy.MM.dd. kk:mm"));
    item.add(DateLabel.forDatePattern("utolsoElbiralas", "yyyy.MM.dd. kk:mm"));
    }
    };
    table.add(ertekelesek);
    add(table);
    
    if (list.size() == 0) {
    info(getLocalizer().getString("info.NincsErtekeles", this));
    table.setVisible(false);
    }
    
    Link ujertekeles = new Link("ujertekeles") {
    
    @Override
    public void onClick() {
    setResponsePage(UjErtekeles.class);
    }
    };
    if (!ertekelesManager.isErtekelesLeadhato(csoport)) {
    ujertekeles.setVisible(false);
    }
    System.out.println(ertekelesManager.isErtekelesLeadhato(csoport));
    add(ujertekeles);
    }*/
    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;
    public String selected = "";
    List<Ertekeles> ertekelesList = new ArrayList<Ertekeles>();
    Long id;
    Csoport csoport;
    Link ujertekeles;

    public Ertekelesek() {
        setHeaderLabelText("Csoportválasztás");
        if (id == null) {
            id = getSession().getUser().getId();
        }
        if (id == null) {
            setResponsePage(Index.class);
            return;
        }
        add(new FeedbackPanel("pagemessages"));

        Felhasznalo user = userManager.findUserWithCsoporttagsagokById(id);
        if (user == null) {
            //Ez egy soha sorra nem kerulo feltetel, mivel csak korvezetonek jelenhet meg
            //az opcio, igy legalabb tuti nem szall el
            info("Nem vagy körtag, mégis értékelést szeretnél leadni? Nono...");
            setResponsePage(GroupHierarchy.class);
            return;
        }
        if (!hasUserRoleInSomeGroup(TagsagTipus.KORVEZETO)) {
            info("Nem vagy sehol sem körvezető, mit csinálsz itt?");
            setResponsePage(GroupHierarchy.class);
            return;
        }
        user.sortCsoporttagsagok();

        final List<Csoporttagsag> cstag = user.getCsoporttagsagok();
        final ArrayList<String> csoportok = new ArrayList<String>();
        //TODO simplify this
        for (Csoporttagsag t : cstag) {
            if (hasUserRoleInGroup(t.getCsoport(), TagsagTipus.KORVEZETO)) {
                csoportok.add(t.getCsoport().getNev());
            }
        }
        // Ha mar korabban volt csoport kivalasztva.
        updateErtekelesList();
        Form csoportForm = new Form("csoportform") {

            @Override
            public void onSubmit() {
                Iterator iterator = cstag.iterator();
                Csoport cs = null;
                while (iterator.hasNext()) {
                    //TODO simplify this
                    cs = ((Csoporttagsag) iterator.next()).getCsoport();
                    if (cs.getNev().equals(selected)) {
                        ((VirSession) getSession()).setCsoport(cs);
                        updateErtekelesList();
                        if ((ertekelesList.size() == 0) ||
                                (!ertekelesManager.isErtekelesLeadhato(csoport))) {
                            ujertekeles.setVisible(false);
                        } else {
                            ujertekeles.setVisible(true);
                        }

                        break;
                    }
                }
//                if (!continueToOriginalDestination()) {
//                    setResponsePage(ShowGroup.class, new PageParameters("id=" + cs.getId().toString()));
//                } else {
//                return;
//                }
            }
        };
        DropDownChoice ddc = new DropDownChoice("groups", csoportok);

        ddc.setModel(new PropertyModel(this, "selected"));

        csoportForm.add(ddc);
        add(csoportForm);

        WebMarkupContainer table = new WebMarkupContainer("ertekelesektabla");
        ListView ertekelesListView = new ListView("ertekeles", ertekelesList) {

            @Override
            protected void populateItem(ListItem item) {
                final Ertekeles e = (Ertekeles) item.getModelObject();
                Link ert = new Link("ertekeleslink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new ErtekelesReszletek(e));
                    }
                };
                ert.add(new Label("ertekelesszemeszter", new PropertyModel(e, "szemeszter")));
                item.add(ert);
                IModel model = new CompoundPropertyModel(e);
                item.setModel(model);

                Link uzenetekLink = new Link("uzeneteklink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new ErtekelesUzenetek(e.getId()));
                    }
                };
                item.add(uzenetekLink);

                Link pontkerelemLink = new Link("pontkerelemlink", model) {

                    @Override
                    public void onClick() {
                        if (e.getPontStatusz() == ErtekelesStatusz.ELFOGADVA) {
                            setResponsePage(new LeadottPontIgenyles(e));
                            return;
                        } else {
                            setResponsePage(new PontIgenylesLeadas(e));
                        }
                    }
                };
                pontkerelemLink.add(new Label("pontStatusz"));
                item.add(pontkerelemLink);

                Link belepokerelemLink = new Link("belepokerelemlink", model) {

                    @Override
                    public void onClick() {
                        if (e.getBelepoStatusz() == ErtekelesStatusz.ELFOGADVA) {
                            setResponsePage(new LeadottBelepoIgenyles(e));
                            return;
                        } else {
                            setResponsePage(new BelepoIgenylesLeadas((e)));
                            return;
                        }
                    }
                };
                item.add(belepokerelemLink);
                belepokerelemLink.add(new Label("belepoStatusz"));

                item.add(DateLabel.forDatePattern("utolsoModositas", "yyyy.MM.dd. kk:mm"));
                item.add(DateLabel.forDatePattern("utolsoElbiralas", "yyyy.MM.dd. kk:mm"));
            }
        };
        table.add(ertekelesListView);
        add(table);

        ujertekeles = new Link("ujertekeles") {

            @Override
            public void onClick() {
                setResponsePage(UjErtekeles.class);
            }
        };
        add(ujertekeles);

//        updateErtekelesList();
//        if (ertekelesList.size() == 0) {
//            info(getLocalizer().getString("info.NincsErtekeles", this));
//            ujertekeles.setVisible(false);
//        }
        if ((ertekelesList.size() == 0) ||
                (!ertekelesManager.isErtekelesLeadhato(csoport))) {
            ujertekeles.setVisible(false);
        } else {
            if (hasUserRoleInSomeGroup(TagsagTipus.KORVEZETO)) {
                ujertekeles.setVisible(true);
            }
        }
    }

    public void updateErtekelesList() {
        csoport = getSession().getCsoport();
        if (csoport != null) {
            ertekelesList.clear();
            ertekelesList.addAll(ertekelesManager.findErtekeles(csoport));
            selected = csoport.getNev();
            setHeaderLabelText(csoport.getNev());
        }
    }
}
