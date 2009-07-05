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
    KORVEZETO(1, "körvezető"),
    VOLTKORVEZETO(2),
    GAZDASAGIS(4, "gazdaságis"),
    PRMENEDZSER(8, "PR menedzser"),
    VENDEGFOGADAS(16),
    OREGTAG(16384),
    JELENTKEZO(32768);
    
    private final int value;
    private final String name;

    private MembershipType(int value) {
        this.value = value;
        this.name = null;
    }
    
    private MembershipType(int value, String name) {
        this.value = value;
        this.name = name;
    }
    
    @Override
    public String toString() {
        if (name != null)
            return name;
        
        return super.toString();
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
