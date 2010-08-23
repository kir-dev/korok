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
package hu.sch.services;

import hu.sch.domain.ValuationPeriod;
import hu.sch.domain.Semester;
import hu.sch.services.exceptions.NoSuchAttributeException;
import java.util.Date;
import javax.ejb.Local;

/**
 * Rendszerattribútumok menedzselése.
 * Ide tartozik a szemeszter és az értékelési időszak lekérdezése / beállítása is.
 * 
 * @author hege
 */
@Local
public interface SystemManagerLocal {

    /**
     * Rendszerattribútum értékét kérdezi le
     * 
     * @param attributeName
     * @return Az attribútumnévhez tartozó érték
     * @throws hu.sch.kp.services.exceptions.NoSuchAttributeException
     */
    String getAttributeValue(String attributeName) throws NoSuchAttributeException;

    /**
     * Beállítja egy rendszerattribútum értékét
     * 
     * @param attributeName
     * @param attributeValue
     */
    void setAttributeValue(String attributeName, String attributeValue);

    /**
     * Visszaadja az aktuális szemesztert
     * 
     * @return Az aktuális szemeszter objektuma
     * @throws NoSuchAttributeException
     */
    Semester getSzemeszter();

    /**
     * Visszaadja az aktuális értékelési időszakot
     * 
     * @return Aktuális értékelési időszak
     */
    ValuationPeriod getErtekelesIdoszak();

    /**
     * Beállítja az aktuális szemesztert
     * 
     * @param szemeszter
     */
    void setSzemeszter(Semester szemeszter);

    /**
     * Értékelési időszakot vált
     * 
     * @param idoszak
     */
    void setErtekelesIdoszak(ValuationPeriod idoszak);

    /**
     * Visszaadja az utolsó logküldési időt
     * @return Ekkor került kiküldésre utoljára log
     */
    Date getLastLogsDate();

    /**
     * Beállítja az utolsó logküldési időt
     */
    void setLastLogsDate();

    /**
     * Megmondja, hogy most gólyaidőszak van-e
     * @return <code>true</code> ha gólyaidőszak van, <code>false</code>, ha nem.
     */
    boolean getNewbieTime();

    /**
     * Beállítjaa gólyaidőszakot.
     * @param newbieTime gólyaidőszak van, vagy sem
     */
    void setNewbieTime(boolean newbieTime);
}
