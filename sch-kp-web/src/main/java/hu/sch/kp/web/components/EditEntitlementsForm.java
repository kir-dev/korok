/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.pages.group.ShowGroup;
import java.io.Serializable;
import java.util.ArrayList;
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
    private List<ExtendedGroup> lines = new ArrayList<ExtendedGroup>();
    private boolean activePanel;

    public EditEntitlementsForm(String name, List<Csoporttagsag> active, boolean isActivePanel) {
        super(name);
        activePanel = isActivePanel;
        for (Csoporttagsag tagsag : active) {
            lines.add(new ExtendedGroup(tagsag));
        }

        WebMarkupContainer table = new WebMarkupContainer("table");
        ListView members = new ListView("members", lines) {

            @Override
            protected void populateItem(ListItem item) {
                ExtendedGroup ext = (ExtendedGroup) item.getModelObject();
                Csoporttagsag cst = ext.getTagsag();
                item.setModel(new CompoundPropertyModel(ext));
                item.add(new FelhasznaloLink("felhlink", cst.getFelhasznalo()));
                item.add(new Label("becenev", cst.getFelhasznalo().getBecenev()));
                item.add(new Label("jogok",
                        getConverter(TagsagTipus.class).convertToString(cst.getJogokString(), getLocale())));
                item.add(new ChangePostLink("postLink", ext.getTagsag()));
                item.add(new CheckBox("check", new PropertyModel(ext, "selected")));
            }
        };
        members.setReuseItems(true);
        table.add(members);
        add(table);
    }

    @Override
    public void onSubmit() {
        try {
            for (ExtendedGroup extendedGroup : lines) {
                Csoporttagsag cst = extendedGroup.getTagsag();
                if (extendedGroup.getSelected()) {
                    if (activePanel) {
                        if (cst.getJogok() == 0 || cst.getJogok() == 2) {
                            userManager.setMemberToOldBoy(cst);
                        } else {
                            getSession().error("Jogokkal rendelkező tagot nem lehet törölni");
                        }
                    } else {
                        userManager.setOldBoyToActive(cst);
                    }
                }
            }
            getSession().info("A változások sikeresen mentésre kerültek");
        } catch (Exception ex) {
            getSession().error("Hiba történt a feldolgozás közben");
        }
        //TODO: szebbé tenni
        setResponsePage(ShowGroup.class, new PageParameters("id=" + lines.get(0).getTagsag().getCsoport().getId()));
        return;
    }

    private class ExtendedGroup implements Serializable {

        private Csoporttagsag tagsag;
        private boolean selected;

        public ExtendedGroup(Csoporttagsag cstagsag) {
            tagsag = cstagsag;
        }

        public Csoporttagsag getTagsag() {
            return tagsag;
        }

        public void setTagsag(Csoporttagsag tagsag) {
            this.tagsag = tagsag;
        }

        public boolean getSelected() {
            return selected;
        }

        public void setSelected(boolean isSelected) {
            this.selected = isSelected;
        }
    }
}
