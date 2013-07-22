package hu.sch.domain.user;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Represents the visibility of a user attribute.
 *
 * @author tomi
 */
@Entity
@Table(name = "usr_private_attrs")
@SequenceGenerator(name = "usr_attrs_seq", sequenceName = "usr_private_attrs_id_seq")
public class UserAttribute implements Serializable {

    @Id
    @GeneratedValue(generator =  "usr_attrs_seq")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "attr_name")
    @NotNull
    private UserAttributeName attrName;

    @Column(name = "visible")
    @NotNull
    private Boolean visible = Boolean.FALSE;

    public UserAttribute() {
    }

    public UserAttribute(UserAttributeName attributeName, boolean visible) {
        this.attrName = attributeName;
        this.visible = visible;
    }

    /**
     * Gets the id of the entit.
     * @return
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the attribute.
     */
    public UserAttributeName getAttributeName() {
        return attrName;
    }

    public void setAttributeName(UserAttributeName attributeName) {
        this.attrName = attributeName;
    }

    /**
     * Gets the visibility of the attribute.
     */
    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean isVisible) {
        this.visible = isVisible;
    }
}