/*
 * Csoport.java
 *
 * Created on April 23, 2007, 1:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author hege
 */
@Entity
@Table(name = "groups")
@NamedQuery(name = "findAllCsoport", query = "SELECT cs FROM Csoport cs")
public class Csoport implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String findAll = "findAllCsoport";

    /*
    grp_id          | integer                | not null
    grp_name        | text                   | not null
    grp_type        | character varying(16)  | not null
    grp_parent      | integer                | 
    grp_state       | character(3)           | default 'akt'::bpchar
    grp_description | text                   | 
    grp_webpage     | character varying(64)  | 
    grp_maillist    | character varying(64)  | 
    grp_head        | character varying(48)  | 
    grp_founded     | integer                | 
    grp_flags       | integer                | 
    grp_acc_cards   | integer                | 
    grp_acc_points  | integer                | 
    is_del          | boolean                | default false
    grp_shortname   | character varying(128) | 
     */
    /**
     * Csoport azonosító id
     */
    private Long id;
    /**
     * Csoport neve
     */
    private String nev;
    /**
     * Típus
     */
    private String tipus;
    /**
     * Szülő csoport
     */
    private Csoport szulo;
    /**
     * Státusz (aktiv / öreg)
     */
    private CsoportStatusz statusz;
    /**
     * Oszthat-e pontot/belépőt
     */
    private Integer flagek;
    /**
     * Csoporttagságok
     */
    private List<Csoporttagsag> csoporttagsagok;
    /**
     * Cache-elt mező
     */
    private List<Felhasznalo> csoporttagok;

    /** Creates a new instance of Csoport */
    public Csoport() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "grp_id")
    public Long getId() {
        return id;
    }

    @Column(name = "grp_name", length = 255, columnDefinition = "text")
    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    @OneToMany(mappedBy = "csoport", fetch = FetchType.LAZY)
    public List<Csoporttagsag> getCsoporttagsagok() {
        return csoporttagsagok;
    }

    public void setCsoporttagsagok(List<Csoporttagsag> csoporttagsagok) {
        this.csoporttagsagok = csoporttagsagok;
    }

    @Column(name = "grp_flags")
    public Integer getFlagek() {
        return flagek;
    }

    public void setFlagek(Integer flagek) {
        this.flagek = flagek;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "grp_state")
    public CsoportStatusz getStatusz() {
        return statusz;
    }

    public void setStatusz(CsoportStatusz statusz) {
        this.statusz = statusz;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "grp_parent")
    public Csoport getSzulo() {
        return szulo;
    }

    public void setSzulo(Csoport szulo) {
        this.szulo = szulo;
    }

    @Column(name = "grp_type")
    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    @Transient
    public List<Felhasznalo> getCsoporttagok() {
        if (csoporttagok == null) {
            loadCsoporttagok();
        }
        return csoporttagok;
    }
    
    private void loadCsoporttagok() {
        csoporttagok = new ArrayList<Felhasznalo>();
        for (Csoporttagsag cst : getCsoporttagsagok()) {
            csoporttagok.add(cst.getFelhasznalo());
        }
    }
    

    @Override
    public String toString() {
        return "hu.uml13.domain.Csoport id=" + getId() +
                ", nev=" + getNev();
    }
}
