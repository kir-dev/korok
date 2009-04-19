/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.util.ArrayList;

/**
 *
 * @author major
 */
public enum TagsagTipus {

    TAG(0),
    KORVEZETO(1),
    VOLTKORVEZETO(2),
    GAZDASAGIS(3),
    PRMENEDZSER(4),
    VENDEGFOGADAS(5),
    OREGTAG(15),
    JELENTKEZO(16);
    private final int value;

    private TagsagTipus(int value) {
        this.value = value;
    }

    static TagsagTipus[] getTagsagTipusByJogok(Long jogok) {
        ArrayList<TagsagTipus> retList = new ArrayList<TagsagTipus>();
        TagsagTipus[] ret = new TagsagTipus[1];
        if (jogok == 0) {
            retList.add(TAG);
            return retList.toArray(ret);
        }
        for (TagsagTipus t : values()) {
            if ((jogok & (1 << t.value - 1)) != 0) {
                retList.add(t);
            }
        }
        if (retList.size() == 0) {
            retList.add(JELENTKEZO);
        }
        return retList.toArray(ret);
    }

    public static TagsagTipus fromEntitlement(String entitlement) {
        if (entitlement.equalsIgnoreCase("tag")) {
            return TAG;
        } else if (entitlement.equalsIgnoreCase("korvezeto")) {
            return KORVEZETO;
        } else if (entitlement.equalsIgnoreCase("gazdasagis")) {
            return GAZDASAGIS;
        }

        return null;
    }
}
