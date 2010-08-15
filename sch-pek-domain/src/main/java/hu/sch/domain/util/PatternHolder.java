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

package hu.sch.domain.util;

import java.util.regex.Pattern;

/**
 *
 * @author aldaris
 */
public class PatternHolder {

    public static final Pattern ENTITLEMENT_PATTERN;
    public static final Pattern GRADUATION_YEAR_PATTERN;
    public static final Pattern GROUP_NAME_OR_POSTTYPE_PATTERN;
    public static final Pattern IM_PATTERN;
    public static final Pattern NAME_PATTERN;
    public static final Pattern NEPTUN_PATTERN;
    public static final Pattern PHONE_NUMBER_PATTERN;
    public static final Pattern UID_PATTERN;
    public static final Pattern VIRID_PATTERN;

    static {
        //                                                         jog:csoportnév:csoportid
        ENTITLEMENT_PATTERN = Pattern.compile("^.*:entitlement:([^:]+):([^:]+):([0-9]+)$");
        GRADUATION_YEAR_PATTERN = Pattern.compile("[0-9]{8}/[1-2]");
        GROUP_NAME_OR_POSTTYPE_PATTERN = Pattern.compile("[^|:]*");
        IM_PATTERN = Pattern.compile("^([a-zA-Z]+):(.*)");
        NAME_PATTERN = Pattern.compile("^[A-ZÁÉÍÓÖŐÚÜŰ][a-záéíóöőúüű]+([ \\-][A-ZÁÉÍÓÖŐÚÜŰ][a-záéíóöőúüű]+)*$");
        NEPTUN_PATTERN = Pattern.compile("^.*:([A-Za-z0-9]{6,7})$");
        PHONE_NUMBER_PATTERN = Pattern.compile(".*\\d.*");
        UID_PATTERN = Pattern.compile("^[a-z0-9]*$");
        VIRID_PATTERN = Pattern.compile("^.*:([0-9]+)$");
    }

    private PatternHolder() {
    }
}

