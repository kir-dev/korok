/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    Semester getSzemeszter();

    /**
     * Visszaadja az aktuális értékelési időszakot
     * 
     * @return
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
     * @return
     */
    Date getLastLogsDate();

    /**
     * Beállítja az utolsó logküldési időt
     */
    void setLastLogsDate();
}
