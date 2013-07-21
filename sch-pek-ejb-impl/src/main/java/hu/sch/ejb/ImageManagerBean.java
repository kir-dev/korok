package hu.sch.ejb;

import hu.sch.domain.SpotImage;
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

    private static final int IMAGE_MAX_SIZE = 320;

    @PersistenceContext
    private EntityManager em;

    @Override
    @RolesAllowed("ADMIN")
    public void loadImages(String folder) throws Exception {
        File directory = new File(folder);
        File[] files = directory.listFiles();
        for (File file : files) {
            FileInputStream fin = null;
            byte[] fileContent;
            try {
                fin = new FileInputStream(file);
                fileContent = new byte[(int) file.length()];
                fin.read(fileContent);
            } finally {
                if (fin != null) {
                    fin.close();
                }
            }

            ImageResizer imageResizer = new ImageResizer(fileContent, IMAGE_MAX_SIZE);
            imageResizer.resizeImage();
            fileContent = imageResizer.getByteArray();

            String fileName = file.getName();
            fileName = fileName.substring(0, 6);
            SpotImage spotImage = new SpotImage();
            spotImage.setNeptunCode(fileName);
            // TODO: save files to filesystem
            //spotImage.setImage(fileContent);
            em.persist(spotImage);
            file.delete();
        }
    }
}
