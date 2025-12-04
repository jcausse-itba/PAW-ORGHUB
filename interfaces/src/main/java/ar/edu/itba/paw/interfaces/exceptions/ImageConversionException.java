package ar.edu.itba.paw.interfaces.exceptions;

public class ImageConversionException extends RuntimeException {
    public ImageConversionException() {
        super("Image conversion failed.");
    }

    public ImageConversionException(String message) {
        super(message);
    }
}

