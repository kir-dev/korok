/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

/**
 *
 * @author hege
 */
public enum GroupStatus {

    akt, old;

    @Override
    public String toString() {
        if (this.equals(GroupStatus.akt)) {
            return "aktív";
        } else if (this.equals(GroupStatus.old)) {
            return "öreg";
        } else {
            throw new RuntimeException("Nem várt csoportstátusz");
        }
    }
}
