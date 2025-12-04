package ar.edu.itba.paw.interfaces.service.exception;

public class UserNameExistsException extends RuntimeException {
    public UserNameExistsException(final String username) {
        super("Username \"" + username + "\" already exists.");
    }
}
