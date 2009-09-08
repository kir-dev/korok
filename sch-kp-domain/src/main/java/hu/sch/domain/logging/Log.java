/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain.logging;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author aldaris
 */
@Entity
@Table(name = "log")
@SequenceGenerator(name = "log_seq", sequenceName = "log_seq")
@NamedQueries({
    @NamedQuery(name = "getFreshEventsForEventTypeByGroup",
    query = "SELECT l FROM Log l " +
    "WHERE l.eventDate >= :date AND l.event.eventType = :evtType AND l.group = :group"),
    @NamedQuery(name = "getFreshEventsForSvie",
    query = "SELECT l FROM Log l " +
    "WHERE l.eventDate >= :date AND l.event.eventType = :evtType AND l.group IS NULL"),
    @NamedQuery(name = "getGroupsForFreshEntries",
    query = "SELECT DISTINCT l.group FROM Log l WHERE l.eventDate >=:date")
})
public class Log implements Serializable {

    private static final long serialVersionUID = 1l;
    public static final String getFreshEventsForEventTypeByGroup = "getFreshEventsForEventTypeByGroup";
    public static final String getGroupsForFreshEntries = "getGroupsForFreshEntries";
    public static final String getFreshEventsForSvie = "getFreshEventsForSvie";
    private Long id;
    private Group group;
    private User user;
    private Event event;
    private Date eventDate;

    @Id
    @GeneratedValue(generator = "log_seq")
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "grp_id", insertable = true, updatable = true)
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @ManyToOne
    @JoinColumn(name = "usr_id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "evt_id", insertable = true, updatable = true)
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "evt_date", columnDefinition = "timestamp")
    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date timestamp) {
        this.eventDate = timestamp;
    }
}
