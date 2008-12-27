/*
 * Felhasznalo.java
 *
 * Created on April 23, 2007, 1:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Felhasználót reprezentáló entitás
 * @author hege
 */
@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(name = "findUserWithCsoporttagsagok",
    query =
    "SELECT f FROM Felhasznalo f JOIN FETCH f.csoporttagsagok WHERE f.id = :id")
})
public class Felhasznalo implements Serializable, Comparable<Felhasznalo> {

    private static final long serialVersionUID = 1L;
    public static final String findAll = "findAllUser";
    public static final String findByLoginName = "findUserByLoginName";
    /**
     * Felhasználó azonosítója
     */
    private Long id;
    /**
     * Bejelentkezési név
     */
    private String becenev;
    /**
     * név
     */
    private String keresztnev;
    private String vezeteknev;
    /**
     * E-mail cím
     */
    private String emailcim;
    /**
     * Neptun-kód
     */
    private String neptunkod;
    /**
     * Csoporttagságok - tagsági idővel kiegészítve
     */
    private List<Csoporttagsag> csoporttagsagok;
    /**
     * Tranziens csoporttagsagok
     */
    private List<Csoport> csoportok;

    /** Creates a new instance of Felhasznalo */
    public Felhasznalo() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "usr_id")
    public Long getId() {
        return id;
    }

    @Column(name = "usr_nickname", nullable = true, columnDefinition = "text")
    public String getBecenev() {
        return becenev;
    }

    public void setBecenev(String becenev) {
        this.becenev = becenev;
    }

    @Column(name = "usr_firstname", nullable = false, columnDefinition = "text")
    public String getKeresztnev() {
        return keresztnev;
    }

    public void setKeresztnev(String keresztnev) {
        this.keresztnev = keresztnev;
    }

    @Column(name = "usr_lastname", nullable = false, columnDefinition = "text")
    public String getVezeteknev() {
        return vezeteknev;
    }

    public void setVezeteknev(String vezeteknev) {
        this.vezeteknev = vezeteknev;
    }

    @Column(name = "usr_neptun", columnDefinition = "char(6)", length = 6, nullable =
    true, updatable = false)
    public String getNeptunkod() {
        return neptunkod;
    }

    public void setNeptunkod(String neptunkod) {
        this.neptunkod = neptunkod;
    }

    @Column(name = "usr_email", length = 64, nullable = false, columnDefinition =
    "varchar(64)")
    public String getEmailcim() {
        return emailcim;
    }

    public void setEmailcim(String emailcim) {
        this.emailcim = emailcim;
    }

    @Transient
    public List<Csoport> getCsoportok() {
        if (csoportok == null) {
            loadCsoportok();
        }
        return csoportok;
    }

    private void loadCsoportok() {
        csoportok = new ArrayList<Csoport>();
        for (Csoporttagsag m : csoporttagsagok) {
            csoportok.add(m.getCsoport());
        }
    }

    @OneToMany(mappedBy = "felhasznalo", fetch = FetchType.LAZY)
    public List<Csoporttagsag> getCsoporttagsagok() {
        return csoporttagsagok;
    }

    public List<Csoporttagsag> getCsoporttagsagokAholSzerepbenVagyok(TagsagTipus type) {
        List ret = new ArrayList<Csoporttagsag>();
        Iterator iterator = csoporttagsagok.iterator();
        while (iterator.hasNext()) {
            Csoporttagsag tagsag = (Csoporttagsag) iterator.next();
            if (TagsagTipus.hasJogCsoportban(tagsag, type)) {
                ret.add(tagsag);
            }
        }
        return ret;
    }

    public void setCsoporttagsagok(List<Csoporttagsag> csoporttagsagok) {
        this.csoporttagsagok = csoporttagsagok;
    }

    @Transient
    public String getNev() {
        return getVezeteknev() + " " + getKeresztnev();
    }

    public void sortCsoporttagsagok() {
        if (this.getCsoporttagsagok() != null) {
            Collections.sort(this.getCsoporttagsagok(),
                    new Comparator<Csoporttagsag>() {

                        public int compare(Csoporttagsag o1, Csoporttagsag o2) {
                            if (o1.getVeg() == null ^ o2.getVeg() == null) {
                                return o1.getVeg() == null ? -1 : 1;
                            }
                            return o1.getCsoport().compareTo(o2.getCsoport());
                        }
                    });
        }
    }

    @Override
    public String toString() {
        return "hu.uml13.domain.User " +
                "id=" + (getId() == null ? "NULL" : getId()) +
                ", nev=" + getNev() +
                ", becenev=" + getBecenev() +
                ", email=" + getEmailcim();
    }

    public int compareTo(Felhasznalo o) {
        return getNev().compareTo(o.getNev());
    }
}
