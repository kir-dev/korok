/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
 * @see Valuation
 * @author hege
 */
@Entity
@Table(name = "ertekeles_uzenet")
public class ValuationMessage implements Serializable {

    protected Long id;
    protected Valuation valuation;
    protected User sender;
    protected Date date;
    protected String message;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "feladas_ido")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToOne
    @JoinColumn(name = "ertekeles_id")
    public Valuation getValuation() {
        return valuation;
    }

    public void setValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    @ManyToOne
    @JoinColumn(name = "felado_usr_id")
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "uzenet", columnDefinition = "text", length = 4096)
    public String getMessage() {
        return message;
    }

    public void setMessage(String uzenet) {
        this.message = uzenet;
    }

    @PrePersist
    public void setDefaultValues() {
        setDate(new Date());
    }

    @Override
    public String toString() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MMMMM dd. HH:mm:ss", new Locale("hu"));
        String result = "Feladó: " + sender.getName() + "\n" +
                "Dátum: " + dateFormat.format(date) + "\n" +
                "Üzenet szövege:\n\n" + message;

        return result;
    }
}
