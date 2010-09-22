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

package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 *
 * @author hege
 */
@Embeddable
public class Semester implements Serializable, Comparable<Semester> {

    /**
     * Semester azonosító, ÉV1ÉV2FÉLÉV formában
     * Pl. 200720081 -> 2007/2008 tanév 1. (őszi) féléve
     */
    protected String id;

    public Semester() {
        id = "200020011";
    }

    public Semester(String id) {
        this.id = id;
    }

    public Semester(Integer firstYear, Integer secondYear, boolean isAutumn) {
        if (firstYear > 2030 || firstYear <= 1970
                || secondYear > 2030 || secondYear <= 1970) {
            throw new IllegalArgumentException("Az évnek 1970 és 2030 között kell lennie");
        }

        setId(firstYear.toString() + secondYear.toString() + (isAutumn ? "1" : "2"));
    }

    @Transient
    public Semester getPrevious() {
        Semester ret = new Semester();

        if (this.isAutumn()) {
            ret.setFirstYear(this.getFirstYear() - 1);
            ret.setSecondYear(this.getSecondYear() - 1);
        } else {
            ret.setFirstYear(this.getFirstYear());
            ret.setSecondYear(this.getSecondYear());
        }

        // őszi félév előtt tavaszi és viszont
        ret.setAutumn(!this.isAutumn());

        return ret;
    }

    @Transient
    public Semester getNext() {
        Semester ret = new Semester();

        if (!this.isAutumn()) {
            ret.setFirstYear(this.getFirstYear() + 1);
            ret.setSecondYear(this.getSecondYear() + 1);
        } else {
            ret.setFirstYear(this.getFirstYear());
            ret.setSecondYear(this.getSecondYear());
        }

        // őszi félév előtt tavaszi és viszont
        ret.setAutumn(!this.isAutumn());

        return ret;
    }

    @Column(name = "semester", length = 9, columnDefinition = "character(9)", nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Transient
    public boolean isAutumn() {
        Long sem = Long.parseLong(id);
        return Long.lowestOneBit(sem) == 1;
    }

    public void setAutumn(boolean isAutumn) {
        setId(getFirstYear().toString() + getSecondYear().toString() + (isAutumn ? "1" : "2"));
    }

    @Transient
    public Integer getFirstYear() {
        return Integer.parseInt(id.substring(0, 4));
    }

    public void setFirstYear(Integer firstYear) {
        if (firstYear > 2030 || firstYear <= 1970) {
            throw new IllegalArgumentException("Az évnek 1970 és 2030 között kell lennie");
        }

        setId(firstYear.toString() + getSecondYear().toString() + (isAutumn() ? "1" : "2"));
    }

    @Transient
    public Integer getSecondYear() {
        return Integer.parseInt(id.substring(4, 8));
    }

    public void setSecondYear(Integer secondYear) {
        if (secondYear > 2030 || secondYear <= 1970) {
            throw new IllegalArgumentException("Az évnek 1970 és 2030 között kell lennie");
        }

        setId(getFirstYear() + secondYear.toString() + (isAutumn() ? "1" : "2"));
    }

    @Transient
    public boolean isValid() {
        int firstYear = 0;
        int secondYear = 0;
        int autumn = 0;

        if (id.length() != 9) {
            return false;
        }

        try {
            firstYear = getFirstYear();
            secondYear = getSecondYear();
            autumn = Integer.parseInt(id.substring(8));
        } catch (NumberFormatException ex) {
            return false;
        }

        if (firstYear > 2030 || secondYear <= 1970 || secondYear > 2030
                || secondYear <= 1970 || (autumn != 1 && autumn != 2)) {
            return false;
        }

        return true;
    }

    /**
     * @return Emészthető formátum: pl 2007/2008 tavasz
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(getFirstYear().toString());
        b.append("/");
        b.append(getSecondYear().toString());
        b.append(" ");
        b.append(isAutumn() ? "ősz" : "tavasz");

        return b.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Semester other = (Semester) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.getId())) {
            return false;
        }
        return true;
    }

    public boolean equals(Semester szemeszter) {
        if (szemeszter == null) {
            return false;
        }

        return this.getId().equals(szemeszter.getId());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Semester o) {
        return id.compareTo(o.id);
    }
}
