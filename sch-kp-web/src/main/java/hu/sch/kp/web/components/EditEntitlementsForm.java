/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.pages.group.ShowGroup;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public class EditEntitlementsForm extends Form {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    private List<Csoporttagsag> datas;

    public EditEntitlementsForm(String name, List<Csoporttagsag> active) {
        super(name);
        datas = active;

        WebMarkupContainer table = new WebMarkupContainer("table");
        ListView members = new ListView("members", datas) {

            @Override
            protected void populateItem(ListItem item) {
                Csoporttagsag cs = (Csoporttagsag) item.getModelObject();
                item.setModel(new CompoundPropertyModel(cs));
                item.add(new FelhasznaloLink("felhlink", cs.getFelhasznalo()));
                item.add(new Label("becenev", cs.getFelhasznalo().getBecenev()));
                item.add(new Label("jogok",
                        getConverter(TagsagTipus.class).convertToString(cs.getJogokString(), getLocale())));
                item.add(new ChangePostLink("postLink", cs));
                item.add(new CheckBox("check", new PropertyModel(cs.getFelhasznalo(), "selected")));
            }
        };
        members.setReuseItems(true);
        table.add(members);
        add(table);
    }

    @Override
    public void onSubmit() {
        try {
            for (Csoporttagsag csoporttagsag : datas) {
                if (csoporttagsag.getFelhasznalo().getSelected()) {
                    if (csoporttagsag.getJogok() == 0) {
                        userManager.setMemberToOldBoy(csoporttagsag);
                    } else {
                        getSession().error("Jogokkal rendelkező tagot nem lehet törölni");
                    }
                }
            }
            getSession().info("A változások sikeresen mentésre kerültek");
        } catch (Exception ex) {
            getSession().error("Hiba történt a feldolgozás közben");
        }
        setResponsePage(ShowGroup.class, new PageParameters("id=" + datas.get(0).getCsoport().getId()));
        return;
    }
}
