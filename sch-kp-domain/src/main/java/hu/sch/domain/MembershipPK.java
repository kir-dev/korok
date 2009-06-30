/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author hege
 */
@Embeddable
public class MembershipPK implements Serializable {

    private Long userId;
    private Long groupId;

    public MembershipPK() {
    }

    public MembershipPK(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    @Column(name = "grp_id", nullable = false)
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Column(name = "usr_id", nullable = false)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}