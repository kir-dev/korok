package hu.sch.services.exceptions;

/**
 *
 * @author aldaris
 */
public class PersonNotFoundException extends Exception {

    private String id;

    public PersonNotFoundException() {
    }

    public PersonNotFoundException(String id) {
        super("Could not find person with id: " + id);
        this.id = id;
    }
}
