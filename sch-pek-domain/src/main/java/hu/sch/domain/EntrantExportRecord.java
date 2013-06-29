package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;

/**
 * Dummy entitás, csak az export_entrant_requests tárolt eljárás eredményeihez használjuk.
 *
 * A tárolt eljárás és a hozzá tartozó típus definíciója a
 * resources/export_entrant_requests.sql-ben található
 * 
 * @author  messo
 * @since   2.4
 */
@Entity
@NamedNativeQuery(name = EntrantExportRecord.exportEntrantRequests,
query = "SELECT * FROM export_entrant_requests(:semester, :entrantType, :num)",
resultClass = EntrantExportRecord.class)
public class EntrantExportRecord implements Serializable {

    public static final String exportEntrantRequests = "exportEntrantRequests";
    public static final String DELIMITER = ",";
    @Id
    private Long uid;
    private String nev;
    private String neptun;
    private String email;
    @Column(name = "primary_group")
    private String primaryGroup;
    @Column(name = "entrant_num")
    private int entrantNum;
    private String indokok;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEntrantNum() {
        return entrantNum;
    }

    public void setEntrantNum(int entrantNum) {
        this.entrantNum = entrantNum;
    }

    public String getIndokok() {
        return indokok;
    }

    public void setIndokok(String indokok) {
        this.indokok = indokok;
    }

    public String getNeptun() {
        return neptun;
    }

    public void setNeptun(String neptun) {
        this.neptun = neptun;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getPrimaryGroup() {
        return primaryGroup;
    }

    public void setPrimaryGroup(String primaryGroup) {
        this.primaryGroup = primaryGroup;
    }

    public String toCVSformat() {
        StringBuilder sb = new StringBuilder();

        sb.append(nev).append(DELIMITER);
        sb.append(neptun).append(DELIMITER);
        sb.append(email).append(DELIMITER);
        sb.append(primaryGroup == null ? "-" : primaryGroup).append(DELIMITER);
        sb.append(entrantNum).append(DELIMITER); //belepok szama
        sb.append("\"").append(indokok.replace("\"", "\"\"").replaceAll("\\r|\\n", " ")).append("\""); //indoklasok

        return sb.toString();
    }
}
