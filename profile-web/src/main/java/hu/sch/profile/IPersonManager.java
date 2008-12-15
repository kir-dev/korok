/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.profile;

import java.util.List;

/**
 *
 * @author konvergal
 */
public interface IPersonManager {

    Person getPersonByUid(String uid) throws PersonNotFoundException;
    
    Person getPersonByVirId(String virId) throws PersonNotFoundException;

    void update(Person p);

    List<Person> getPersonsWhoHasBirthday(String searchDate);
    
    List<Person> search(List<String> searchWords);
    
    List<Person> searchByAdmin(List<String> searchWords);
    
    List<Person> getPersonByDn(List<String> dnList);
    
    void changePassword(String uid, String oldPassword, String newPassword) 
            throws InvalidPasswordException;
}
