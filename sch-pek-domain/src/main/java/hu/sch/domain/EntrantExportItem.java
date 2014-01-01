package hu.sch.domain;

import java.io.Serializable;

/**
 * A belépő exportnál egy export egy elemet reprezentál.
 *
 * Egy felhasználóhoz több ilyen is tartozhat.
 *
 * @author  messo
 * @author  balo
 * @author  tomi
 * @since   2.4
 */
public class EntrantExportItem implements Serializable {

    private Long userId;
    private String fullName;
    private String neptun;
    private String email;
    private String primaryGroup;
    private String valuationGroupName;
    private String valuationText;

    public EntrantExportItem() {
    }

    public EntrantExportItem(Long userId, String firstName, String lastName, String neptun, String email, String primaryGroup, String valuationGroupName, String valuationText) {
        this.userId = userId;
        this.fullName = lastName + " " + firstName;
        this.neptun = neptun;
        this.email = email;
        this.primaryGroup = primaryGroup;
        this.valuationGroupName = valuationGroupName;
        this.valuationText = valuationText;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getNeptun() {
        return neptun;
    }

    public String getEmail() {
        return email;
    }

    public String getPrimaryGroup() {
        return primaryGroup;
    }

    public String getValuationGroupName() {
        return valuationGroupName;
    }

    public String getValuationText() {
        return valuationText.replaceAll("[\\t, \\n, \\r]", " ");
    }
}
