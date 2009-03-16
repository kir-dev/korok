/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain.ldap;

/**
 *
 * @author aldaris
 */
public class LdapMembership {

    private String groupName;
    private String status;

    public LdapMembership(String groupName, String status) {
        this.groupName = groupName;
        this.status = status;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
