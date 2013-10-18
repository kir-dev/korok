package hu.sch.domain.user;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@NamedQuery(name = LostPasswordToken.getByToken,
        query = "SELECT t FROM LostPasswordToken t WHERE t.token = :token")
public class LostPasswordToken implements Serializable {

    public static final String getByToken = "getByToken";
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
}
