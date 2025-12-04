package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.image.Image;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class ImageJpaDaoTest {

    @Autowired
    private ImageJpaDao imageJpaDao;

    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Rollback
    @Test
    public void testGetImageNonExistent() {
        final Optional<Image> image = imageJpaDao.getImage(999);
        Assert.assertFalse(image.isPresent());
    }

    @Rollback
    @Test
    public void testUploadImage() {
        final byte[] photoBlob = new byte[]{1, 2, 3, 4, 5};
        final Image image = imageJpaDao.uploadImage(photoBlob);

        Assert.assertNotNull(image);
        Assert.assertNotNull(image.getImageID());
        Assert.assertArrayEquals(photoBlob, image.getImageBlob());
    }

    @Rollback
    @Test
    public void testGetImage() {
        final byte[] photoBlob = new byte[]{1, 2, 3, 4, 5};
        final Image uploadedImage = imageJpaDao.uploadImage(photoBlob);
        em.flush();
        em.clear();

        final Optional<Image> retrievedImage = imageJpaDao.getImage(uploadedImage.getImageID());
        Assert.assertTrue(retrievedImage.isPresent());
        Assert.assertArrayEquals(photoBlob, retrievedImage.get().getImageBlob());
    }

    @Rollback
    @Test
    public void testDeleteImage() {
        final byte[] photoBlob = new byte[]{1, 2, 3, 4, 5};
        final Image image = imageJpaDao.uploadImage(photoBlob);
        em.flush();
        final int imageID = image.getImageID();

        imageJpaDao.deleteImage(imageID);
        em.flush();
        em.clear();

        final Optional<Image> deletedImage = imageJpaDao.getImage(imageID);
        Assert.assertFalse(deletedImage.isPresent());
    }

    @Rollback
    @Test
    public void testDeleteImageNonExistent() {
        // Should not throw exception
        imageJpaDao.deleteImage(999);
    }
}

