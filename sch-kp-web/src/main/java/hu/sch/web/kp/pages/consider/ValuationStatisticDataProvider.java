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
package hu.sch.web.kp.pages.consider;

import hu.sch.domain.ValuationStatistic;
import hu.sch.domain.Semester;
import hu.sch.services.ValuationManagerLocal;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 *
 * @author hege
 */
public class ValuationStatisticDataProvider implements IDataProvider<ValuationStatistic> {

    @EJB(name = "ValuationManagerBean")
    private ValuationManagerLocal valuationManager;
    private List<ValuationStatistic> statList;

    public ValuationStatisticDataProvider(Semester semester) {
        InjectorHolder.getInjector().inject(this);
        statList = valuationManager.findValuationStatisticForSemester();
    }

    @Override
    public Iterator<ValuationStatistic> iterator(int first, int count) {
        return statList.subList(first, first + count).iterator();
    }

    @Override
    public int size() {
        return statList.size();
    }

    @Override
    public IModel<ValuationStatistic> model(ValuationStatistic object) {
        return new CompoundPropertyModel<ValuationStatistic>(object);
    }

    @Override
    public void detach() {
    }
}