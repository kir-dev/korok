package hu.sch.services;

import hu.sch.domain.enums.ValuationPeriod;
import hu.sch.domain.Semester;
import hu.sch.services.exceptions.NoSuchAttributeException;
import java.util.Map;
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
     * EnumMap keys for sending exception report email to the dev list
     */
    public enum EXC_REPORT_KEYS {

        PAGE_NAME, PAGE_PATH, PAGE_PARAMS, REMOTE_USER, REMOTE_ADDRESS, EMAIL,
        VIRID, EXCEPTION
    };

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

    /**
     * Sends an email to the dev mailing list with the given details.
     *
     * @see EXC_REPORT_KEYS
     *
     * @param params
     */
    void sendExceptionReportMail(final Map<SystemManagerLocal.EXC_REPORT_KEYS, String> params);
}
