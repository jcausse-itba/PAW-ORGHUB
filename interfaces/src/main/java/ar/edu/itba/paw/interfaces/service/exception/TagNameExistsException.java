package ar.edu.itba.paw.interfaces.service.exception;

public class TagNameExistsException extends RuntimeException {
    public TagNameExistsException(final String tagName) {
        super("Tag with name \"" + tagName + "\" already exists.");
    }
}
