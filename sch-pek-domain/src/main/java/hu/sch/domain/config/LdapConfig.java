/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.domain.config;

/**
 *
 * @author tomi
 */
public class LdapConfig {

    private String host;
    private int port;
    private String password;
    private String user;

    public LdapConfig(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.user = user;
    }

    /**
     * Get the value of host
     *
     * @return the value of host
     */
    public String getHost() {
        return host;
    }

    /**
     * Get the value of port
     *
     * @return the value of port
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the value of password
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the value of user (baseDN in LDAP terminology)
     * @return the value of user
     */
    public String getUser() {
        return user;
    }
}
