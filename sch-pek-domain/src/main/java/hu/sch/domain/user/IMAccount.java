package hu.sch.domain.user;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author hege
 * @author tomi
 */
@Entity
@Table(name = "im_accounts")
@SequenceGenerator(name = "im_accounts_seq", sequenceName = "im_accounts_seq", allocationSize = 1)
public class IMAccount implements Serializable {

    @Id
    @GeneratedValue(generator = "im_accounts_seq")
    @Column(name = "id")
    private Long id;
    //--------------------------------
    @Column(name = "protocol")
    @Enumerated(EnumType.STRING)
    @NotNull
    private IMProtocol protocol;
    //--------------------------------
    @Column(name = "account_name")
    @NotNull
    private String accountName;

    public IMAccount() {
    }

    public IMAccount(IMProtocol protocol, String accountName) {
        this.protocol = protocol;
        this.accountName = accountName;
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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String screenName) {
        this.accountName = screenName;
    }

    /**
     * Az IMAccount string formában
     * @return protocol:screenName
     */
    @Override
    public String toString() {
        return protocol.toString() + ":" + accountName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IMAccount other = (IMAccount) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (this.protocol != other.getProtocol()) {
            return false;
        }
        if (!Objects.equals(this.accountName, other.getProtocol())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.protocol);
        hash = 53 * hash + Objects.hashCode(this.accountName);
        return hash;
    }
}
