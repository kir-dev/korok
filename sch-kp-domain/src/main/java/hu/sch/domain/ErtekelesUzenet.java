/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Az egyes értékelésekhez tartozó üzeneteket reprezentáló entitás
 * 
 * @see Ertekeles
 * @author hege
 */
@Entity
@Table(name="ertekeles_uzenet")
public class ErtekelesUzenet implements Serializable {
    protected Long id;
    protected Ertekeles ertekeles;
    protected Felhasznalo felado;
    protected Date datum;
    protected String uzenet;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="feladas_ido")
    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    @ManyToOne
    @JoinColumn(name="ertekeles_id")
    public Ertekeles getErtekeles() {
        return ertekeles;
    }

    public void setErtekeles(Ertekeles ertekeles) {
        this.ertekeles = ertekeles;
    }

    @ManyToOne
    @JoinColumn(name="felado_usr_id")
    public Felhasznalo getFelado() {
        return felado;
    }

    public void setFelado(Felhasznalo felado) {
        this.felado = felado;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="uzenet", columnDefinition="text", length=4096)
    public String getUzenet() {
        return uzenet;
    }

    public void setUzenet(String uzenet) {
        this.uzenet = uzenet;
    }
    
    @PrePersist
    public void setDefaultValues() {
        setDatum(new Date());
    }
}
