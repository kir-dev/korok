/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.domain;

/**
 *
 * @author hege
 */
public class ElbiraltErtekeles {
    private Ertekeles ertekeles;
    private ErtekelesStatusz pontStatusz;
    private ErtekelesStatusz belepoStatusz;
    private String indoklas;

    public ElbiraltErtekeles(Ertekeles ertekeles, ErtekelesStatusz pontStatusz, ErtekelesStatusz belepoStatusz) {
        setErtekeles(ertekeles);
        setPontStatusz(pontStatusz);
        setBelepoStatusz(belepoStatusz);
    }
    
    public ElbiraltErtekeles(Ertekeles ertekeles) {
        setErtekeles(ertekeles);
        setPontStatusz(ertekeles.getPontStatusz());
        setBelepoStatusz(ertekeles.getBelepoStatusz());
    }

    public ErtekelesStatusz getBelepoStatusz() {
        return belepoStatusz;
    }

    public void setBelepoStatusz(ErtekelesStatusz belepoStatusz) {
        System.out.println("setting belepostatusz... "+belepoStatusz);
        
        this.belepoStatusz = belepoStatusz;
    }

    public Ertekeles getErtekeles() {
        return ertekeles;
    }

    public void setErtekeles(Ertekeles ertekeles) {
        this.ertekeles = ertekeles;
    }

    public String getIndoklas() {
        return indoklas;
    }

    public void setIndoklas(String indoklas) {
        this.indoklas = indoklas;
    }

    public ErtekelesStatusz getPontStatusz() {
        return pontStatusz;
    }

    public void setPontStatusz(ErtekelesStatusz pontStatusz) {
        System.out.println("setting pontstatusz... "+pontStatusz);
        this.pontStatusz = pontStatusz;
    }

    @Override
    public String toString() {
        return "Elbírált értékelés a "+getErtekeles().getId()+
                " értékeléshez. Pontigény: "+getPontStatusz()+
                "/"+getErtekeles().getPontStatusz() +
                ", Belépőigény: "+getBelepoStatusz()+
                "/"+getErtekeles().getBelepoStatusz();
    }
    
    public boolean isElbiralt() {
        return !(getPontStatusz().equals(ErtekelesStatusz.ELBIRALATLAN) ||
                getBelepoStatusz().equals(ErtekelesStatusz.ELBIRALATLAN));
    }
    
    public boolean isElbiralatlan() {
        return getPontStatusz().equals(ErtekelesStatusz.ELBIRALATLAN) &&
                getBelepoStatusz().equals(ErtekelesStatusz.ELBIRALATLAN);
    }
    
    public boolean isInkonzisztens() {
        return !isElbiralatlan() && !isElbiralt();
    }
}
