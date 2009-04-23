/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author hege
 */
@Entity
@Table(name = "ertekelesek")
@NamedQueries({
    @NamedQuery(name = "findErtekelesBySzemeszterAndCsoport",
    query = "SELECT e FROM Ertekeles e WHERE e.szemeszter=:szemeszter " +
    "AND e.csoport=:csoport"),
    @NamedQuery(name = "findErtekelesByIdUzenetJoined",
    query = "SELECT e FROM Ertekeles e LEFT JOIN FETCH e.uzenetek " +
    "WHERE e.id=:id"),
    @NamedQuery(name = "findErtekelesByCsoport",
    query = "SELECT e FROM Ertekeles e WHERE e.csoport=:csoport " +
    "ORDER BY e.szemeszter DESC")
})
public class Ertekeles implements Serializable {

    public static final String findByIdUzenetJoined = "findErtekelesByIdUzenetJoined";
    public static final String findBySzemeszterAndCsoport = "findErtekelesBySzemeszterAndCsoport";
    public static final String findByCsoport = "findErtekelesByCsoport";
    protected Long id;
    protected Csoport csoport;
    protected Felhasznalo felado;
    protected Felhasznalo elbiralo;
    protected String szovegesErtekeles;
    protected ErtekelesStatusz pontStatusz;
    protected ErtekelesStatusz belepoStatusz;
    protected Szemeszter szemeszter;
    protected Date feladas;
    protected Date utolsoElbiralas;
    protected Date utolsoModositas;
    protected List<BelepoIgeny> belepoIgenyek;
    protected List<PontIgeny> pontIgenyek;
    protected List<ErtekelesUzenet> uzenetek;
    protected Float pontAtlag;

    @ManyToOne
    @JoinColumn(name = "grp_id")
    public Csoport getCsoport() {
        return csoport;
    }

    public void setCsoport(Csoport csoport) {
        this.csoport = csoport;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "felado_usr_id")
    public Felhasznalo getFelado() {
        return felado;
    }

    public void setFelado(Felhasznalo felado) {
        this.felado = felado;
    }

    @Embedded
    public Szemeszter getSzemeszter() {
        return szemeszter;
    }

    public void setSzemeszter(Szemeszter szemeszter) {
        this.szemeszter = szemeszter;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "utolso_elbiralas")
    public Date getUtolsoElbiralas() {
        return utolsoElbiralas;
    }

    public void setUtolsoElbiralas(Date utolsoElbiralas) {
        this.utolsoElbiralas = utolsoElbiralas;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "feladas")
    public Date getFeladas() {
        return feladas;
    }

    public void setFeladas(Date feladas) {
        this.feladas = feladas;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "utolso_modositas")
    public Date getUtolsoModositas() {
        return utolsoModositas;
    }

    public void setUtolsoModositas(Date utolsoModositas) {
        this.utolsoModositas = utolsoModositas;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "belepoigeny_statusz")
    public ErtekelesStatusz getBelepoStatusz() {
        return belepoStatusz;
    }

    public void setBelepoStatusz(ErtekelesStatusz belepoStatusz) {
        this.belepoStatusz = belepoStatusz;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "pontigeny_statusz")
    public ErtekelesStatusz getPontStatusz() {
        return pontStatusz;
    }

    public void setPontStatusz(ErtekelesStatusz pontStatusz) {
        this.pontStatusz = pontStatusz;
    }

    @OneToMany(mappedBy = "ertekeles", fetch = FetchType.LAZY)
    public List<BelepoIgeny> getBelepoIgenyek() {
        return belepoIgenyek;
    }

    public void setBelepoIgenyek(List<BelepoIgeny> belepoIgenyek) {
        this.belepoIgenyek = belepoIgenyek;
    }

    @OneToMany(mappedBy = "ertekeles", fetch = FetchType.LAZY)
    public List<PontIgeny> getPontIgenyek() {
        return pontIgenyek;
    }

    public void setPontIgenyek(List<PontIgeny> pontIgenyek) {
        this.pontIgenyek = pontIgenyek;
    }

    @Column(name = "szoveges_ertekeles", columnDefinition = "text", length = 4096, nullable = false)
    @Basic(fetch = FetchType.LAZY)
    public String getSzovegesErtekeles() {
        return szovegesErtekeles;
    }

    public void setSzovegesErtekeles(String szovegesErtekeles) {
        this.szovegesErtekeles = szovegesErtekeles;
    }

    @PrePersist
    protected void setDefaultValues() {
        setFeladas(new Date());
        setUtolsoModositas(new Date());
        setPontStatusz(ErtekelesStatusz.NINCS);
        setBelepoStatusz(ErtekelesStatusz.NINCS);
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "elbiralo_usr_id")
    public Felhasznalo getElbiralo() {
        return elbiralo;
    }

    public void setElbiralo(Felhasznalo elbiralo) {
        this.elbiralo = elbiralo;
    }

    @OneToMany(mappedBy = "ertekeles", fetch = FetchType.LAZY)
    public List<ErtekelesUzenet> getUzenetek() {
        return uzenetek;
    }

    public void setUzenetek(List<ErtekelesUzenet> uzenetek) {
        this.uzenetek = uzenetek;
    }

    @Transient
    public Float getPontAtlag() {
        return pontAtlag;
    }

    public void setPontAtlag(Float pontAtlag) {
        this.pontAtlag = pontAtlag;
    }
}
