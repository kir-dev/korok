package hu.sch.domain.user;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Size;

/**
 *
 * @author balo
 */
@Entity
@Table(name = "lostpw_tokens")
@NamedQueries({
    @NamedQuery(name = LostPasswordToken.getByToken,
            query = "SELECT t FROM LostPasswordToken t WHERE t.token = :token"
    ),
    @NamedQuery(name = LostPasswordToken.removeExpired,
            query = "DELETE FROM LostPasswordToken t "
            + "WHERE t.created < :time_in_past"
    )
})
public class LostPasswordToken implements Serializable {

    public static final String getByToken = "getByToken";
    public static final String removeExpired = "removeExpired";
    //
    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_id")
    private User subjectUser;
    //--------------------------------
    @Column(length = 64, unique = true)
    @Size(max = 64)
    private String token;
    //--------------------------------
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date created;

    public LostPasswordToken() {
    }

    public LostPasswordToken(final User subjectUser, final String token,
            final Date created) {

        this.subjectUser = subjectUser;
        this.token = token;
        this.created = new Date(created.getTime());
    }

    public User getSubjectUser() {
        return subjectUser;
    }

    public String getToken() {
        return token;
    }

    public Date getCreated() {
        return created;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.subjectUser);
        hash = 59 * hash + Objects.hashCode(this.token);
        hash = 59 * hash + Objects.hashCode(this.created);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LostPasswordToken other = (LostPasswordToken) obj;
        if (!Objects.equals(this.subjectUser, other.subjectUser)) {
            return false;
        }
        if (!Objects.equals(this.token, other.token)) {
            return false;
        }
        if (!Objects.equals(this.created, other.created)) {
            return false;
        }
        return true;
    }

}
