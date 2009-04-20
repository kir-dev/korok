/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.pontigenyles;

import hu.sch.kp.web.util.ListDataProviderCompoundPropertyModelImpl;
import hu.sch.domain.Ertekeles;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.PontIgeny;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.web.pages.ertekeles.Ertekelesek;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.NumberValidator.RangeValidator;

/**
 *
 * @author hege
 */
public class PontIgenylesLeadas extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;

    public PontIgenylesLeadas(final Ertekeles ert) {
        setHeaderLabelText("Pontigénylés leadása");
        //TODO jogosultság?!
        final Long ertekelesId = ert.getId();
        final List<PontIgeny> igenylista = igenyeketElokeszit(ert);

        setModel(new CompoundPropertyModel(ert));
        add(new Label("csoport.nev"));
        add(new Label("szemeszter"));
        add(new FeedbackPanel("pagemessages"));

        // Űrlap létrehozása
        Form igform = new Form("igenyekform") {

            @Override
            protected void onSubmit() {
                // pontok tárolása
                ertekelesManager.pontIgenyekLeadasa(ertekelesId, igenylista);
                getSession().info(getLocalizer().getString("info.PontIgenylesMentve", this));
                setResponsePage(Ertekelesek.class);
            }
        };

        // Bevitelhez táblázat létrehozása
        IDataProvider provider =
                new ListDataProviderCompoundPropertyModelImpl(igenylista);
        DataView dview = new DataView("igenyek", provider) {

            // QPA csoport pontozásvalidátora
            final IValidator QpaPontValidator = RangeValidator.range(0, 100);
            // A többi csoport pontozásvalidátora
            final IValidator pontValidator = RangeValidator.range(0, 50);
            // QPA csoport ID-ja
            private final long SCH_QPA_ID = 27L;

            @Override
            protected void populateItem(Item item) {
                final ValidationError validationError = new ValidationError();
                validationError.addMessageKey("err.MinimumPontHiba");

                item.add(new Label("felhasznalo.nev"));
                item.add(new Label("felhasznalo.becenev"));
                TextField pont = new TextField("pont");
                //csoportfüggő validátor hozzácsatolása
                if (ert.getCsoport().getId().equals(SCH_QPA_ID)) {
                    pont.add(QpaPontValidator);
                } else {
                    pont.add(pontValidator);
                }

                //olyan validátor, ami akkor dob hibát ha 0 és 5 pont között adott meg
                pont.add(new IValidator() {

                    public void validate(IValidatable arg0) {
                        final Integer pont = (Integer) arg0.getValue();
                        if (pont.compareTo(5) < 0 && pont.compareTo(0) > 0) {
                            arg0.error(validationError);
                        }
                    }
                });
                item.add(pont);
            }
        };

        igform.add(dview);
        add(igform);
    }

    private List<PontIgeny> igenyeketElokeszit(Ertekeles ert) {
        List<Felhasznalo> csoporttagok =
                userManager.getCsoporttagokWithoutOregtagok(ert.getCsoport().getId());
        List<PontIgeny> igenyek =
                ertekelesManager.findPontIgenyekForErtekeles(ert.getId());

        //tagok és igények összefésülése
        if (igenyek.size() == 0) {
            for (Felhasznalo f : csoporttagok) {
                igenyek.add(new PontIgeny(f, 0));
            }
        } else {
            // TODO tényleges összefésülés
            if (igenyek.size() != csoporttagok.size()) {
                // TODO összefésülés
                throw new UnsupportedOperationException("PontIgény - Csoporttag összefésülés még nincs implementálva");
            }
        }

        return igenyek;
    }
}
