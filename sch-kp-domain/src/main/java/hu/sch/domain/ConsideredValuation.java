/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

    public ConsideredValuation(Valuation valuation, ValuationStatus pointStatus, ValuationStatus entrantStatus) {
        setValuation(valuation);
        setPointStatus(pointStatus);
        setEntrantStatus(entrantStatus);
    }

    public ConsideredValuation(Valuation valuation) {
        setValuation(valuation);
        setPointStatus(valuation.getPointStatus());
        setEntrantStatus(valuation.getEntrantStatus());
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

    @Override
    public String toString() {
        return "Elbírált értékelés a " + getValuation().getId() +
                " értékeléshez. Pontigény: " + getPointStatus() +
                "/" + getValuation().getPointStatus() +
                ", Belépőigény: " + getEntrantStatus() +
                "/" + getValuation().getEntrantStatus();
    }

    public boolean isConsidered() {
        return !(getPointStatus().equals(ValuationStatus.ELBIRALATLAN) ||
                getEntrantStatus().equals(ValuationStatus.ELBIRALATLAN));
    }

    public boolean isUnconsidered() {
        return getPointStatus().equals(ValuationStatus.ELBIRALATLAN) &&
                getEntrantStatus().equals(ValuationStatus.ELBIRALATLAN);
    }

    public boolean isConsistent() {
        return !isUnconsidered() && !isConsidered();
    }
}
