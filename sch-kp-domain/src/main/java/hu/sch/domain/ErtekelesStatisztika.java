/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.domain;

/**
 *
 * @author hege
 */
public class ErtekelesStatisztika {
    protected Ertekeles ertekeles;
    protected Double atlagPont;
    protected Long kiosztottKDO;
    protected Long kiosztottKB;
    protected Long kiosztottAB;

    public ErtekelesStatisztika(Ertekeles ertekeles, Double atlagPont, Long kiosztottKDO, Long kiosztottKB, Long kiosztottAB) {
        this.ertekeles = ertekeles;
        this.atlagPont = atlagPont;
        this.kiosztottKDO = kiosztottKDO;
        this.kiosztottKB = kiosztottKB;
        this.kiosztottAB = kiosztottAB;
    }
    
    public Double getAtlagPont() {
        return atlagPont;
    }

    public void setAtlagPont(Double atlagPont) {
        this.atlagPont = atlagPont;
    }

    public Ertekeles getErtekeles() {
        return ertekeles;
    }

    public void setErtekeles(Ertekeles ertekeles) {
        this.ertekeles = ertekeles;
    }

    public Long getKiosztottKDO() {
        return kiosztottKDO;
    }

    public void setKiosztottKDO(Long kiosztottKDO) {
        this.kiosztottKDO = kiosztottKDO;
    }

    public Long getKiosztottAB() {
        return kiosztottAB;
    }

    public void setKiosztottAB(Long kiosztottAB) {
        this.kiosztottAB = kiosztottAB;
    }

    public Long getKiosztottKB() {
        return kiosztottKB;
    }

    public void setKiosztottKB(Long kiosztottKB) {
        this.kiosztottKB = kiosztottKB;
    }
    
    
}
