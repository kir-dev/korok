package hu.sch.web.wicket.components;

import hu.sch.web.wicket.components.tables.MembershipTable;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * {@link MembershipTable} objektumhoz egy {@link Panel}, amivel egy div-be foglalhatjuk.
 * Azért kell ez, mert van ahol speckó borítást akarunk adni a MembershipTable-nek, ott
 * külön csinálunk ehhez panelt, de akkor a sima megjelenítéshez, kell egy általános megoldás,
 * erre született ez a {@link Panel}.
 *
 * @author  messo
 * @since   2.3.1
 */
public class MembershipTablePanel extends Panel {

    /**
     * Létrehozunk egy panelt, amihez egyből hozzáadjuk a táblázatot.
     *
     * @param id    panel azonosítója
     * @param tb    táblázat objektum, aminek az ID-jának "table"-nek kell lennie!
     */
    public MembershipTablePanel(String id, MembershipTable tb) {
        super(id);
        add(tb.getDataTable());
    }
}
