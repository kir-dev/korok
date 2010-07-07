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

    public LinkPanel(String id) {
        super(id);
    }

    public void setColumn(LinkColumn column) {
        this.column = column;
    }

    public void setObject(T obj) {
        this.obj = obj;
    }
}
