/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author hege
 */
@Entity
@Table(name = "pontigenyles")
public class PontIgeny implements Serializable {

    protected Long id;
    protected Ertekeles ertekeles;
    protected Integer pont;
    protected Felhasznalo felhasznalo;

    public PontIgeny() {
    }

    public PontIgeny(Felhasznalo felhasznalo, Integer pont) {
        this.pont = pont;
        this.felhasznalo = felhasznalo;
    }

    @ManyToOne
    @JoinColumn(name = "ertekeles_id", nullable = false)
    public Ertekeles getErtekeles() {
        return ertekeles;
    }

    public void setErtekeles(Ertekeles ertekeles) {
        this.ertekeles = ertekeles;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "pont")
    public Integer getPont() {
        return pont;
    }

    public void setPont(Integer pont) {
        this.pont = pont;
    }

    @ManyToOne
    @JoinColumn(name = "usr_id")
    public Felhasznalo getFelhasznalo() {
        return felhasznalo;
    }

    public void setFelhasznalo(Felhasznalo felhasznalo) {
        this.felhasznalo = felhasznalo;
    }
}
