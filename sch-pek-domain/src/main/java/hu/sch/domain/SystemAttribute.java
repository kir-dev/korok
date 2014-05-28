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
@NamedQuery(name = SystemAttribute.findByAttributeName,
query = "SELECT a FROM SystemAttribute a WHERE a.attributeName=:attributeName")
public class SystemAttribute implements Serializable {

    public static final String findByAttributeName = "findSystemAttributeByAttributeName";
    public static final String LAST_LOG = "utolso_log_kuldve";
    public static final String SEMESTER = "szemeszter";
    public static final String VALUATION_PERIOD = "ertekeles_idoszak";
    public static final String NEWBIE_TIME = "golyaidoszak";
    @Id
    @GeneratedValue
    private Long attributeId;
    //----------------------------------------------------
    @Column(nullable = false)
    private String attributeName;
    //----------------------------------------------------
    @Column(nullable = false)
    private String attributeValue;

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
