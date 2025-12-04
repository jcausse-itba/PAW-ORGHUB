package ar.edu.itba.paw.interfaces.service.exception;

public class UserNotEnrolledException extends RuntimeException{
    public UserNotEnrolledException(final long eventID, final long userID) {
        super("User with ID " + userID + " not enrolled in Event with ID " +eventID);
    }
}
