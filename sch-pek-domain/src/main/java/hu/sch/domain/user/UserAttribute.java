package hu.sch.domain.user;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

/**
 * Represents the visibility of a user attribute.
 *
 * @author tomi
 */
@Embeddable
public class UserAttribute implements Serializable {

    /**
     * The name of the attribute.
     *
     * NOTE: DO NOT RENAME IT. Hibernate uses its name for the mapping, because
     * it is an embeddable class.
     */
    @Transient
    private String attr_name;
    /**
     * Visibility of the attribute.
     */
    private Boolean visible = false;

    public UserAttribute() {
    }

    public UserAttribute(UserAttributeName attributeName, boolean visible) {
        this.attr_name = attributeName.name();
        this.visible = visible;
    }

    public UserAttributeName getAttributeName() {
        return UserAttributeName.valueOf(attr_name);
    }

    public void setAttributeName(UserAttributeName attributeName) {
        this.attr_name = attributeName.name();
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean isVisible) {
        this.visible = isVisible;
    }
}
