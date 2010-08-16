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

import hu.sch.domain.interfaces.HasUserRelation;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Egy felhasználóhoz tartozó belépőigény
 * 
 * @author hege
 */
@Entity
@Table(name = "belepoigenyles")
public class EntrantRequest implements Serializable, HasUserRelation {

    protected Long id;
    protected Valuation valuation;
    protected String valuationText;
    protected EntrantType entrantType;
    protected User user;
    private Long userId;
    private Long valuationId;

    public EntrantRequest() {
        entrantType = EntrantType.KDO;
    }

    public EntrantRequest(User user, EntrantType entrantType) {
        this.entrantType = entrantType;
        setUser(user);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "belepo_tipus")
    public EntrantType getEntrantType() {
        return entrantType;
    }

    public void setEntrantType(EntrantType entrantType) {
        this.entrantType = entrantType;
    }

    @ManyToOne
    @JoinColumn(name = "ertekeles_id", nullable = false)
    public Valuation getValuation() {
        return valuation;
    }

    public void setValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    @Column(name = "ertekeles_id", updatable = false, insertable = false)
    public Long getValuationId() {
        return valuationId;
    }

    public void setValuationId(Long valuationId) {
        this.valuationId = valuationId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_id")
    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            userId = user.getId();
        }
    }

    /**
     * Felhasználó ID-ja akié a belépőkérelem. Azért kell nekünk egy ilyen külön,
     * mert ha nem akarjuk lekérni a User objektumot, akkor is kíváncsiak lehetünk
     * az ID-ra, amit fel tudunk használni.
     *
     * @return  felhasználó azonosítója, akié a belépőkérelem.
     */
    @Column(name = "usr_id", insertable = false, updatable = false)
    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "szoveges_ertekeles", columnDefinition = "text", length = 4096)
    public String getValuationText() {
        return valuationText;
    }

    public void setValuationText(String valuationText) {
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
    public EntrantRequest copy(Valuation v) {
        EntrantRequest er = new EntrantRequest();
        er.setValuation(v);
        er.setValuationText(valuationText);
        er.setEntrantType(entrantType);
        er.setUser(user);
        return er;
    }

    /**
     * Érvényes-e a belépőkérelem, tehát ha ÁB, vagy KB típusú, akkor legyen
     * hozzá értékelés
     * 
     * @return
     */
    @Transient
    public boolean isValid() {
        return !((entrantType == EntrantType.AB || entrantType == EntrantType.KB) && valuationText == null);
    }
}
