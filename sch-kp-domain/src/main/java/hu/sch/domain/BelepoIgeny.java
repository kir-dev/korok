/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Egy felhasználóhoz tartozó belépőigény
 * 
 * @author hege
 */
@Entity
@Table(name = "belepoigenyles")
public class BelepoIgeny implements Serializable {

    protected Long id;
    protected Ertekeles ertekeles;
    protected String szovegesErtekeles;
    protected BelepoTipus belepotipus;
    protected Felhasznalo felhasznalo;

    public BelepoIgeny() {
    }

    public BelepoIgeny(Felhasznalo felhasznalo, BelepoTipus belepotipus) {
        this.belepotipus = belepotipus;
        this.felhasznalo = felhasznalo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "belepo_tipus")
    public BelepoTipus getBelepotipus() {
        return belepotipus;
    }

    public void setBelepotipus(BelepoTipus belepotipus) {
        this.belepotipus = belepotipus;
    }

    @ManyToOne
    @JoinColumn(name = "ertekeles_id", nullable = false)
    public Ertekeles getErtekeles() {
        return ertekeles;
    }

    public void setErtekeles(Ertekeles ertekeles) {
        this.ertekeles = ertekeles;
    }

    @ManyToOne
    @JoinColumn(name = "usr_id")
    public Felhasznalo getFelhasznalo() {
        return felhasznalo;
    }

    public void setFelhasznalo(Felhasznalo felhasznalo) {
        this.felhasznalo = felhasznalo;
    }

    @Column(name = "szoveges_ertekeles", columnDefinition = "text", length = 4096)
    public String getSzovegesErtekeles() {
        return szovegesErtekeles;
    }

    public void setSzovegesErtekeles(String szovegesErtekeles) {
        this.szovegesErtekeles = szovegesErtekeles;
    }
}
