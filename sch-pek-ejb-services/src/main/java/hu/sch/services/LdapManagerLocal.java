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

    Person getPersonByNeptun(String neptun) throws PersonNotFoundException;

    List<Person> getPersonsWhoHasBirthday(String searchDate);

    void update(Person p);

    List<Person> search(String keyWord);

    List<Person> searchInactives();

    List<Person> searchMyUid(String mail);

    void registerPerson(Person p, String password);

    void registerNewbie(Person p, String password);
}
