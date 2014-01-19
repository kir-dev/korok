package hu.sch.domain;

import hu.sch.domain.enums.ValuationStatus;
import hu.sch.domain.user.User;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 *
 * @author hege
 */
@Entity
@Table(name = "ertekelesek")
@NamedQueries({
    @NamedQuery(name = Valuation.findForDetails,
    query = "SELECT v FROM Valuation v "
    + "LEFT JOIN FETCH v.sender "
    + "LEFT JOIN FETCH v.consideredBy "
    + "WHERE v.id = :id"),
    @NamedQuery(name = Valuation.findBySemesterAndGroup,
    query = "SELECT v FROM Valuation v WHERE v.semester=:semester "
    + "AND v.group=:group AND v.nextVersion IS NULL"),
    @NamedQuery(name = Valuation.findIdBySemesterAndGroup,
    query = "SELECT v.id FROM Valuation v WHERE v.semester=:semester "
    + "AND v.group=:group AND v.nextVersion IS NULL"),
    @NamedQuery(name = Valuation.findByGroup,
    query = "SELECT v FROM Valuation v "
    + "WHERE v.group=:group AND v.nextVersion IS NULL "
    + "ORDER BY v.semester DESC"),
    @NamedQuery(name = Valuation.delete, query = "DELETE FROM Valuation v WHERE v.group = :group AND v.semester = :semester"),
    @NamedQuery(name = Valuation.findStatisticBySemester, query = Valuation.statQuery + "WHERE v.semester = :semester AND v.nextVersion IS NULL"),
    @NamedQuery(name = Valuation.findStatisticBySemesterAndGroup, query = Valuation.statQuery + "WHERE v.semester = :semester AND v.group = :group ORDER BY v.id DESC"),
    @NamedQuery(name = Valuation.findStatisticByValuation, query = Valuation.statQuery + "WHERE v.id = :valuationId"),
    @NamedQuery(name = Valuation.findStatisticByValuations, query = Valuation.statQuery + "WHERE v.id in (:ids)"),
    @NamedQuery(name = Valuation.findStatisticBySemesterAndStatuses, query = Valuation.statQuery
    + "WHERE v.semester=:semester AND (v.pointStatus=:pointStatus OR v.entrantStatus=:entrantStatus)")
})
public class Valuation implements Serializable {

