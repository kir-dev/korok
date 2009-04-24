/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.TagsagTipus;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.IClusterable;

/**
 *
 * @author aldaris
 */
public class Input implements IClusterable {

    List<TagsagTipus> choices = new ArrayList<TagsagTipus>();
    Csoporttagsag cst = null;

    public Input(Csoporttagsag cst) {
        for (TagsagTipus tagsagTipus : cst.getJogokString()) {
            choices.add(tagsagTipus);
        }
    }

    public List<TagsagTipus> getChoices() {
        return choices;
    }

    public void setChoices(List<TagsagTipus> temp) {
        choices = temp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (TagsagTipus tagsagTipus : choices) {
            sb.append(tagsagTipus.toString());
        }
        return sb.toString();
    }
}
