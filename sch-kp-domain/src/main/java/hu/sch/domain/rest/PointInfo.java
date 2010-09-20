package hu.sch.domain.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author aldaris
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PointInfo {

    private Long groupId;
    private Integer point;

    public PointInfo() {
    }

    public PointInfo(Long groupId, Integer point) {
        this.groupId = groupId;
        this.point = point;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }
}
