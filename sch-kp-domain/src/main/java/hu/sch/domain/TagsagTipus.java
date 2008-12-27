/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author major
 */
public enum TagsagTipus {

    TAG(0) {

        @Override
        public String toString() {
            return enumString[0];
        }
    },
    KORVEZETO(1) {

        @Override
        public String toString() {
            return enumString[1];
        }
    },
    VOLTKORVEZETO(2) {

        @Override
        public String toString() {
            return enumString[2];
        }
    },
    GAZDASAGIS(3) {

        @Override
        public String toString() {
            return enumString[3];
        }
    },
    PRMENEDZSER(4) {

        @Override
        public String toString() {
            return enumString[4];
        }
    },
    VENDEGFOGADAS(5) {

        @Override
        public String toString() {
            return enumString[5];
        }
    },
    JELENTKEZO(16) {

        @Override
        public String toString() {
            return enumString[16];
        }
    };
    private final int value;
    private static final String[] enumString;


    static {
        enumString = new String[16];
        enumString[0] = "tag";
        enumString[1] = "körvezető";
        enumString[2] = "volt körvezető";
        enumString[3] = "gazdaságis";
        enumString[4] = "PR menedzser";
        enumString[5] = "vendégfogadó";
        enumString[15] = "jelentkező";
    }

    private TagsagTipus(int value) {
        this.value = value;
    }

    static String getTagsagTipusByJogok(Long jogok) {
        String ret = new String();
        if (jogok == 0) {
            return "tag";
        }
        for (int i = KORVEZETO.value; i < JELENTKEZO.value; i++) {
            if ((jogok & (1 << i - 1)) != 0) {
                ret += (ret.length() != 0) ? ", " + enumString[i] : enumString[i];
            }
        }
        if (ret.length() == 0) {
            ret = enumString[15];
        }
        return ret;
    }

    static boolean hasJogCsoportban(Csoporttagsag cstagsag, TagsagTipus type) {
        String jogok = getTagsagTipusByJogok(cstagsag.getJogok());
        String[] temp;
        temp = jogok.split(", ");
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].equals(enumString[type.value])) {
                return true;
            }
        }
        return false;
    }

    static List<Csoporttagsag> getCsoportokWhereSzerepbenVagyok(List<Csoporttagsag> cstagsag, TagsagTipus type) {
        List ret = new ArrayList<Csoporttagsag>();
        Iterator iterator = cstagsag.iterator();
        while (iterator.hasNext()) {
            Csoporttagsag tagsag = (Csoporttagsag) iterator.next();
            if (hasJogCsoportban(tagsag, type)) {
                ret.add(tagsag);
            }
        }
        return ret;
    }
}
