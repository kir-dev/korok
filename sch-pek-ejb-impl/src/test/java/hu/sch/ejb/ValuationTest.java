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
package hu.sch.ejb;

import java.util.concurrent.atomic.AtomicBoolean;
import hu.sch.domain.ConsideredValuation;
import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationStatus;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.valuation.AlreadyModifiedException;
import hu.sch.services.exceptions.valuation.NoExplanationException;
import hu.sch.services.exceptions.valuation.NothingChangedException;
import javax.naming.NamingException;
import org.junit.BeforeClass;
import hu.sch.test.base.ContainerAwareAbstractTest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author messo
 */
public class ValuationTest extends ContainerAwareAbstractTest {

    private static ValuationManagerLocal valuationManager;
    private static UserManagerLocal userManager;
    private static SystemManagerLocal systemManager;
    /* Igazából csak egy szálból módosítjuk őket, de a Boolean az immutable */
    private AtomicBoolean b1 = new AtomicBoolean(false);
    private AtomicBoolean b2 = new AtomicBoolean(false);
    private Semester semester;
    private Group group;

    private class MagicThread extends Thread {

        private AtomicBoolean failed;

        public MagicThread(String name, AtomicBoolean failed) {
            super(name);
            this.failed = failed;
        }

        @Override
        public void run() {
            Valuation v = valuationManager.findLatestValuation(group, semester);
            ConsideredValuation cv = new ConsideredValuation(v, ValuationStatus.ELFOGADVA, ValuationStatus.ELFOGADVA, null);
            cv.setExplanation(getName());
            try {
                valuationManager.considerValuation(cv);
            } catch (NoExplanationException ex) {
                // ilyen nem lesz
            } catch (NothingChangedException ex) {
                // ilyen se, tutira mindkettő státusz NINCS, és most ELFOGADJUK mindegyiket
            } catch (AlreadyModifiedException ex) {
                // ez az érdekes
                failed.set(true);
            }
        }
    }

    public ValuationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        try {
            valuationManager = lookupEJB(ValuationManagerBean.class);
            userManager = lookupEJB(UserManagerBean.class);
            systemManager = lookupEJB(SystemManagerBean.class);
        } catch (NamingException ne) {
            ne.printStackTrace(System.err);
        }
    }

    @Test
    public void concurrentEditing() {
        semester = systemManager.getSzemeszter().getNext();
        group = userManager.findGroupById(Group.KIRDEV);
        // 1. létrehozunk egy értékelést
        Valuation v = new Valuation();
        v.setGroup(group);
        v.setSemester(semester);
        v.setValuationText("bla-bla");
        v.setPrinciple("bla-bla");
        valuationManager.createValuation(v);
        // 2. két külön szálon megpróbáljuk módosítani
        Thread t1 = new MagicThread("első", b1);
        Thread t2 = new MagicThread("második", b2);
        t1.start();
        t2.start();
        try {
            // megvárjuk míg mind a két szál befejezi a melót
            t1.join();
            t2.join();
        } catch (InterruptedException ex) {
        }
        // a létrehozott értékeléseket töröljük
        valuationManager.deleteValuations(group, semester);
        // 3. elvárás: csak az egyik dobjon kivételt
        assertTrue(b1.get() ^ b2.get());
    }
}
