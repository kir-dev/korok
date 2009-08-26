/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.services;

import javax.ejb.Local;

/**
 *
 * @author aldaris
 */
@Local
public interface TimerServiceLocal {

    void scheduleTimers();
}
