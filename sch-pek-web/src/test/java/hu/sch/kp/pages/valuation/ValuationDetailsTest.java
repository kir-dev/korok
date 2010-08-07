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
package hu.sch.kp.pages.valuation;

import java.util.List;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationStatistic;
import hu.sch.ejb.ValuationManagerBean;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.test.WebTest;
import javax.naming.NamingException;
import org.apache.wicket.PageParameters;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author  messo
 * @since   2.3.1
 */
public class ValuationDetailsTest extends WebTest {

    @Test
    public void render() {
        // kérjük le a statisztikákat
        ValuationManagerLocal valuationManager = null;
        try {
            valuationManager = lookupEJB(ValuationManagerBean.class);
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
        assertNotNull(valuationManager);

        List<ValuationStatistic> list = valuationManager.findValuationStatisticForSemester();
        if (!list.isEmpty()) {
            // válasszunk ki belőle az elsőt és nézzük meg, hogy lerenderelhető-e
            Valuation val = list.get(0).getValuation();
            tester.startPage(ValuationDetails.class, new PageParameters("id=" + val.getId()));
            tester.assertRenderedPage(ValuationDetails.class);
        }
    }
}
