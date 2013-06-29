package hu.sch.web.wicket.components.tables;

import hu.sch.web.wicket.components.customlinks.LinkPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Egy olyan {@link AbstractColumn} gyermekosztály, ami {@link LinkPanel}eket jelenít meg.
 *
 * @author  messo
 * @since   2.3.1
 */
public abstract class LinkColumn<T> extends AbstractColumn<T, String> {

    public LinkColumn(IModel<String> displayModel) {
        // link alapján ne akarjunk rendezni
        super(displayModel, null);
    }

    public LinkColumn(String header) {
        this(new Model<String>(header));
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        T obj = rowModel.getObject();
        if (isVisible(obj)) {
            LinkPanel panel = getLinkPanel(componentId, obj);
            panel.setColumn(this);
            item.add(panel);
        } else {
            item.add(new Label(componentId, ""));
        }
    }

    /**
     * Akkor hívhatjuk meg, amikor linkre kapcsolunk így átadhatjuk az eseményvezérlést
     * a táblázatnak, amennyiben ottani adatoktól is függ a cselekmény.
     */
    public void onClick(T obj) {

    }

    /**
     * Itt mondhatjuk meg, hogy látható legyen-e a link, amihez segítségül
     * hívhatjuk az objektumunkat.
     *
     * @param obj   ami segíthet a válaszolásban
     * @return  látható-e (alapértelemzetten igen)
     */
    protected boolean isVisible(T obj) {
        return true;
    }

    /**
     * Ezzel kérjük le a konkrét LinkPanel implementációt.
     * @param componentId
     * @param obj
     * @return  a megjelenítendő LinkPanel
     */
    protected abstract LinkPanel getLinkPanel(String componentId, T obj);
}
