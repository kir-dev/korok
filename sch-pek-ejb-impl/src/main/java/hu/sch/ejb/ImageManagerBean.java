package hu.sch.ejb;

import hu.sch.domain.SpotImage;
import hu.sch.domain.profile.Person;
import hu.sch.domain.util.ImageResizer;
import hu.sch.services.ImageManagerLocal;
import java.io.File;
import java.io.FileInputStream;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author aldaris
 */
@Stateless
public class ImageManagerBean implements ImageManagerLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    @RolesAllowed("ADMIN")
    public void loadImages(String folder) throws Exception {
        File directory = new File(folder);
        File[] files = directory.listFiles();
        for (File file : files) {
            FileInputStream fin = new FileInputStream(file);
            byte[] fileContent = new byte[(int) file.length()];
            fin.read(fileContent);

            ImageResizer imageResizer = new ImageResizer(fileContent, Person.IMAGE_MAX_SIZE);
            if (imageResizer != null) {
                imageResizer.resizeImage();
                fileContent = imageResizer.getByteArray();
            }

            String fileName = file.getName();
            fileName = fileName.substring(0, 6);
            SpotImage spotImage = new SpotImage();
            spotImage.setNeptunCode(fileName);
            spotImage.setImage(fileContent);
            em.persist(spotImage);
            file.delete();
        }
    }
}
