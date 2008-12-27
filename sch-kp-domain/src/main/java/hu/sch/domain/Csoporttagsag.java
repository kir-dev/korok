/*
 * Member.java
 *
 * Created on April 23, 2007, 1:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Csoporttagságot reprezentáló entity
 * @author hege
 */
@Entity
@Table(name = "grp_members")
public class Csoporttagsag implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String find = "findCsoporttagsag";
    private CsoporttagsagPK id;
    private Felhasznalo felhasznalo;
    private Csoport csoport;
    /**
     * A csoporttagság idejének kezdete - kötelező
     */
    private Date kezdet;
    /**
     * A csoporttagság vége - nem kötelező
     */
    private Date veg;
    /**
     * Jogok tárolására bitmaszk
     */
    private Long jogok;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grp_id", insertable = false, updatable = false)
    public Csoport getCsoport() {
        return csoport;
    }

    public void setCsoport(Csoport csoport) {
        this.csoport = csoport;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "usr_id", insertable = false, updatable = false)
    public Felhasznalo getFelhasznalo() {
        return felhasznalo;
    }

    public void setFelhasznalo(Felhasznalo felhasznalo) {
        this.felhasznalo = felhasznalo;
    }

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "felhasznaloID", column = @Column(name = "usr_id")),
        @AttributeOverride(name = "csoportID", column = @Column(name = "grp_id"))
    })
    public CsoporttagsagPK getId() {
        return id;
    }

    public void setId(CsoporttagsagPK id) {
        this.id = id;
    }

    @Column(name = "membership_start", nullable = false, columnDefinition = "date")
    @Temporal(TemporalType.DATE)
    public Date getKezdet() {
        return kezdet;
    }

    public void setKezdet(Date kezdet) {
        this.kezdet = kezdet;
    }

    @Column(name = "membership_end", nullable = true, columnDefinition = "date")
    @Temporal(TemporalType.DATE)
    public Date getVeg() {
        return veg;
    }

    public void setVeg(Date veg) {
        this.veg = veg;
    }

    @Column(name = "member_rights", nullable = false, columnDefinition = "INTEGER")
    public Long getJogok() {
        return jogok;
    }

    @Transient
    public TagsagTipus[] getJogokString() {
        if (veg != null) {
            TagsagTipus[] ret = new TagsagTipus[1];
            ret[0] = TagsagTipus.OREGTAG;
            return ret;
        }
        return TagsagTipus.getTagsagTipusByJogok(jogok);
    }

//    @Transient
//    public boolean isKorvezeto() {
//        Long jogok1 = getJogok();
//        if ((jogok1 & 1) != 0) {
//            return true;
//        } else {
//            return false;
//        }
//    }
    public void setJogok(Long jogok) {
        this.jogok = jogok;
    }
}

