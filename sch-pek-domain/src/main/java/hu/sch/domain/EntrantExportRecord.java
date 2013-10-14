package hu.sch.domain;

import java.io.Serializable;

/**
 * Az export_entrant_requests tárolt eljárás eredményeihez használjuk.
 *
 * A tárolt eljárás és a hozzá tartozó típus definíciója a
 * resources/export_entrant_requests.sql-ben található
 *
 * @author  messo
 * @author  balo
 * @since   2.4
 */
public class EntrantExportRecord implements Serializable {
    //
    public static final String DELIMITER = ",";
    //
    private Long uid;
    private String nev;
    private String neptun;
    private String email;
    private String primaryGroup;
    private int entrantNum;
    private String indokok;

    //fixme #67
    //temporary factory method; it can be removed after we moved stored procedures into the code (#67)
    public static EntrantExportRecord createFrom(final Object[] record) {
        final EntrantExportRecord result = new EntrantExportRecord();
        result.setUid(Long.valueOf(String.valueOf(record[0])));
        result.setNev((String) record[1]);
        result.setNeptun((String) record[2]);
        result.setEmail((String) record[3]);
        result.setPrimaryGroup((String) record[4]);
        result.setEntrantNum(Integer.valueOf(String.valueOf(record[5])));
        result.setIndokok((String) record[6]);

        return result;
    }

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
