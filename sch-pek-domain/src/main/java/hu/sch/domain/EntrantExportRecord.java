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
 * Segédosztály a színes belépők exportálásához
 * 
 * @author balo
 */
public final class EntrantExportRecord implements Serializable {

    private final User user;
    private Integer numOfEntrants = 0; // a megadott belépőkből mennyit kapott a user
    private String valuationTexts = ""; //indoklások
    public static final String DELIMITER = ",";

    public EntrantExportRecord(final User user) {
        this.user = user;
    }

    /**
     * Az export rekordhoz hozzáaadja az indoklásokat körnevekkel, megfelelő
     * sortörésekkel
     * 
     * @param groupName
     * @param valuationText 
     */
    public final void addRequest(EntrantRequest request) {

        //belépőt adó kör neve
        final String groupName = request.getValuation().getGroup().getName();

        StringBuilder sb = new StringBuilder(this.valuationTexts);

        if (!this.valuationTexts.isEmpty()) {
            sb.append(" || ");
        }

        sb.append('*').append(groupName).append("*: ");

        // indoklásokban lévő sortörések elrontják a csv-t, cseréljük le space-re
        sb.append(request.getValuationText().replace("\"", "\"\"").replaceAll("\\r|\\n", " "));

        this.valuationTexts = sb.toString();

        ++numOfEntrants;
    }

    public final User getUser() {
        return user;
    }

    public final Integer getNumOfEntrants() {
        return numOfEntrants;
    }

    public final String getValuationTexts() {
        return valuationTexts;
    }

    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(user.getName()).append(DELIMITER);
        sb.append(user.getNeptunCode()).append(DELIMITER);
        sb.append(user.getEmailAddress()).append(DELIMITER);

        if (user.getSviePrimaryMembership() != null) {
            sb.append(user.getSviePrimaryMembership().getGroup().getName());
        } else {
            sb.append("-");
        }

        sb.append(DELIMITER);
        sb.append(numOfEntrants).append(DELIMITER); //belepok szama
        sb.append("\"").append(valuationTexts).append("\""); //indoklasok
        
        return sb.toString();
    }
}
