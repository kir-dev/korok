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

package hu.sch.ejb.util;

import org.springframework.ldap.filter.CompareFilter;

/**
 * Internacionalizált filter
 *
 * Ez egy OpenDS specifikus egyedi filter, amivel lehetőség van ékezetektől
 * független keresések végrehajtására. Pl. ha 'Gábor'-ra keresel, akkor
 * megtalálod a 'Gabor'-t is és fordítva is. Egyelőre csak a 'sub'-os filter
 * van támogatva, de később könnyűszerrel ki lehet majd egészíteni, hogy mondjuk
 * a sima 'eq'-t is támogassa, stb.
 *
 * <strong>FIGYELEM!</strong>
 * <p>
 * Sajnos ez a filter egy kicsit buta az OpenDS-ben, pl, ha nem teszed be a '*'-okat
 * sub üzemmódban, akkor nem fog matchelni a teljes tartalomra...
 * </p>
 *
 * @see <a href="https://www.opends.org/wiki/page/SearchingUsingInternationalCollationRules">OpenDS dokumentáció</a>
 * @author aldaris
 */
public class I18nFilter extends CompareFilter {

    private static final String HUNGARIAN_COLLATOR = ":1.3.6.1.4.1.42.2.27.9.4.88.1.6";
    private static final String I18N_COMPARE = ":=";

    public I18nFilter(String attribute, String value) {
        super(attribute + HUNGARIAN_COLLATOR, value);
    }

    @Override
    protected String getCompareString() {
        return I18N_COMPARE;
    }
}
