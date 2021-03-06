package hu.sch.domain;

import hu.sch.domain.user.User;
import hu.sch.domain.interfaces.HasUserRelation;
import hu.sch.util.HungarianStringComparator;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author balo
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractValuationRequest
        implements Serializable, HasUserRelation, Comparable<AbstractValuationRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //----------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "ertekeles_id", nullable = false)
    private Valuation valuation;
    //----------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_id")
    private User user;
    //----------------------------------------------------
    @Column(name = "usr_id", insertable = false, updatable = false)
    private Long userId;
    //----------------------------------------------------
    @Column(name = "ertekeles_id", updatable = false, insertable = false)
    private Long valuationId;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Valuation getValuation() {
        return valuation;
    }

    public void setValuation(final Valuation valuation) {
        this.valuation = valuation;
    }

    public Long getValuationId() {
        return valuationId;
    }

    public void setValuationId(final Long valuationId) {
        this.valuationId = valuationId;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(final User user) {
        this.user = user;
        if (user != null) {
            userId = user.getId();
        }
    }

    /**
     * Felhasználó ID-ja akié a belépőkérelem. Azért kell nekünk egy ilyen
     * külön, mert ha nem akarjuk lekérni a User objektumot, akkor is kíváncsiak
     * lehetünk az ID-ra, amit fel tudunk használni.
     *
     * @return felhasználó azonosítója, akié a belépőkérelem.
     */
    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    @Override
    public int compareTo(final AbstractValuationRequest o) {
        return HungarianStringComparator.scompare(user.getFullName(), o.getUser().getFullName());
    }
}
