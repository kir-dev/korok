package hu.sch.domain;

import hu.sch.domain.user.User;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Az egyes értékelésekhez tartozó üzeneteket reprezentáló entitás
 * 
 * @see Valuation
 * @author hege
 */
@Entity
@Table(name = "ertekeles_uzenet")
@NamedQueries({
    @NamedQuery(name = ValuationMessage.listMessages,
    query = "SELECT m FROM ValuationMessage m WHERE m.semester=:semester AND m.group=:group ORDER BY m.id DESC")
})
public class ValuationMessage implements Serializable {

    public static final String listMessages = "listMessages";
    protected Long id;
    protected Group group;
    protected Long groupId;
    protected Semester semester;
    protected User sender;
    protected Date date;
    protected String message;
    protected boolean fromSystem;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "feladas_ido")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToOne
    @JoinColumn(name = "group_id")
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
        if (group != null) {
            groupId = group.getId();
        }
    }

    @Column(name = "group_id", insertable = false, updatable = false)
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Embedded
    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    @ManyToOne
    @JoinColumn(name = "felado_usr_id")
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "uzenet", columnDefinition = "text", length = 4096)
    public String getMessage() {
        return message;
    }

    public void setMessage(String uzenet) {
        this.message = uzenet;
    }

    @Column(name = "from_system")
    public boolean isFromSystem() {
        return fromSystem;
    }

    public void setFromSystem(boolean fromSystem) {
        this.fromSystem = fromSystem;
    }

    @PrePersist
    public void setDefaultValues() {
        setDate(new Date());
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MMMMM dd. HH:mm:ss", new Locale("hu"));
        return "Feladó: " + sender.getName() + "\n"
                + "Dátum: " + dateFormat.format(new Date()) + "\n"
                + "Üzenet szövege:\n\n" + message;
    }
}
