/**
 * Copyright (c) 2008-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hu.sch.domain;

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
    protected Long id;
    protected Valuation nextVersion;
    protected Group group;
    protected Long groupId;
    protected User sender;
    protected Date sended;
    protected String valuationText;
    protected String principle;
    protected ValuationStatus pointStatus;
    protected ValuationStatus entrantStatus;
    protected Semester semester;
    protected Date lastModified;
    protected User consideredBy;
    protected Date lastConsidered;
    protected String explanation;
    protected List<EntrantRequest> entrantRequests;
    protected List<PointRequest> pointRequests;
    protected Set<EntrantRequest> entrantRequestsAsSet;
    protected Set<PointRequest> pointRequestsAsSet;
    protected Float averagePoint;
    protected int optLock;
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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "grp_id")
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Column(name = "grp_id", insertable = false, updatable = false)
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @OneToOne
    @JoinColumn(name = "next_version")
    public Valuation getNextVersion() {
        return nextVersion;
    }

    public void setNextVersion(Valuation nextVersion) {
        this.nextVersion = nextVersion;
    }

    @Version
    @Column(name = "optlock")
    public int getOptLock() {
        return optLock;
    }

    public void setOptLock(int optLock) {
        this.optLock = optLock;
    }

    @Column(name = "is_considered")
    public boolean isConsidered() {
        return considered;
    }

    public void setConsidered(boolean considered) {
        this.considered = considered;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "felado_usr_id")
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @Embedded
    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "utolso_elbiralas")
    public Date getLastConsidered() {
        return lastConsidered;
    }

    public void setLastConsidered(Date lastConsidered) {
        this.lastConsidered = lastConsidered;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "feladas")
    public Date getSended() {
        return sended;
    }

    public void setSended(Date sended) {
        this.sended = sended;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "utolso_modositas")
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "belepoigeny_statusz")
    public ValuationStatus getEntrantStatus() {
        return entrantStatus;
    }

    public void setEntrantStatus(ValuationStatus entrantStatus) {
        this.entrantStatus = entrantStatus;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "pontigeny_statusz")
    public ValuationStatus getPointStatus() {
        return pointStatus;
    }

    public void setPointStatus(ValuationStatus pointStatus) {
        this.pointStatus = pointStatus;
    }

    @Deprecated
    @OneToMany(mappedBy = "valuation", fetch = FetchType.LAZY)
    public List<EntrantRequest> getEntrantRequests() {
        return entrantRequests;
    }

    public void setEntrantRequests(List<EntrantRequest> entrantRequests) {
        this.entrantRequests = entrantRequests;
    }

    @OneToMany(mappedBy = "valuation", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    public Set<EntrantRequest> getEntrantRequestsAsSet() {
        return entrantRequestsAsSet;
    }

    public void setEntrantRequestsAsSet(Set<EntrantRequest> entrantRequestsAsSet) {
        this.entrantRequestsAsSet = entrantRequestsAsSet;
    }

    @Deprecated
    @OneToMany(mappedBy = "valuation", fetch = FetchType.LAZY)
    public List<PointRequest> getPointRequests() {
        return pointRequests;
    }

    public void setPointRequests(List<PointRequest> pointRequests) {
        this.pointRequests = pointRequests;
    }

    @OneToMany(mappedBy = "valuation", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    public Set<PointRequest> getPointRequestsAsSet() {
        return pointRequestsAsSet;
    }

    public void setPointRequestsAsSet(Set<PointRequest> pointRequestsAsSet) {
        this.pointRequestsAsSet = pointRequestsAsSet;
    }

    @Column(name = "szoveges_ertekeles", columnDefinition = "text", length = 4096, nullable = false)
    @Basic(fetch = FetchType.LAZY)
    public String getValuationText() {
        return valuationText;
    }

    public void setValuationText(String valuationText) {
        this.valuationText = valuationText;
    }

    @Column(name = "explanation", columnDefinition = "text", nullable = false)
    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @Column(name = "pontozasi_elvek", columnDefinition = "text", nullable = false)
    public String getPrinciple() {
        return principle;
    }

    public void setPrinciple(String principle) {
        this.principle = principle;
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "elbiralo_usr_id")
    public User getConsideredBy() {
        return consideredBy;
    }

    public void setConsideredBy(User consideredBy) {
        this.consideredBy = consideredBy;
    }

    @Transient
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
        Set<PointRequest> result = new HashSet<PointRequest>(pReqs.size());

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
        Set<EntrantRequest> result = new HashSet<EntrantRequest>(eReqs.size());

        for (EntrantRequest er : eReqs) {
            result.add(er.copy(newVersion));
        }

        newVersion.setEntrantRequestsAsSet(result);
    }

    @Transient
    public boolean isObsolete() {
        return nextVersion != null;
    }
}
