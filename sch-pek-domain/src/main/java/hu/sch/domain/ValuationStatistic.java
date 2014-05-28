package hu.sch.domain;

import java.io.Serializable;

/**
 *
 * @author hege
 */
public class ValuationStatistic implements Serializable {

    private Valuation valuation;
    private Double averagePoint;
    private Long summaPoint;
    private Long givenKDO;
    private Long givenKB;
    private Long givenAB;

    public ValuationStatistic(Valuation valuation, Double averagePoint, Long summaPoint,
            Long givenKDO, Long givenKB, Long givenAB) {
        this.valuation = valuation;
        //nem minden értékeléshez vannak pontok a DB-ben, ha nincs rekord, null-t dob (#919)
        this.averagePoint = averagePoint == null ? 0.0 : averagePoint;
        this.summaPoint = summaPoint == null ? 0 : summaPoint;
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
