package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

/**
 *
 * @author aldaris
 */
@Entity
@Table(name = "spot_images")
public class SpotImage implements Serializable {

    @Id
    @Column(name = "usr_neptun", nullable = false)
    private String neptunCode;
    /**
     * A SPOT által készített képet tartalmazza
     * @see <a href="http://stackoverflow.com/questions/3677380/proper-hibernate-annotation-for-byte">Hibernate és BLOB</a>
     */
    @Type(type = "org.hibernate.type.PrimitiveByteArrayBlobType")
    @Column(name = "image", nullable = false)
    private byte[] image;

    public String getNeptunCode() {
        return neptunCode;
    }

    public void setNeptunCode(String neptunCode) {
        this.neptunCode = neptunCode;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
