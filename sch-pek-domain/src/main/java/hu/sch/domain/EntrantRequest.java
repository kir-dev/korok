package hu.sch.domain;

import hu.sch.domain.enums.EntrantType;
import hu.sch.domain.user.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import org.hibernate.annotations.Index;

/**
 * Egy felhasználóhoz tartozó belépőigény
 *
 * @author hege
 */
@Entity
@Table(name = "belepoigenyles")
public class EntrantRequest extends AbstractValuationRequest {

    //----------------------------------------------------
    @Column(name = "szoveges_ertekeles", columnDefinition = "text", length = 4096)
    private String valuationText;
    //----------------------------------------------------
    @Enumerated(EnumType.STRING)
    @Column(name = "belepo_tipus")
    @Index(name = "bel_tipus_idx")
    private EntrantType entrantType;

    public EntrantRequest() {
        this(null, EntrantType.KDO);
    }

    public EntrantRequest(final User user, final EntrantType entrantType) {
        this.entrantType = entrantType;
        setUser(user);
    }

    public EntrantType getEntrantType() {
        return entrantType;
    }

    public void setEntrantType(final EntrantType entrantType) {
        this.entrantType = entrantType;
    }

    public String getValuationText() {
        return valuationText;
    }

    public void setValuationText(final String valuationText) {
        this.valuationText = valuationText;
    }

    @Override
    public String toString() {
        return "EntrantRequest: " + entrantType + " (" + valuationText + ")";
    }

    /**
     * Lemásoljuk a kérelmet, hogy egy új értékeléshez elmenthessük.
     *
     * @param v az új értékelés, amihez lemásoltuk a kérelmet
     * @return másolat, amit elmenthetünk újként
     */
    public EntrantRequest copy(final Valuation v) {
        final EntrantRequest er = new EntrantRequest();
        er.setValuation(v);
        er.setValuationText(valuationText);
        er.setEntrantType(entrantType);
        er.setUser(getUser());
        return er;
    }

    /**
     * Érvényes-e a belépőkérelem, tehát ha ÁB, vagy KB típusú, akkor legyen
     * hozzá értékelés
     *
     * @return
     */
    public boolean isValid() {
        return !((entrantType == EntrantType.AB || entrantType == EntrantType.KB) && valuationText == null);
    }
}
