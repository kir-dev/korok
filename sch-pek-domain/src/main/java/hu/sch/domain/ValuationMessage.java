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
import org.hibernate.annotations.Index;

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
    //
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //----------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    //----------------------------------------------------
    @Index(name = "fki_group_id")
    @Column(name = "group_id", insertable = false, updatable = false)
    private Long groupId;
    //----------------------------------------------------
    @Embedded
    private Semester semester;
    //----------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "felado_usr_id")
    @Index(name = "fki_felado_usr_id")
    private User sender;
    //----------------------------------------------------
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "feladas_ido", columnDefinition = "timestamp without time zone")
    private Date date;
    //----------------------------------------------------
    @Column(name = "uzenet", columnDefinition = "text", length = 4096)
    private String message;
    //----------------------------------------------------
    @Column(name = "from_system", columnDefinition = "boolean default false")
    private boolean fromSystem;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
        if (group != null) {
            groupId = group.getId();
        }
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String uzenet) {
        this.message = uzenet;
    }

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
        return "Feladó: " + sender.getFullName() + "\n"
                + "Dátum: " + dateFormat.format(new Date()) + "\n"
                + "Üzenet szövege:\n\n" + message;
    }
}
