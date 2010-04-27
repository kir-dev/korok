/**
 * Copyright (c) 2009-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
