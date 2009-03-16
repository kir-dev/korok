/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.services;

import hu.sch.domain.ldap.LdapPerson;
import hu.sch.kp.services.exceptions.PersonNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author aldaris
 */
@Local
public interface LdapPersonManagerLocal {

    LdapPerson getPersonByUid(String uid) throws PersonNotFoundException;

    LdapPerson getPersonByVirId(String virId) throws PersonNotFoundException;

    void update(LdapPerson p);

    List<LdapPerson> search(List<String> searchWords);

    List<LdapPerson> searchByAdmin(List<String> searchWords);

    List<LdapPerson> searchsomething(String searchDate);

    List<LdapPerson> getPersonByDn(List<String> dnList);

    void initialization();
}