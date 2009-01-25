/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author hege
 */
@Embeddable
public class CsoporttagsagPK implements Serializable {

    private Long felhasznaloID;
    private Long csoportID;

    public CsoporttagsagPK() {
    }

    public CsoporttagsagPK(Long felhasznaloID, Long csoportID) {
        this.felhasznaloID = felhasznaloID;
        this.csoportID = csoportID;
    }

    @Column(name = "grp_id", nullable = false)
    public Long getCsoportID() {
        return csoportID;
    }

    public void setCsoportID(Long csoportID) {
        this.csoportID = csoportID;
    }

    @Column(name = "usr_id", nullable = false)
    public Long getFelhasznaloID() {
        return felhasznaloID;
    }

    public void setFelhasznaloID(Long felhasznaloID) {
        this.felhasznaloID = felhasznaloID;
    }
}