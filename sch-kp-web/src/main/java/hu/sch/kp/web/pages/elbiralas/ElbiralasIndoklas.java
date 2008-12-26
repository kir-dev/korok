/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.elbiralas;

import hu.sch.domain.ElbiraltErtekeles;
import hu.sch.domain.ErtekelesStatusz;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author hege
 */
public class ElbiralasIndoklas extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;

    public ElbiralasIndoklas(final List<ElbiraltErtekeles> elbiralasAlatt) {
        Form elbiralasForm = new Form("elbiralasindoklasform") {

            @Override
            protected void onSubmit() {
                ertekelesManager.ErtekeleseketElbiral(elbiralasAlatt, getFelhasznalo());
            }
        };
        add(elbiralasForm);
        elbiralasForm.add(new ListView("elbiraltErtekeles", elbiralasAlatt) {

            @Override
            protected void populateItem(ListItem item) {
                final ElbiraltErtekeles e = (ElbiraltErtekeles) item.getModelObject();
                item.setModel(new CompoundPropertyModel(e));
                item.add(new Label("ertekeles.csoport.nev"));
                item.add(new Label("pontStatusz"));
                item.add(new Label("belepoStatusz"));
                FormComponent ta = new TextArea("indoklas");
                if (!e.getBelepoStatusz().equals(ErtekelesStatusz.ELFOGADVA) ||
                        !e.getPontStatusz().equals(ErtekelesStatusz.ELFOGADVA)) {

                    ta.setRequired(true);
                    ta.add(new AttributeModifier("class", new Model("kotelezo")));
                }

                item.add(ta);
            }
        });
    }
}
