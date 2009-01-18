/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.admin;

import hu.sch.domain.ErtekelesIdoszak;
import hu.sch.domain.Szemeszter;
import hu.sch.kp.services.SystemManagerLocal;
import hu.sch.kp.services.exceptions.NoSuchAttributeException;
import hu.sch.kp.web.pages.group.GroupHierarchy;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.Arrays;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.NumberValidator;

/**
 *
 * @author major
 */
public class EditSettings extends SecuredPageTemplate {

    @EJB(name = "SystemManagerBean")
    SystemManagerLocal systemManager;
    private Szemeszter szemeszter;
    ErtekelesIdoszak ertekelesIdoszak;

    public EditSettings() {
        super();
        if (!isCurrentUserAdmin() && !isCurrentUserJETI()) {
            info("Nincs jogod a megadott művelethez");
            setResponsePage(GroupHierarchy.class);
            return;
        }
        setHeaderLabelText("Beállítások");
        try {
            szemeszter = systemManager.getSzemeszter();
        } catch (NoSuchAttributeException e) {
            szemeszter = new Szemeszter();
        }


        setErtekelesIdoszak(systemManager.getErtekelesIdoszak());

        Form beallitasForm = new Form("beallitasform") {

            @Override
            public void onSubmit() {
                systemManager.setSzemeszter(getSzemeszter());
                systemManager.setErtekelesIdoszak(getErtekelesIdoszak());
                setResponsePage(Index.class);
            }
        };
        beallitasForm.setModel(new CompoundPropertyModel(szemeszter));
        final TextField elsoEv = new TextField("elsoEv");
        beallitasForm.add(elsoEv.add(NumberValidator.range(2000, 2030)));
        final TextField masodikEv = new TextField("masodikEv");
        beallitasForm.add(masodikEv.add(NumberValidator.range(2000, 2030)));
        beallitasForm.add(new CheckBox("osziFelev"));
        beallitasForm.add(new AbstractFormValidator() {

            public FormComponent[] getDependentFormComponents() {
                return new FormComponent[]{elsoEv, masodikEv};
            }

            public void validate(Form form) {
                if (Integer.parseInt(elsoEv.getValue()) + 1 !=
                        Integer.parseInt(masodikEv.getValue())) {
                    error(elsoEv, "err.SzemeszterEvKulonbseg");
                }
            }
        });
        DropDownChoice ddc1 = new DropDownChoice("ertekelesidoszak",
                Arrays.asList(ErtekelesIdoszak.values()));
        ddc1.setRequired(true);
        ddc1.setModel(new PropertyModel(this, "ertekelesIdoszak"));

        ddc1.setChoiceRenderer(new IChoiceRenderer() {

            public Object getDisplayValue(Object object) {
                return getLocalizer().getString("ertekelesidoszak." + object.toString(), getParent());
            }

            public String getIdValue(Object object, int index) {
                return object.toString();
            }
        });
        beallitasForm.add(ddc1);
        add(beallitasForm);
    }

    public ErtekelesIdoszak getErtekelesIdoszak() {
        return ertekelesIdoszak;
    }

    public void setErtekelesIdoszak(ErtekelesIdoszak idoszak) {
        this.ertekelesIdoszak = idoszak;
    }

    public Szemeszter getSzemeszter() {
        return szemeszter;
    }
}
