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
 * Egy felhasználó értékelését jellemzi (pont- és belépőkérelem)
 *
 * @author  messo
 * @since   2.3.1
 * @see     PointRequest
 * @see     EntrantRequest
 */
public class ValuationData implements Serializable {

    protected User user;
    protected Valuation valuation = null;
    protected PointRequest pointRequest;
    protected EntrantRequest entrantRequest;

    public ValuationData(User user, PointRequest pointRequest, EntrantRequest entrantRequest) {
        this.user = user;
        this.pointRequest = pointRequest;
        this.entrantRequest = entrantRequest;

        init();
    }

    private void init() {
        if (pointRequest == null) {
            pointRequest = new PointRequest();
            // ha a pontkérelem null, akkor lennie kell belépőnek
            valuation = entrantRequest.getValuation();
        }
        if (entrantRequest == null) {
            entrantRequest = new EntrantRequest();
            // ha a belépőkérelem null, akkor lennie kell pontnak
            valuation = pointRequest.getValuation();
        }
    }

    public User getUser() {
        return user;
    }

    public Group getGroup() {
        return valuation.getGroup();
    }

    public Semester getSemester() {
        return valuation.getSemester();
    }

    public EntrantRequest getEntrantRequest() {
        return entrantRequest;
    }

    public void setEntrantRequest(EntrantRequest eReq) {
        this.entrantRequest = eReq;
    }

    public PointRequest getPointRequest() {
        return pointRequest;
    }

    public void setPointRequest(PointRequest pReq) {
        this.pointRequest = pReq;
    }
}
