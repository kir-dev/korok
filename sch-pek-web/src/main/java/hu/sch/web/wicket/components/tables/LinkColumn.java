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
