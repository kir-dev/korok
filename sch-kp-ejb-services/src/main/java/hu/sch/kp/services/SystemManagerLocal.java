/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.kp.services;

import hu.sch.domain.ErtekelesIdoszak;
import hu.sch.domain.Szemeszter;
import hu.sch.kp.services.exceptions.NoSuchAttributeException;
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
     * @return
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
     * @return
     * @throws NoSuchAttributeException
     */
    Szemeszter getSzemeszter();

    /**
     * Visszaadja az aktuális értékelési időszakot
     * 
     * @return
     */
    ErtekelesIdoszak getErtekelesIdoszak();

    /**
     * Beállítja az aktuális szemesztert
     * 
     * @param szemeszter
     */
    void setSzemeszter(Szemeszter szemeszter);

    /**
     * Értékelési időszakot vált
     * 
     * @param idoszak
     */
    void setErtekelesIdoszak(ErtekelesIdoszak idoszak);
}
