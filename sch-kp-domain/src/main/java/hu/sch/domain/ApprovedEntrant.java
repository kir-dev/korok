/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

/**
 *
 * @author hege
 */
public class ApprovedEntrant {

    private String neptunCode;
    private EntrantType entrantType;

    public ApprovedEntrant() {
    }

    public ApprovedEntrant(String neptunCode, EntrantType entrantType) {
        this.neptunCode = neptunCode;
        this.entrantType = entrantType;
    }

    public EntrantType getEntrantType() {
        return entrantType;
    }

    public void setEntrantType(EntrantType entrantType) {
        this.entrantType = entrantType;
    }

    public String getNeptunCode() {
        return neptunCode;
    }

    public void setNeptunCode(String neptunCode) {
        this.neptunCode = neptunCode;
    }
}
