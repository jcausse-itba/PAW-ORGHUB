package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.image.Image;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class ImageJpaDao implements ImageDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Image> getImage(final int imageID) {
        return Optional.ofNullable(em.find(Image.class, imageID));
    }

    @Override
    public Image uploadImage(final byte[] photoBlob) {
        final Image image = new Image(null, photoBlob);
        em.persist(image);
        return image;
    }

    @Override
    public void deleteImage(final int imageID) {
        final Image image = em.find(Image.class, imageID);
        if (image != null) {
            em.remove(image);
        }
    }

    @Override
    public boolean canUserAccessImage(final User user, final Image image) {
        if (user == null || image == null || user.getInstitution() == null) {
            return false;
        }
        final Institution inst = user.getInstitution();
        final String hql = "SELECT COUNT(e) FROM Event e " +
                "WHERE e.image = :image AND e.organization.institution = :inst";
        return em.createQuery(hql, Long.class)
                .setParameter("image", image)
                .setParameter("inst", inst)
                .getSingleResult() > 0;
    }
}

