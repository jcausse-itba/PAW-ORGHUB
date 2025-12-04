package ar.edu.itba.paw.interfaces.service.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(final long postID) {
        super("Post with ID " + postID + " not found.");
    }
}
