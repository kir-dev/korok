/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.services.exceptions;

/**
 *
 * @author aldaris
 */
public class PersonNotFoundException extends Exception {

    public PersonNotFoundException() {
    }

    public PersonNotFoundException(String msg) {
        super(msg);
    }
}