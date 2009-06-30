/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import hu.sch.domain.Membership;
import hu.sch.domain.MembershipType;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.IClusterable;

/**
 *
 * @author aldaris
 */
public class Input implements IClusterable {

    List<MembershipType> choices = new ArrayList<MembershipType>();
    Membership cst = null;

    public Input(Membership cst) {
        for (MembershipType tagsagTipus : cst.getRightsAsString()) {
            choices.add(tagsagTipus);
        }
    }

    public List<MembershipType> getChoices() {
        return choices;
    }

    public void setChoices(List<MembershipType> temp) {
        choices = temp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MembershipType tagsagTipus : choices) {
            sb.append(tagsagTipus.toString());
        }
        return sb.toString();
    }
}
