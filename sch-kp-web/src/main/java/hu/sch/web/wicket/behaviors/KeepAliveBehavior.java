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
package hu.sch.web.wicket.behaviors;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.time.Duration;

/**
 * Egy olyan Behavior, ami lehetővé teszi, hogy időnként megpingeljük a servert, így
 * a kritikus helyeken, ahol erre szükség van, nem járhat le a session.
 *
 * @see http://chillenious.wordpress.com/2007/06/19/how-to-create-a-text-area-with-a-heart-beat-with-wicket/
 */
public class KeepAliveBehavior extends AbstractAjaxTimerBehavior {

    /**
     * A default, hogy 15 percenként pingelünk.
     */
    public KeepAliveBehavior() {
        // 30 perces a session, így félidőnél hosszabbítsunk
        this(Duration.minutes(15));
    }

    /**
     * Finomhangolhatjuk, hogy milyen időközönként jelezzen a szervernek.
     *
     * @param duration milyen időközönként pingeljen
     */
    public KeepAliveBehavior(Duration duration) {
        super(duration);
    }

    @Override
    protected void onTimer(AjaxRequestTarget target) {
        // prevent wicket changing focus
        target.focusComponent(null);
    }
}
