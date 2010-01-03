/**
 * Copyright (c) 2009, Peter Major
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author hege
 */
@Entity
@Table(name = "ertekelesek")
@NamedQueries({
    @NamedQuery(name = "findValuationBySemesterAndGroup",
    query = "SELECT v FROM Valuation v WHERE v.semester=:semester "
    + "AND v.group=:group"),
    @NamedQuery(name = "findValuationByIdMessageJoined",
    query = "SELECT v FROM Valuation v LEFT JOIN FETCH v.messages "
    + "WHERE v.id=:id"),
    @NamedQuery(name = "findValuationByGroup",
    query = "SELECT v FROM Valuation v WHERE v.group=:group "
    + "ORDER BY v.semester DESC")
})
public class Valuation implements Serializable {

    public static final String findByIdMessageJoined = "findValuationByIdMessageJoined";
    public static final String findBySemesterAndGroup = "findValuationBySemesterAndGroup";
    public static final String findByGroup = "findValuationByGroup";
    protected Long id;
    protected Group group;
    protected User sender;
    protected User consideredBy;
    protected String valuationText;
    protected ValuationStatus pointStatus;
    protected ValuationStatus entrantStatus;
    protected Semester semester;
    protected Date sended;
    protected Date lastConsidered;
    protected Date lastModified;
    protected List<EntrantRequest> entrantRequests;
    protected List<PointRequest> pointRequests;
    protected List<ValuationMessage> messages;
    protected Float averagePoint;

    @ManyToOne
    @JoinColumn(name = "grp_id")
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @OneToMany(mappedBy = "valuation", fetch = FetchType.LAZY)
    public List<EntrantRequest> getEntrantRequests() {
        return entrantRequests;
    }

    public void setEntrantRequests(List<EntrantRequest> entrantRequests) {
        this.entrantRequests = entrantRequests;
    }

    @OneToMany(mappedBy = "valuation", fetch = FetchType.LAZY)
    public List<PointRequest> getPointRequests() {
        return pointRequests;
    }

    public void setPointRequests(List<PointRequest> pointRequests) {
        this.pointRequests = pointRequests;
    }

    @Column(name = "szoveges_ertekeles", columnDefinition = "text", length = 4096, nullable = false)
    @Basic(fetch = FetchType.LAZY)
    public String getValuationText() {
        return valuationText;
    }

    public void setValuationText(String valuationText) {
        this.valuationText = valuationText;
    }

    @PrePersist
    protected void setDefaultValues() {
        setSended(new Date());
        setLastModified(new Date());
        setPointStatus(ValuationStatus.NINCS);
        setEntrantStatus(ValuationStatus.NINCS);
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "elbiralo_usr_id")
    public User getConsideredBy() {
        return consideredBy;
    }

    public void setConsideredBy(User consideredBy) {
        this.consideredBy = consideredBy;
    }

    @OneToMany(mappedBy = "valuation", fetch = FetchType.LAZY)
    public List<ValuationMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ValuationMessage> messages) {
        this.messages = messages;
    }

    @Transient
    public Float getAveragePoint() {
        return averagePoint;
    }

    public void setAveragePoint(Float averagePoint) {
        this.averagePoint = averagePoint;
    }

    public void sortMessages() {
        if (this.getMessages() != null) {
            Collections.sort(this.getMessages(),
                    new Comparator<ValuationMessage>() {

                        public int compare(ValuationMessage o1, ValuationMessage o2) {
                            return o1.getDate().compareTo(o2.getDate()) * -1;
                        }
                    });
        }
    }
}
