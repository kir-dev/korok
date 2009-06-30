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
public class PointRequest implements Serializable {

    protected Long id;
    protected Valuation valuation;
    protected Integer point;
    protected User user;

    public PointRequest() {
    }

    public PointRequest(User user, Integer point) {
        this.point = point;
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "ertekeles_id", nullable = false)
    public Valuation getValuation() {
        return valuation;
    }

    public void setValuation(Valuation valuation) {
        this.valuation = valuation;
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
    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    @ManyToOne
    @JoinColumn(name = "usr_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
