/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.util.ArrayList;

/**
 *
 * @author aldaris
 */
public enum TagsagTipus {

    TAG(0),
    KORVEZETO(1) {

        @Override
        public String toString() {
            return "körvezető";
        }
    },
    VOLTKORVEZETO(2),
    GAZDASAGIS(4) {

        @Override
        public String toString() {
            return "gazdaságis";
        }
    },
    PRMENEDZSER(8) {

        @Override
        public String toString() {
            return "PR menedzser";
        }
    },
    VENDEGFOGADAS(16),
    OREGTAG(16384),
    JELENTKEZO(32768);
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
            if ((jogok & t.value) != 0) {
                retList.add(t);
            }
        }
        if (retList.isEmpty()) {
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

    public static boolean hasJogCsoportban(Csoporttagsag cstagsag, TagsagTipus type) {
        Long jogok = cstagsag.getJogok();
        return (jogok & type.value) != 0;
    }

    public static Long addOrRemoveEntitlement(Long current, TagsagTipus type) {
        return (current ^ type.value);
    }
}
