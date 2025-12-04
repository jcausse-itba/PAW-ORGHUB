package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.interfaces.exceptions.ImageConversionException;
import ar.edu.itba.paw.model.image.Image;

import java.util.Optional;

public interface ImageService {

    Optional<Image> getImage(int imageID);

    Optional<Image> getImage(String imagePath) throws ImageConversionException;

    Image uploadImage(byte[] photoBlob);

    void deleteImage(int imageID);

}

