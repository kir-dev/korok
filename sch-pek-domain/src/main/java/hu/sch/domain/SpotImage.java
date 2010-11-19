package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

/**
 *
 * @author aldaris
 */
@Entity
@Table(name = "spot_images")
@NamedQueries({
    @NamedQuery(name = SpotImage.findByNeptun, query = "SELECT si FROM SpotImage si WHERE si.neptunCode = :neptunCode"),
    @NamedQuery(name = SpotImage.deleteByNeptun, query = "DELETE FROM SpotImage si WHERE si.neptunCode = :neptunCode")
})
public class SpotImage implements Serializable {

    public static final String findByNeptun = "findSpotImageByNeptun";
    public static final String deleteByNeptun = "deleteSpotImageByNeptun";
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
