/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.services;

import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.services.exceptions.InvalidPasswordException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author aldaris
 */
@Local
public interface LdapManagerLocal {

    public void changePassword(String uid, String oldPassword, String newPassword)
            throws InvalidPasswordException;

    void deletePersonByUid(String uid) throws PersonNotFoundException;

    Person getPersonByUid(String uid) throws PersonNotFoundException;

    Person getPersonByVirId(String virId) throws PersonNotFoundException;

    List<Person> getPersonsWhoHasBirthday(String searchDate);

    void update(Person p);

    List<Person> search(List<String> searchWords);

    List<Person> searchByAdmin(List<String> searchWords);

    List<Person> searchsomething(String searchDate);

    List<Person> getPersonByDn(List<String> dnList);

    void initialization();
}
