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
public enum MembershipType {

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

    private MembershipType(int value) {
        this.value = value;
    }

    static MembershipType[] getMembershipTypeFromRights(Long rights) {
        if (rights == 0) {
            return new MembershipType[]{TAG};
        }
        ArrayList<MembershipType> retList = new ArrayList<MembershipType>(8);
        for (MembershipType t : values()) {
            if ((rights & t.value) != 0) {
                retList.add(t);
            }
        }
        if (retList.isEmpty()) {
            retList.add(JELENTKEZO);
        }
        return retList.toArray(new MembershipType[retList.size()]);
    }

    public static MembershipType fromEntitlement(String entitlement) {
        if (entitlement.equalsIgnoreCase("tag")) {
            return TAG;
        } else if (entitlement.equalsIgnoreCase("korvezeto")) {
            return KORVEZETO;
        } else if (entitlement.equalsIgnoreCase("gazdasagis")) {
            return GAZDASAGIS;
        }

        return null;
    }

    public static boolean hasJogInGroup(Membership membership, MembershipType type) {
        Long jogok = membership.getRights();
        return (jogok & type.value) != 0;
    }

    public static Long addOrRemoveEntitlement(Long current, MembershipType type) {
        return (current ^ type.value);
    }
}
