/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author hege
 */
@Entity
@Table(name = "system_attrs")
@NamedQuery(name = "findSystemAttributeByAttributeName",
query = "SELECT a FROM SystemAttribute a WHERE a.attributeName=:attributeName")
public class SystemAttribute implements Serializable {

    public static final String findByAttributeName = "findSystemAttributeByAttributeName";
    @Id
    @GeneratedValue
    Long attributeId;
    @Column(nullable = false)
    String attributeName;
    @Column(nullable = false)
    String attributeValue;

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
}
