package ar.edu.itba.paw.interfaces.service.exception;

public class UserEmailExistsException extends RuntimeException {
    public UserEmailExistsException(final String userEmail) {
        super("User with email \"" + userEmail + "\" already exists.");
    }
}
