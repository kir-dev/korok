package hu.sch.domain.rest;

import hu.sch.domain.EntrantType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author balo
 */
@XmlRootElement(name = "entrants")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApprovedEntrant implements Serializable {

    private Long groupId;
    private String groupName;
    private EntrantType entrantType;

    public ApprovedEntrant() {
    }

    public ApprovedEntrant(final Long groupId, final String groupName,
            final EntrantType entrantType) {

        this.groupId = groupId;
        this.groupName = groupName;
        this.entrantType = entrantType;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(final Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public EntrantType getEntrantType() {
        return entrantType;
    }

    public void setEntrantType(final EntrantType entrantType) {
        this.entrantType = entrantType;
    }
}
