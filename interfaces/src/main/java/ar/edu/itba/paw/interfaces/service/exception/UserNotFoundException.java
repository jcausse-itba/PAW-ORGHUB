package ar.edu.itba.paw.interfaces.service.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(final long userID) {
        super("User with id " + userID + " not found");
    }
    public UserNotFoundException(final String email) {
        super("User with email \"" + email + "\" not found");
    }
}
