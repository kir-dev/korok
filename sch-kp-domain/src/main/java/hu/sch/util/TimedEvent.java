/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.util;

/**
 *
 * @author aldaris
 */
public enum TimedEvent {

    DAILY_EVENT(24 * 3600 * 1000);
    final long interval;

    private TimedEvent(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }
}
