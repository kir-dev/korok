package hu.sch.domain.logging;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    @NamedQuery(name = Log.getFreshEventsForEventTypeByGroup,
            query = "SELECT l FROM Log l "
            + "WHERE l.id > :lastUsedLogId AND l.id <= :lastLogId AND l.event = :evtType AND l.group = :group"),
    @NamedQuery(name = Log.getFreshEventsForSvie,
            query = "SELECT l FROM Log l "
            + "WHERE l.id > :lastUsedLogId AND l.id <= :lastLogId AND l.event = :evtType AND l.group IS NULL"),
    @NamedQuery(name = Log.getGroupsForFreshEntries,
            query = "SELECT DISTINCT l.group FROM Log l WHERE l.id > :lastUsedLogId AND l.id <= :lastLogId"),
    @NamedQuery(name = Log.getLastId,
            query = "SELECT l.id FROM Log l ORDER BY l.id DESC"),
    @NamedQuery(name = Log.getLastLogIdByDate,
            query = "SELECT l.id FROM Log l WHERE l.eventDate <= :date ORDER BY l.id DESC")
})
public class Log implements Serializable {

    private static final long serialVersionUID = 1l;
    public static final String getFreshEventsForEventTypeByGroup = "getFreshEventsForEventTypeByGroup";
    public static final String getGroupsForFreshEntries = "getGroupsForFreshEntries";
    public static final String getFreshEventsForSvie = "getFreshEventsForSvie";
    public static final String getLastId = "getLastId";
    public static final String getLastLogIdByDate = "getLastLogIdByDate";
    //
    @Id
    @GeneratedValue(generator = "log_seq")
    @Column(name = "id")
    private Long id;
    //
    @ManyToOne
    @JoinColumn(name = "grp_id", insertable = true, updatable = false)
    private Group group;
    //
    @ManyToOne
    @JoinColumn(name = "usr_id", insertable = true, updatable = false)
    private User user;
    //
    @Enumerated(EnumType.STRING)
    @Column(name = "event", length = 30)
    private EventType event;
    //
    @Temporal(TemporalType.DATE)
    @Column(name = "evt_date", columnDefinition = "timestamp")
    private Date eventDate;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(final Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public EventType getEvent() {
        return event;
    }

    public void setEvent(final EventType event) {
        this.event = event;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(final Date timestamp) {
        this.eventDate = timestamp;
    }
}
