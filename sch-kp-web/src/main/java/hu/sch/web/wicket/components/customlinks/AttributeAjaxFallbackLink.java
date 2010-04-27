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

import hu.sch.domain.profile.Person;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public class AttributeAjaxFallbackLink extends AjaxFallbackLink {

    private String privateAttr;
    private boolean isPrivateAttr;
    private Image img;
    private static Person person;

    public AttributeAjaxFallbackLink(String id) {
        super(id);
    }

    public AttributeAjaxFallbackLink(String linkId, String imgId, final String privateAttr) {
        super(linkId);
        this.privateAttr = privateAttr;
        isPrivateAttr = person.isPrivateAttribute(privateAttr);

        img = new Image(imgId);
        img.setOutputMarkupId(true);
        setImgModel();

        this.add(img);
    }

    public void setImgModel() {
        if (isPrivateAttr) {
            img.setDefaultModel(new Model(new ResourceReference(AttributeAjaxFallbackLink.class, "resources/private.gif")));
        } else {
            img.setDefaultModel(new Model(new ResourceReference(AttributeAjaxFallbackLink.class, "resources/public.gif")));
        }
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        person.inversePrivateAttribute(privateAttr);
        isPrivateAttr = !isPrivateAttr;

        setImgModel();
        target.addComponent(img);
    }

    public static void setPerson(Person person2) {
        person = person2;
    }
}
