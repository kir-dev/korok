/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * backup 2008-12-25
 */
package hu.sch.kp.web.pages.elbiralas;

import hu.sch.domain.ElbiraltErtekeles;
import hu.sch.domain.Ertekeles;
import hu.sch.domain.ErtekelesStatisztika;
import hu.sch.domain.ErtekelesStatusz;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.web.components.ErtekelesStatuszValaszto;
import hu.sch.kp.web.pages.ertekeles.ErtekelesReszletek;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class OsszesErtekeles extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;
    Map<Long, ElbiraltErtekeles> elbiralasAlatt = new HashMap<Long, ElbiraltErtekeles>();

    public Map<Long, ElbiraltErtekeles> getElbiralasAlatt() {
        return elbiralasAlatt;
    }

    public void setElbiralasAlatt(Map<Long, ElbiraltErtekeles> elbiralasAlatt) {
        this.elbiralasAlatt = elbiralasAlatt;
    }

    public OsszesErtekeles() {
        //TODO: csak JETI-nek menjen az oldal
        add(new FeedbackPanel("pagemessages"));
        add(new Label("szemeszter", new PropertyModel(this, "szemeszter")));
        SortableDataProvider dp = new SortableErtekelesStatisztikaDataProvider(ertekelesManager, getSzemeszter());

        Form form = new Form("elbiralasform") {

            @Override
            protected void onSubmit() {
                List<ElbiraltErtekeles> list = new LinkedList<ElbiraltErtekeles>();

                for (ElbiraltErtekeles elbiraltertekeles : getElbiralasAlatt().values()) {
                    //if ((elbiraltertekeles.getPontStatusz().equals(ErtekelesStatusz.ELBIRALATLAN) && (elbiraltertekele)

                    // Ha valtozott valamelyik belepokerelemhez vagy pontkerelemhez tartozo legordulo,
                    if ((elbiraltertekeles.getPontStatusz() != elbiraltertekeles.getErtekeles().getPontStatusz()) ||
                            (elbiraltertekeles.getBelepoStatusz() != elbiraltertekeles.getErtekeles().getBelepoStatusz())) {
                        list.add(elbiraltertekeles);
                    }
                /*if (e.isInkonzisztens()) {
                error("A " + e.getErtekeles().getCsoport().getNev() +
                " csoport értékelésének elbírálása hibás. A pont- és belépőkérelmeket is el kell bírálni!");
                System.out.println("Inkonzisztens: " + e);
                } else if (e.isElbiralt()) {
                System.out.println("Elbiralt: " + e);
                list.add(e);
                } else {
                System.out.println("Nem elbiralt: " + e);
                }*/
                }
                /*
                Iterator it = dp.iterator(1, dp.size());
                while (it.hasNext()) {
                ErtekelesStatisztika st = (ErtekelesStatisztika) it.next();
                ElbiraltErtekeles e = new ElbiraltErtekeles(st.getErtekeles());
                
                if (e.isInkonzisztens()) {
                error("A " + e.getErtekeles().getCsoport().getNev() +
                " csoport értékelésének elbírálása hibás. A pont- és belépőkérelmeket is el kell bírálni!");
                System.out.println("Inkonzisztens: " + e);
                } else if (e.isElbiralt()) {
                System.out.println("Elbiralt: " + e);
                list.add(e);
                } else {
                System.out.println("Nem elbiralt: " + e);
                }
                }*/

                /**/

                if (!hasError() && list.isEmpty()) {
                    error("Nem bíráltál el egy értékelést sem!");
                }
                if (!hasError()) {
                    setResponsePage(new ElbiralasIndoklas(list));
                }
            }
        };
        add(form);

        form.add(new DataView("ertekeleslista", dp) {

            @Override
            protected void populateItem(Item item) {
                final Ertekeles ert = ((ErtekelesStatisztika) item.getModelObject()).getErtekeles();

                ElbiraltErtekeles ee = null;
                if (!getElbiralasAlatt().containsKey(ert.getId())) {
                    ee = new ElbiraltErtekeles(ert, ert.getPontStatusz(), ert.getBelepoStatusz());
                    getElbiralasAlatt().put(ert.getId(), ee);
                } else {
                    ee = getElbiralasAlatt().get(ert.getId());
                }

                Link ertekeleslink = new Link("ertekeleslink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new ErtekelesReszletek(ert, getPage()));
                    }
                };
                item.add(ertekeleslink);
                ertekeleslink.add(new Label("ertekeles.csoport.nev"));
                item.add(new Label("atlagPont"));
                item.add(new Label("kiosztottKDO"));
                item.add(new Label("kiosztottKB"));
                item.add(new Label("kiosztottAB"));

                Component pontStatusz = new ErtekelesStatuszValaszto("pontStatusz");
                Component belepoStatusz = new ErtekelesStatuszValaszto("belepoStatusz");
                pontStatusz.setVisible(!ert.getPontStatusz().equals(ErtekelesStatusz.NINCS));
                belepoStatusz.setVisible(!ert.getBelepoStatusz().equals(ErtekelesStatusz.NINCS));
                //System.out.println("abba: " + ert.getPontStatusz() + " " + ert.getBelepoStatusz());
                //System.out.println("aaa: " + ert.getPontStatusz());
                //System.out.println("bbb: " + ert.getBelepoStatusz());
                //IModel newModel = new CompoundPropertyModel(ee);
                pontStatusz.setModel(new PropertyModel(ee, "pontStatusz"));
                belepoStatusz.setModel(new PropertyModel(ee, "belepoStatusz"));
                item.add(pontStatusz);
                item.add(belepoStatusz);
            }
            /*
            class ErtekelesStatuszValasztoImpl extends ErtekelesStatuszValaszto {
            
            public ErtekelesStatuszValasztoImpl(String id) {
            super(id);
            }
            
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
            return true;
            }
            
            @Override
            protected void onSelectionChanged(Object newSelection) {
            updateModel();
            Object o = getInnermostModel().getObject();
            ElbiraltErtekeles e = (ElbiraltErtekeles) o;
            
            ElbiraltErtekeles ee = getElbiralasAlatt().get(e.getErtekeles().getId());
            getElbiralasAlatt().put(e.getErtekeles().getId(), ee);
            }
            }*/
        });

        form.add(new OrderByBorderImpl("orderByCsoport", "csoportNev", dp));
        form.add(new OrderByBorderImpl("orderByAtlagPont", "atlagPont", dp));
        form.add(new OrderByBorderImpl("orderByKiosztottKDO", "kiosztottKDO", dp));
        form.add(new OrderByBorderImpl("orderByKiosztottKB", "kiosztottKB", dp));
        form.add(new OrderByBorderImpl("orderByKiosztottAB", "kiosztottAB", dp));
        form.add(new OrderByBorderImpl("orderByPontStatusz", "pontStatusz", dp));
        form.add(new OrderByBorderImpl("orderByBelepoStatusz", "belepoStatusz", dp));
    }

    private class OrderByBorderImpl extends OrderByBorder {

        public OrderByBorderImpl(String id, String property, ISortStateLocator stateLocator) {
            super(id, property, stateLocator);
        }

        @Override
        protected void onSortChanged() {
            getElbiralasAlatt().clear();
        }
    }
}
