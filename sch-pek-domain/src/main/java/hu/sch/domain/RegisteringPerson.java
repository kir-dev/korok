package hu.sch.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 * We are using this class in the registration process. Store temporary values
 * and search for user in our fixed list.
 *
 * @author balo
 */
@Entity
@Table(name = "neptun_list")
@NamedQueries({
    @NamedQuery(name = RegisteringPerson.findRegPersonByNeptun,
            query = "SELECT p FROM RegisteringPerson p "
            + "WHERE UPPER(neptun)=:neptun AND szuldat = :dateOfBirth"),
    //-----------------------------------------------------------------------//
    @NamedQuery(name = RegisteringPerson.findRegPersonByEducationId,
            query = "SELECT p FROM RegisteringPerson p "
            + "WHERE education_id = :educationid AND szuldat = :dateOfBirth")
})
public class RegisteringPerson implements Serializable {

    public static final String findRegPersonByNeptun = "neptun_check";
    public static final String findRegPersonByEducationId = "newbie_om_check";
    //
    @Id
    @Column(length = 6)
    private String neptun;
    //
    @Column(name = "nev", nullable = false)
    private String name;
    //
    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "szuldat", nullable = false)
    private Date dateOfBirth;
    //
    @Column(name = "education_id", length = 11, nullable = true)
    private String educationId;
    //
    @Column(nullable = false)
    private boolean newbie;
    //
    //----- transient fields, they need in the reg. process to store until they have been saved
    //
    @Transient
    private String uid;
    //
    @Transient
    private String mail;
    //
    @Transient
    private String firstName;
    //
    @Transient
    private String lastName;

    public String getName() {
        return name;
    }

    public String getNeptun() {
        return neptun;
    }

    public void setNeptun(final String neptun) {
        this.neptun = neptun;
    }

    public String getEducationId() {
        return educationId;
    }

    public void setEducationId(final String educationId) {
        this.educationId = educationId;
    }

    public boolean isNewbie() {
        return newbie;
    }

    public void setNewbie(final boolean newbie) {
        this.newbie = newbie;
    }

    public Date getDateOfBirth() {
        if (dateOfBirth == null) {
            dateOfBirth = new Date();
        }
        return new Date(dateOfBirth.getTime());
    }

    public void setDateOfBirth(final Date dateOfBirth) {
        this.dateOfBirth = new Date(dateOfBirth.getTime());
    }

    public String getMail() {
        return mail;
    }

    public void setMail(final String mail) {
        this.mail = mail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "RegisteringPerson{" + "neptun=" + neptun + ", name=" + name + ", dateOfBirth=" + dateOfBirth + ", educationId=" + educationId + ", newbie=" + newbie + ", uid=" + uid + ", mail=" + mail + ", firstName=" + firstName + ", lastName=" + lastName + '}';
    }
}