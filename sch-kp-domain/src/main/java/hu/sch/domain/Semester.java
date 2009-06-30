/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class Semester implements Serializable {

    /**
     * Semester azonosító, ÉV1ÉV2FÉLÉV formában
     * Pl. 200720081 -> 2007/2008 tanév 1. (őszi) féléve
     */
    protected String id;

    public Semester() {
        id = "200020011";
    }

    public Semester(Integer firstYear, Integer secondYear, boolean isAutumn) {
        if (firstYear > 2030 || firstYear < 2000 ||
                secondYear > 2030 || secondYear < 2000) {
            throw new IllegalArgumentException("Az évnek 2000 és 2030 között kell lennie");
        }

        setId(firstYear.toString() + secondYear.toString() + (isAutumn ? "1" : "2"));
    }

    @Transient
    public Semester getPrevious()
    {
        Semester ret = new Semester();

        if (this.isAutumn())
        {
            ret.setFirstYear(this.getFirstYear()-1);
            ret.setSecondYear(this.getSecondYear()-1);
        }
        else
        {
            ret.setFirstYear(this.getFirstYear());
            ret.setSecondYear(this.getSecondYear());
        }

        // őszi félév előtt tavaszi és viszont
        ret.setAutumn(!this.isAutumn());

        return ret;
    }

    @Transient
    public Semester getNext()
    {
        Semester ret = new Semester();

        if (!this.isAutumn())
        {
            ret.setFirstYear(this.getFirstYear()+1);
            ret.setSecondYear(this.getSecondYear()+1);
        }
        else
        {
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
        if (firstYear > 2030 || firstYear < 2000) {
            throw new IllegalArgumentException("Az évnek 2000 és 2030 között kell lennie");
        }

        setId(firstYear.toString() + getSecondYear().toString() + (isAutumn() ? "1" : "2"));
    }

    @Transient
    public Integer getSecondYear() {
        return Integer.parseInt(id.substring(4, 8));
    }

    public void setSecondYear(Integer secondYear) {
        if (secondYear > 2030 || secondYear < 2000) {
            throw new IllegalArgumentException("Az évnek 2000 és 2030 között kell lennie");
        }

        setId(getFirstYear() + secondYear.toString() + (isAutumn() ? "1" : "2"));
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Semester other = (Semester) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    public boolean equals(Semester szemeszter)
    {
        if (szemeszter == null)
            return false;
        
        return this.getId().equals(szemeszter.getId());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

}
