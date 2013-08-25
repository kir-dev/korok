package hu.sch.domain;

import hu.sch.domain.config.Configuration;
import java.io.Serializable;
import java.nio.file.Paths;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author aldaris
 */
@Entity
@Table(name = "spot_images")
@NamedQueries({
    @NamedQuery(name = SpotImage.findByNeptun, query = "SELECT si FROM SpotImage si WHERE UPPER(si.neptunCode) = UPPER(:neptunCode)")
})
public class SpotImage implements Serializable {

    public static final String findByNeptun = "findSpotImageByNeptun";
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
        if (neptunCode != null) {
            this.neptunCode = neptunCode.toUpperCase();
        }
        else {
            this.neptunCode = null;
        }
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageFullPath() {
        return Paths.get(Configuration.getImageUploadConfig().getBasePath(),
                getImagePath()).toString();
    }
}
