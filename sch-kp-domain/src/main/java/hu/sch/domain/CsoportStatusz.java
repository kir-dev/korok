/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.domain;

/**
 *
 * @author hege
 */
public enum CsoportStatusz {
    akt,old;

    @Override
    public String toString() {
        if (this.equals(CsoportStatusz.akt)) {
            return "aktív";
        } else if (this.equals(CsoportStatusz.old)) {
            return "öreg";
        } else {
            throw new RuntimeException("Nem vart csoport statusz");
        }
    }
}
