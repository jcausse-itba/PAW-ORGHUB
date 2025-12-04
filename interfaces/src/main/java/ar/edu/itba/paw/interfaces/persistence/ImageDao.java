package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.image.Image;

import java.util.Optional;

public interface ImageDao {

    Optional<Image> getImage(int imageID);

    Image uploadImage(byte[] photoBlob);

    void deleteImage(int imageID);

    boolean canUserAccessImage(User user, Image image);

}

