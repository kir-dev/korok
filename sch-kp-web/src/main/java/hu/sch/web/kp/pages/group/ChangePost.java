/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.User;
import hu.sch.domain.MembershipType;
import hu.sch.web.components.Input;
import hu.sch.web.kp.templates.SecuredPageTemplate;
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
        Group group = userManager.findGroupWithCsoporttagsagokById(groupid);
        User user = userManager.findUserById(userid);
        final Membership cst = userManager.getCsoporttagsag(userid, groupid);
        final List<Membership> activeMembers = group.getActiveMemberships();

        if (!hasUserRoleInGroup(group, MembershipType.KORVEZETO)) {
            getSession().info("Nincs jogod a megadott művelethez");
            setResponsePage(GroupHierarchy.class);
            return;
        }
        //if (user == null || group == null) { // group == null always false
        if (user == null) {
            getSession().info("Hibás adatok");
            setResponsePage(ShowGroup.class, new PageParameters("id=" + groupid));
            return;
        }
        add(new Label("groupname", group.getName()));
        add(new Label("username", user.getName()));
        final Input input = new Input(cst);
        setDefaultModel(new CompoundPropertyModel(input));
        Form form = new Form("changePost") {

            @Override
            protected void onSubmit() {
                List<MembershipType> memberRights = input.getChoices();
                List<MembershipType> originalRights = Arrays.asList(cst.getRightsAsString());
                boolean found = false;
                try {
                    //milyen jogok vannak a checkbox-ok alapján
                    for (MembershipType tagsagTipus : memberRights) {
                        //Ha az eredeti jogai között nem szerepelt valamelyik jog:
                        if (!originalRights.contains(tagsagTipus)) {
                            //végigmegyünk az összes csoporttagon
                            for (Membership csoporttagsag : activeMembers) {
                                //megnézzük, hogy van-e neki ugyanilyen joga
                                if (MembershipType.hasJogInGroup(csoporttagsag, tagsagTipus)) {
                                    //ha már volt ilyen emberke
                                    if (found) {
                                        //akkor ennek már csak elvesszük a jogát
                                        userManager.updateMemberRights(csoporttagsag, null, tagsagTipus);
                                    } else {
                                        //egyébként a kettő user posztot cserél
                                        userManager.updateMemberRights(csoporttagsag, cst, tagsagTipus);
                                    }
                                    found = true;
                                }
                            }
                            //ha nem volt egy emberke se ilyen joggal, akkor ez a user fogja megkapni
                            if (!found) {
                                userManager.updateMemberRights(null, cst, tagsagTipus);
                            }
                        }
                    }
                    //végigmegyünk az eredeti jogain
                    for (MembershipType tagsagTipus : originalRights) {
                        //ha most elvettünk egy jogot a user-től, akkor tényleg elvesszük :)
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
        List<MembershipType> tomb = new ArrayList<MembershipType>();
        tomb.add(MembershipType.KORVEZETO);
        tomb.add(MembershipType.GAZDASAGIS);
        tomb.add(MembershipType.PRMENEDZSER);


        CheckBoxMultipleChoice<MembershipType> choices = new CheckBoxMultipleChoice<MembershipType>("choices", tomb) {

            @Override
            protected boolean isDisabled(MembershipType object, int index, String selected) {
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