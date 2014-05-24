package hu.sch.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author tomi
 */
public interface EntityView {

    /**
     * Determines whether it has a valid, full-grown entity.
     * @return true if the entity is not null
     */
    @JsonIgnore
    boolean hasEntity();

    /**
     * Gets the name of the entity class.
     * @return name of the entity class.
     */
    @JsonIgnore
    String getEntityName();
}
