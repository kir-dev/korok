package hu.sch.web.wicket.components.customlinks;

import hu.sch.web.wicket.components.tables.LinkColumn;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Ez egy olyan {@link Panel}, amit arra használhatunk, hogy valamilyen típusú
 * objektumokat listázó táblázatnak egy oszlopát ({@link LinkColumn}) linkekkel
 * töltsünk fel. A konkrét implementációnak a markupja mondja meg, hogy milyen
 * legyen a pontos kinézet, viszont ez a szülőosztály lehetőséget ad arra, hogy a
 * {@link LinkColumn#onClick(Object)} metódust meghívjuk (praktikusan a
 * {@link Link#onClick} metódusban), ezáltal jelezve a táblázatnak, hogy
 * pontosan melyik sorban kapcsoltunk a linkre.
 *
 * @author  messo
 * @since   2.3.1
 * @see OldBoyLinkPanel
 * @see LinkColumn
 */
public abstract class LinkPanel<T> extends Panel {
    protected LinkColumn column;
    protected T obj;

    public LinkPanel(String id, T obj) {
        super(id);
        this.obj = obj;
    }

    public void setColumn(LinkColumn column) {
        this.column = column;
    }
}
