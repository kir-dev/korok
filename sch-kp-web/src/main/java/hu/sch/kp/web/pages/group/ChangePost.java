/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.group;

import hu.sch.domain.Csoport;
import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.web.components.Input;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author aldaris
 */
public final class ChangePost extends SecuredPageTemplate {

    public ChangePost(PageParameters params) {
        final Long groupid = new Long(params.getLong("groupid"));
        final Long userid = new Long(params.getLong("userid"));

        setHeaderLabelText("Jog megadása");
        Csoport group = userManager.findGroupWithCsoporttagsagokById(groupid);
        Felhasznalo user = userManager.findUserById(userid);
        final Csoporttagsag cst = userManager.getCsoporttagsag(userid, groupid);
        final List<Csoporttagsag> activeMembers = group.getActiveMembers();

        if (!hasUserRoleInGroup(group, TagsagTipus.KORVEZETO)) {
            getSession().info("Nincs jogod a megadott művelethez");
            setResponsePage(GroupHierarchy.class);
            return;
        }
        if (user == null || group == null) {
            getSession().info("Hibás adatok");
            setResponsePage(ShowGroup.class, new PageParameters("id=" + groupid));
            return;
        }
        add(new Label("groupname", group.getNev()));
        add(new Label("username", user.getNev()));
        final Input input = new Input(cst);
        setModel(new CompoundPropertyModel(input));
        Form form = new Form("changePost") {

            @Override
            protected void onSubmit() {
                List<TagsagTipus> memberRights = input.getChoices();
                List<TagsagTipus> originalRights = Arrays.asList(cst.getJogokString());
                boolean found = false;
                try {
                    for (TagsagTipus tagsagTipus : memberRights) {
                        if (!originalRights.contains(tagsagTipus)) {
                            for (Csoporttagsag csoporttagsag : activeMembers) {
                                if (TagsagTipus.hasJogCsoportban(csoporttagsag, tagsagTipus)) {
                                    found = true;
                                    userManager.updateMemberRights(csoporttagsag, cst, tagsagTipus);
                                }
                            }
                            if (!found) {
                                userManager.updateMemberRights(null, cst, tagsagTipus);
                            }
                        }
                    }
                    for (TagsagTipus tagsagTipus : originalRights) {
                        if (!memberRights.contains(tagsagTipus)) {
                            userManager.updateMemberRights(cst, null, tagsagTipus);
                        }
                    }
                } catch (Exception e) {
                    getSession().info("A mentés során hiba lépett fel");
                    setResponsePage(ShowGroup.class, new PageParameters("id=" + groupid.toString()));
                    return;
                }
                getSession().info("A változások mentésre kerültek");
                setResponsePage(ShowGroup.class, new PageParameters("id=" + groupid.toString()));
                return;
            }
        };

        //form.add(checkboxes);
        List<TagsagTipus> tomb = new ArrayList<TagsagTipus>();
        tomb.add(TagsagTipus.KORVEZETO);
        tomb.add(TagsagTipus.GAZDASAGIS);
        tomb.add(TagsagTipus.PRMENEDZSER);


        CheckBoxMultipleChoice choices = new CheckBoxMultipleChoice("choices", tomb) {

            @Override
            protected boolean isDisabled(Object object, int index, String selected) {
                if (index == 1) {
                    return true;
                }
                return super.isDisabled(object, index, selected);
            }
        };

        //checkboxes.add(choices);
        form.add(choices);
        add(form);
    }
}