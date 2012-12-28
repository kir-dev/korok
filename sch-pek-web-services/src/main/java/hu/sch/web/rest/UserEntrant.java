package hu.sch.web.rest;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author balo
 */
@XmlRootElement(name="entrant")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserEntrant implements Serializable {

    Long groupId = 106L;
    String groupName = "Kir-Dev";
    String entrant = "AB";
}
