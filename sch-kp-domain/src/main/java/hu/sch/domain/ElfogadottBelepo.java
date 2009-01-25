/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

/**
 *
 * @author hege
 */
public class ElfogadottBelepo {

    private String neptunKod;
    private BelepoTipus belepoTipus;

    public ElfogadottBelepo() {
    }

    public ElfogadottBelepo(String neptunKod, BelepoTipus belepoTipus) {
        this.neptunKod = neptunKod;
        this.belepoTipus = belepoTipus;
    }

    public BelepoTipus getBelepoTipus() {
        return belepoTipus;
    }

    public void setBelepoTipus(BelepoTipus belepoTipus) {
        this.belepoTipus = belepoTipus;
    }

    public String getNeptunKod() {
        return neptunKod;
    }

    public void setNeptunKod(String neptunKod) {
        this.neptunKod = neptunKod;
    }
}
