/**
 * Copyright (c) 2008-2010, Peter Major
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