    public static final String findForDetails = "findForDetails";
    public static final String findBySemesterAndGroup = "findValuationBySemesterAndGroup";
    public static final String findIdBySemesterAndGroup = "findValuationIdBySemesterAndGroup";
    public static final String findByGroup = "findValuationByGroup";
    public static final String findStatisticByValuation = "findStatisticForValuation";
    public static final String findStatisticByValuations = "findStatisticForValuations";
    public static final String findStatisticBySemester = "findStatisticBySemester";
    public static final String findStatisticBySemesterAndGroup = "findStatisticBySemesterAndGroup";
    public static final String findStatisticBySemesterAndStatuses = "findStatisticBySemesterAndStatuses";
    public static final String delete = "delete";
    protected static final String statQuery = "SELECT new hu.sch.domain.ValuationStatistic(v, "
            + "(SELECT avg(p.point) FROM PointRequest p WHERE p.valuation = v AND p.point > 0) as averagePoint, "
            + "(SELECT sum(p.point) FROM PointRequest p WHERE p.valuation = v AND p.point > 0) as summaPoint, "
            + "(SELECT count(*) as numKDO FROM EntrantRequest as e WHERE e.valuation = v AND e.entrantType=\'KDO\') as givenKDO, "
            + "(SELECT count(*) as numKB FROM EntrantRequest as e WHERE e.valuation = v AND e.entrantType=\'KB\') as givenKB, "
            + "(SELECT count(*) as numAB FROM EntrantRequest as e WHERE e.valuation = v AND e.entrantType=\'AB\') as givenAB"
            + ") FROM Valuation v ";
    //----------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    //----------------------------------------------------
    @OneToOne
    @JoinColumn(name = "next_version")
    protected Valuation nextVersion;
    //----------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "grp_id")
    protected Group group;
    //----------------------------------------------------
    @Column(name = "grp_id", insertable = false, updatable = false)
    protected Long groupId;
    //----------------------------------------------------
    @ManyToOne(optional = true)
    @JoinColumn(name = "felado_usr_id")
    protected User sender;
    //----------------------------------------------------
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "feladas")
    protected Date sended;
    //----------------------------------------------------
    @Column(name = "szoveges_ertekeles", columnDefinition = "text", length = 4096, nullable = false)
    @Basic(fetch = FetchType.LAZY)
    protected String valuationText;
    //----------------------------------------------------
    @Column(name = "pontozasi_elvek", columnDefinition = "text", nullable = false)
    protected String principle;
    //----------------------------------------------------
    @Enumerated(EnumType.STRING)
    @Column(name = "pontigeny_statusz")
    protected ValuationStatus pointStatus;
    //----------------------------------------------------
    @Enumerated(EnumType.STRING)
    @Column(name = "belepoigeny_statusz")
    protected ValuationStatus entrantStatus;
    //----------------------------------------------------
    @Embedded
    protected Semester semester;
    //----------------------------------------------------
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "utolso_modositas")
    protected Date lastModified;
    //----------------------------------------------------
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "elbiralo_usr_id")
    protected User consideredBy;
    //----------------------------------------------------
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "utolso_elbiralas")
    protected Date lastConsidered;
    //----------------------------------------------------
    @Column(name = "explanation", columnDefinition = "text", nullable = false)
    protected String explanation;
    //----------------------------------------------------
    @OneToMany(mappedBy = "valuation", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    protected Set<EntrantRequest> entrantRequestsAsSet;
    //----------------------------------------------------
    @OneToMany(mappedBy = "valuation", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    protected Set<PointRequest> pointRequestsAsSet;
    //----------------------------------------------------
    @Transient
    protected Float averagePoint;
    //----------------------------------------------------
    @Version
    @Column(name = "optlock")
    protected int optLock;
    //----------------------------------------------------
    @Column(name = "is_considered")
    protected boolean considered;

    @PrePersist
    protected void setDefaultValues() {
        if (sended == null) {
            setSended(new Date());
        }
        if (lastModified == null) {
            setLastModified(new Date());
        }
        if (pointStatus == null) {
            setPointStatus(ValuationStatus.NINCS);
        }
        if (entrantStatus == null) {
            setEntrantStatus(ValuationStatus.NINCS);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Valuation getNextVersion() {
        return nextVersion;
    }

    public void setNextVersion(Valuation nextVersion) {
        this.nextVersion = nextVersion;
    }

    public int getOptLock() {
        return optLock;
    }

    public void setOptLock(int optLock) {
        this.optLock = optLock;
    }

    public boolean isConsidered() {
        return considered;
    }

    public void setConsidered(boolean considered) {
        this.considered = considered;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Date getLastConsidered() {
        return lastConsidered;
    }

    public void setLastConsidered(Date lastConsidered) {
        this.lastConsidered = lastConsidered;
    }

    public Date getSended() {
        return sended;
    }

    public void setSended(Date sended) {
        this.sended = sended;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public ValuationStatus getEntrantStatus() {
        return entrantStatus;
    }

    public void setEntrantStatus(ValuationStatus entrantStatus) {
        this.entrantStatus = entrantStatus;
    }

    public ValuationStatus getPointStatus() {
        return pointStatus;
    }

    public void setPointStatus(ValuationStatus pointStatus) {
        this.pointStatus = pointStatus;
    }

    public Set<EntrantRequest> getEntrantRequestsAsSet() {
        return entrantRequestsAsSet;
    }

    public void setEntrantRequestsAsSet(Set<EntrantRequest> entrantRequestsAsSet) {
        this.entrantRequestsAsSet = entrantRequestsAsSet;
    }

    public Set<PointRequest> getPointRequestsAsSet() {
        return pointRequestsAsSet;
    }

    public void setPointRequestsAsSet(Set<PointRequest> pointRequestsAsSet) {
        this.pointRequestsAsSet = pointRequestsAsSet;
    }

    public String getValuationText() {
        return valuationText;
    }

    public void setValuationText(String valuationText) {
        this.valuationText = valuationText;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getPrinciple() {
        return principle;
    }

    public void setPrinciple(String principle) {
        this.principle = principle;
    }

    public User getConsideredBy() {
        return consideredBy;
    }

    public void setConsideredBy(User consideredBy) {
        this.consideredBy = consideredBy;
    }

    public Float getAveragePoint() {
        return averagePoint;
    }

    public void setAveragePoint(Float averagePoint) {
        this.averagePoint = averagePoint;
    }

    public boolean entrantsAreAccepted() {
        return entrantStatus == ValuationStatus.ELFOGADVA;
    }

    public boolean pointsAreAccepted() {
        return pointStatus == ValuationStatus.ELFOGADVA;
    }

    @Override
    public String toString() {
        return new StringBuilder("Valuation: ").append(semester).append(" ").append(group.getName()).toString();
    }

    /**
     * Ennek a segítségével csinálunk új verziót, ha módosítottak rajta
     * @return
     */
    public Valuation copy() {
        Valuation v = new Valuation();
        v.setGroup(group);
        v.setSender(sender);
        v.setValuationText(valuationText);
        v.setPrinciple(principle);
        v.setPointStatus(pointStatus);
        v.setEntrantStatus(entrantStatus);
        v.setSemester(semester);
        v.setSended(sended); // vajon ez maradjon a régi, vagy legyen friss?
        v.setLastModified(lastModified);
        v.setConsidered(false);
        return v;
    }

    /**
     * Lemásoljuk az értékelés pontkérelmeit a paraméterben megadott értékeléshez,
     * amiket hozzá is rendelünk.
     *
     * @param v az értékelés, amelyiknek beállítjuk a másolatokat
     */
    public void copyPointRequests(Valuation v) {
        Set<PointRequest> pReqs = getPointRequestsAsSet();
        Set<PointRequest> result = new HashSet<>(pReqs.size());

        for (PointRequest pr : pReqs) {
            result.add(pr.copy(v));
        }

        v.setPointRequestsAsSet(result);
    }

    /**
     * Lemásoljuk az értékelés belépőkérelmeit a paraméterben megadott értékeléshez,
     * amiket hozzá is rendelünk.
     *
     * @param v az értékelés, amelyiknek beállítjuk a másolatokat
     */
    public void copyEntrantRequests(Valuation newVersion) {
        Set<EntrantRequest> eReqs = getEntrantRequestsAsSet();
        Set<EntrantRequest> result = new HashSet<>(eReqs.size());

        for (EntrantRequest er : eReqs) {
            result.add(er.copy(newVersion));
        }

        newVersion.setEntrantRequestsAsSet(result);
    }

    public boolean isObsolete() {
        return nextVersion != null;
    }
}
