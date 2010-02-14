/**
 * Copyright (c) 2009, Peter Major
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
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Felhasználót reprezentáló entitás
 * @author hege
 */
@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(name = "findAllUser",
    query = "SELECT u FROM User u"),
    @NamedQuery(name = "findUserWithMemberships",
    query = "SELECT u FROM User u LEFT OUTER JOIN FETCH u.memberships WHERE u.id = :id"),
    @NamedQuery(name = "findUserByNeptunCode",
    query = "SELECT u FROM User u WHERE u.neptunCode = :neptun"),
    @NamedQuery(name = "findUser", query = "SELECT u FROM User u WHERE upper(u.neptunCode) = upper(:neptunkod) OR "
            + "upper(u.emailAddress) = upper(:emailcim)"),
    @NamedQuery(name = User.getAllValuatedSemesterForUser, query = "SELECT DISTINCT pr.valuation.semester FROM PointRequest pr WHERE pr.user = :user ORDER BY pr.valuation.semester DESC")
})
@SequenceGenerator(name = "users_seq", sequenceName = "users_usr_id_seq")
public class User implements Serializable, Comparable<User> {

    private static final long serialVersionUID = 1L;
    public static final String findAll = "findAllUser";
    public static final String findByLoginName = "findUserByLoginName";
    public static final String findWithMemberships = "findUserWithMemberships";
    public static final String findUserByNeptunCode = "findUserByNeptunCode";
    public static final String findUser = "findUser";
    public static final String getAllValuatedSemesterForUser = "getAllValuatedSemesterForUser";
    /*
    usr_id                 | integer                | not null default nextval('users_usr_id_seq'::regclass)
    usr_email              | character varying(64)  | 
    usr_neptun             | character(6)           | 
    usr_firstname          | text                   | not null
    usr_lastname           | text                   | not null
    usr_nickname           | text                   | 
    usr_svie_state         | character varying(255) | not null default 'NEMTAG'::character varying
    usr_svie_member_type   | character varying(255) | not null default 'NEMTAG'::character varying
    usr_svie_primary_group | integer                |
    usr_delegated          | boolean                |
     */
    /**
     * Felhasználó azonosítója
     */
    private Long id;
    /**
     * E-mail cím
     */
    private String emailAddress;
    /**
     * Neptun-kód
     */
    private String neptunCode;
    /**
     * Keresztnév
     */
    private String firstName;
    /**
     * Vezetéknév
     */
    private String lastName;
    /**
     * Bejelentkezési név
     */
    private String nickName;
    /**
     * SVIE tagság státusza
     */
    private SvieStatus svieStatus;
    /**
     * SVIE tagság típusa
     */
    private SvieMembershipType svieMembershipType;
    /**
     * SVIE elsődleges kör
     * Rendes tagsága kell legyen a körben
     */
    private Membership sviePrimaryMemberhip;
    /**
     * Csoporttagságok - tagsági idővel kiegészítve
     */
    private List<Membership> memberships;
    /**
     * Az illető küldött-e az elsődleges körében
     */
    private boolean delegated;
    /**
     * Tranziens csoporttagsagok
     */
    private List<Group> groups;

    /**
     * A felhasználó egyedi azonosítóját visszaadó függvény
     * @return A user virId-je
     */
    @Id
    @GeneratedValue(generator = "users_seq")
    @Column(name = "usr_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "usr_email", length = 64, columnDefinition = "varchar(64)")
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Column(name = "usr_neptun", columnDefinition = "char(6)", length = 6, nullable =
    true, updatable = false)
    public String getNeptunCode() {
        return neptunCode;
    }

    public void setNeptunCode(String neptunCode) {
        this.neptunCode = neptunCode;
    }

    @Column(name = "usr_firstname", nullable = false, columnDefinition = "text")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "usr_lastname", nullable = false, columnDefinition = "text")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "usr_nickname", nullable = true, columnDefinition = "text")
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "usr_svie_state", nullable = false)
    public SvieStatus getSvieStatus() {
        return svieStatus;
    }

    public void setSvieStatus(SvieStatus svieStatus) {
        this.svieStatus = svieStatus;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "usr_svie_member_type", nullable = false)
    public SvieMembershipType getSvieMembershipType() {
        return svieMembershipType;
    }

    public void setSvieMembershipType(SvieMembershipType svieMembershipType) {
        this.svieMembershipType = svieMembershipType;
    }

    @Column(name = "usr_delegated", nullable = false, columnDefinition = "boolean default false")
    public boolean getDelegated() {
        return delegated;
    }

    public void setDelegated(boolean newValue) {
        this.delegated = newValue;
    }

    @ManyToOne
    @JoinColumn(name = "usr_svie_primary_membership", insertable = true, updatable = true)
    public Membership getSviePrimaryMembership() {
        return sviePrimaryMemberhip;
    }

    public void setSviePrimaryMembership(Membership sviePrimaryMembership) {
        this.sviePrimaryMemberhip = sviePrimaryMembership;
    }

    @Transient
    public List<Group> getGroups() {
        if (groups == null) {
            loadGroups();
        }
        return groups;
    }

    private void loadGroups() {
        groups = new ArrayList<Group>();
        if (memberships != null) {
            for (Membership m : memberships) {
                groups.add(m.getGroup());
            }
        }
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public List<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<Membership> memberships) {
        this.memberships = memberships;
    }

    @Transient
    public String getName() {
        return getLastName() + " " + getFirstName();
    }

    public void sortMemberships() {
        if (this.getMemberships() != null) {
            Collections.sort(this.getMemberships(),
                    new Comparator<Membership>() {

                        public int compare(Membership o1, Membership o2) {
                            if (o1.getEnd() == null ^ o2.getEnd() == null) {
                                return o1.getEnd() == null ? -1 : 1;
                            }
                            return o1.getGroup().compareTo(o2.getGroup());
                        }
                    });
        }
    }

    @Override
    public String toString() {
        return "hu.uml13.domain.User "
                + "id=" + (getId() == null ? "NULL" : getId())
                + ", nev=" + getName()
                + ", becenev=" + getNickName()
                + ", email=" + getEmailAddress();
    }

    @Override
    public int compareTo(User o) {
        Collator huCollator = Collator.getInstance(new Locale("hu"));
        return huCollator.compare(getName(), o.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 61 * hash + (this.neptunCode != null ? this.neptunCode.hashCode() : 0);
        hash = 61 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 61 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        return hash;
    }
}
