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
public class EntrantRequest implements Serializable {

    protected Long id;
    protected Valuation valuation;
    protected String valuationText;
    protected EntrantType entrantType;
    protected User user;

    public EntrantRequest() {
    }

    public EntrantRequest(User user, EntrantType entrantType) {
        this.entrantType = entrantType;
        this.user = user;
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
    public EntrantType getEntrantType() {
        return entrantType;
    }

    public void setEntrantType(EntrantType entrantType) {
        this.entrantType = entrantType;
    }

    @ManyToOne
    @JoinColumn(name = "ertekeles_id", nullable = false)
    public Valuation getValuation() {
        return valuation;
    }

    public void setValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    @ManyToOne
    @JoinColumn(name = "usr_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "szoveges_ertekeles", columnDefinition = "text", length = 4096)
    public String getValuationText() {
        return valuationText;
    }

    public void setValuationText(String valuationText) {
        this.valuationText = valuationText;
    }
}
