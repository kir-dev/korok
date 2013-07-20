package hu.sch.domain.user;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author hege
 * @author tomi
 */
@Entity
@Table(name = "im_accounts")
@SequenceGenerator(name = "im_accounts_seq", sequenceName = "im_accounts_seq")
public class IMAccount implements Serializable {

    @Id
    @GeneratedValue(generator = "im_accounts_seq")
    @Column(name = "id")
    private Long id;
    //--------------------------------
    @Column(name = "protocol")
    @Enumerated(EnumType.STRING)
    private IMProtocol protocol;
    //--------------------------------
    @Column(name = "screen_name")
    private String screenName;

    public IMAccount() {
    }

    public IMAccount(IMProtocol protocol, String presenceID) {
        this.protocol = protocol;
        this.screenName = presenceID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IMProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(IMProtocol protocol) {
        this.protocol = protocol;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    /**
     * Az IMAccount string formában
     * @return protocol:screenName
     */
    @Override
    public String toString() {
        return protocol.toString() + ":" + screenName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof IMAccount) {
            IMAccount o2 = (IMAccount) obj;
            return o2.getId().equals(this.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.id.hashCode();
        return hash;
    }
}
