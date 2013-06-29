package hu.sch.domain.interfaces;

import hu.sch.domain.Membership;
import java.io.Serializable;

/**
 * Ezt kell megvalósítani, ha valamelyik objektumot a MembershipTable-ben akarjuk
 * listáztatni.
 *
 * @author      messo
 * @since       2.3.1
 */
public interface MembershipTableEntry extends Serializable {

    public Membership getMembership();
}
