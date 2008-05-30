/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 *
 * @author hege
 */
@Embeddable
public class Szemeszter implements Serializable {
    /**
     * Szemeszter azonosító, ÉV1ÉV2FÉLÉV formában
     * Pl. 200720081 -> 2007/2008 tanév 1. (őszi) féléve
     */
    protected String id;    
    
    public Szemeszter() {
        id = "200020011";
    }
    
    public Szemeszter(Integer elsoEv, Integer masodikEv, boolean osziFelev) {
        if (elsoEv > 2030 || elsoEv < 2000 || 
                masodikEv > 2030 || masodikEv < 2000) {
            throw new IllegalArgumentException("Az évnek 2000 és 2030 között kell lennie");
        }
        
        setId(elsoEv.toString()+masodikEv.toString()+(osziFelev?"1":"2"));
    }

    @Column(name="semester",length=9,columnDefinition="character(9)",nullable=false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @Transient
    public boolean isOsziFelev() {
        Long sem = Long.parseLong(id);
        return Long.lowestOneBit(sem) == 1;
    }
    
    public void setOsziFelev(boolean osziFelev) {
        setId(getElsoEv().toString()+getMasodikEv().toString()+(osziFelev?"1":"2"));
    }

    @Transient
    public Integer getElsoEv() {
        return Integer.parseInt(id.substring(0, 4));
    }
    
    public void setElsoEv(Integer elsoEv) {
        if (elsoEv > 2030 || elsoEv < 2000) {
            throw new IllegalArgumentException("Az évnek 2000 és 2030 között kell lennie");
        }
        
        setId(elsoEv.toString()+getMasodikEv().toString()+(isOsziFelev()?"1":"2"));
    }
    
    @Transient
    public Integer getMasodikEv() {
        return Integer.parseInt(id.substring(4, 8));
    }
    
    public void setMasodikEv(Integer masodikEv) {
        if (masodikEv > 2030 || masodikEv < 2000) {
            throw new IllegalArgumentException("Az évnek 2000 és 2030 között kell lennie");
        }
        
        setId(getElsoEv()+masodikEv.toString()+(isOsziFelev()?"1":"2"));
    }
    
    /**
     * @return Emészthető formátum: pl 2007/2008 tavasz
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(getElsoEv().toString());
        b.append("/");
        b.append(getMasodikEv().toString());
        b.append(" ");
        b.append(isOsziFelev()?"ősz":"tavasz");
        
        return b.toString();
    }
}
