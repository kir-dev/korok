package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
     */
    @NotNull
    @Column(name = "image_path", nullable = false)
    private String imagePath;

    public String getNeptunCode() {
        return neptunCode;
    }

    public void setNeptunCode(String neptunCode) {
        this.neptunCode = neptunCode;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
