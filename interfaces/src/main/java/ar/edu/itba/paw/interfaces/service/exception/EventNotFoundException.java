package ar.edu.itba.paw.interfaces.service.exception;

public class EventNotFoundException extends RuntimeException{
    public EventNotFoundException(final long eventID) {
        super("Event with ID " + eventID + " not found.");
    }
}
