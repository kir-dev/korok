/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain.logging;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author aldaris
 */
@Entity
@Table(name = "event")
@SequenceGenerator(name = "event_seq", sequenceName = "event_seq")
@NamedQueries({
    @NamedQuery(name = "getEventForEventType",
    query = "SELECT e FROM Event e WHERE e.eventType = :evt")
})
public class Event implements Serializable {

    private static final long serialVersionUID = 1l;
    public static final String getEventForEventType = "getEventForEventType";
    /*
    evt_id   | integer               | not null default nextval('event_seq'::regclass)
    evt_text | character varying(30) |
     */
    private Long id;
    private EventType eventType;

    @Id
    @GeneratedValue(generator = "event_seq")
    @Column(name = "evt_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "evt_text", length = 30)
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
