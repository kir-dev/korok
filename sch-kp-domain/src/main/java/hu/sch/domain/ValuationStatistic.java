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
public class ValuationStatistic implements Serializable {

    protected Valuation valuation;
    protected Double averagePoint;
    protected Long summaPoint;
    protected Long givenKDO;
    protected Long givenKB;
    protected Long givenAB;

    public ValuationStatistic(Valuation valuation, Double averagePoint, Long summaPoint,
            Long givenKDO, Long givenKB, Long givenAB) {
        this.valuation = valuation;
        this.averagePoint = averagePoint;
        this.summaPoint = summaPoint;
        this.givenKDO = givenKDO;
        this.givenKB = givenKB;
        this.givenAB = givenAB;
    }

    public Double getAveragePoint() {
        return averagePoint;
    }

    public void setAveragePoint(Double averagePoint) {
        this.averagePoint = averagePoint;
    }

    public Long getGivenAB() {
        return givenAB;
    }

    public void setGivenAB(Long givenAB) {
        this.givenAB = givenAB;
    }

    public Long getGivenKB() {
        return givenKB;
    }

    public void setGivenKB(Long givenKB) {
        this.givenKB = givenKB;
    }

    public Long getGivenKDO() {
        return givenKDO;
    }

    public void setGivenKDO(Long givenKDO) {
        this.givenKDO = givenKDO;
    }

    public Long getSummaPoint() {
        return summaPoint;
    }

    public void setSummaPoint(Long summaPoint) {
        this.summaPoint = summaPoint;
    }

    public Valuation getValuation() {
        return valuation;
    }

    public void setValuation(Valuation valuation) {
        this.valuation = valuation;
    }
}
