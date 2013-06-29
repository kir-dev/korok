package hu.sch.domain;

import java.io.Serializable;

/**
 *
 * @author hege
 */
public class ConsideredValuation implements Serializable {

    private Valuation valuation;
    private ValuationStatus pointStatus;
    private ValuationStatus entrantStatus;
    private String explanation;
    private User user;

    public ConsideredValuation(Valuation valuation, ValuationStatus pointStatus, ValuationStatus entrantStatus, User user) {
        this.valuation = valuation;
        this.pointStatus = pointStatus;
        this.entrantStatus = entrantStatus;
        this.user = user;
    }

    public ConsideredValuation(Valuation valuation, User user) {
        this.valuation = valuation;
        this.pointStatus = valuation.getPointStatus();
        this.entrantStatus = valuation.getEntrantStatus();
        this.user = user;
    }

    public ValuationStatus getEntrantStatus() {
        return entrantStatus;
    }

    public void setEntrantStatus(ValuationStatus entrantStatus) {
        this.entrantStatus = entrantStatus;
    }

    public Valuation getValuation() {
        return valuation;
    }

    public void setValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public ValuationStatus getPointStatus() {
        return pointStatus;
    }

    public void setPointStatus(ValuationStatus pointStatus) {
        this.pointStatus = pointStatus;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Elbírált értékelés a " + getValuation().getId()
                + " értékeléshez. Pontigény: " + getPointStatus()
                + "/" + getValuation().getPointStatus()
                + ", Belépőigény: " + getEntrantStatus()
                + "/" + getValuation().getEntrantStatus();
    }

    public boolean isConsidered() {
        return !(getPointStatus().equals(ValuationStatus.ELBIRALATLAN)
                || getEntrantStatus().equals(ValuationStatus.ELBIRALATLAN));
    }

    public boolean isUnconsidered() {
        return getPointStatus().equals(ValuationStatus.ELBIRALATLAN)
                && getEntrantStatus().equals(ValuationStatus.ELBIRALATLAN);
    }

    public boolean isConsistent() {
        return !isUnconsidered() && !isConsidered();
    }
}
