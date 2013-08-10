package hu.sch.services;

import hu.sch.domain.enums.ValuationPeriod;
import hu.sch.domain.Semester;
import hu.sch.services.exceptions.NoSuchAttributeException;
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
     * Visszaadja, hogy melyik log volt az amelyiket utoljára kiküldtük.
     *
     * @return Az utoljára kiküldött log ID-je
     */
    long getLastLogId();

    /**
     * Beállítja, hogy melyik log volt az utolsó amit kiküldtünk.
     */
    void setLastLogId(long id);

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
