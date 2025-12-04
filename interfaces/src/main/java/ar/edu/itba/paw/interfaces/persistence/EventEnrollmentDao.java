// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.EventEnrollment;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventEnrollmentDao {
    /**
     * Enroll a user to an event.
     * @param event     The Event to enroll to. Must not be null.
     * @param user      The User to enroll. Must not be null.
     */
    void enrollUser(Event event, User user);
    
    /**
     * Unenroll a user from an event.
     * @param event     The Event to unenroll from. Must not be null.
     * @param user      The User to unenroll. Must not be null.
     */
    void unenrollUser(Event event, User user);
    
    /**
     * Check if a user is enrolled to an event.
     * @param event     The Event to check. Must not be null.
     * @param user      The User to check. Must not be null.
     * @return          Boolean indicating whether the user is enrolled.
     */
    boolean isUserEnrolled(Event event, User user);
    
    /**
     * Get all enrollments for an event.
     * @param event     The Event. Must not be null.
     * @return          A List of EventEnrollment objects (that might be empty).
     */
    List<EventEnrollment> getEnrollments(Event event);
    
    /**
     * Get the enrollment count for an event.
     * @param event     The Event. Must not be null.
     * @return          The number of enrollments.
     */
    long getEnrollmentCount(Event event);
    
    /**
     * Get all events a user is enrolled to.
     * @param user      The User. Must not be null.
     * @return          A List of EventEnrollment objects (that might be empty).
     */
    List<EventEnrollment> getAllEventsUserEnrolled(User user);
    
    /**
     * Update the enrollment status for a user in an event.
     * @param event     The Event. Must not be null.
     * @param user      The User. Must not be null.
     * @param status    The new Status.
     */
    void updateEnrollmentStatus(Event event, User user, Status status);
    
    /**
     * Get the enrollment status for a user in an event.
     * @param event     The Event. Must not be null.
     * @param user      The User. Must not be null.
     * @return          An Optional containing the Status if the user is enrolled. An empty Optional otherwise.
     */
    Optional<Status> getEnrollmentStatus(Event event, User user);
    
    /**
     * Get all enrollments for an event filtered by status.
     * @param event     The Event. Must not be null.
     * @param status    The Status to filter by.
     * @return          A List of EventEnrollment objects (that might be empty).
     */
    List<EventEnrollment> getEnrollmentsByStatus(Event event, Status status);
    
    /**
     * Get enrollment counts grouped by status for an event.
     * @param event     The Event. Must not be null.
     * @return          A Map with Status as key and count as value.
     */
    Map<Status, Long> getEnrollmentCountsByStatus(Event event);
    
    /**
     * Delete all enrollments for an event.
     * @param event     The Event whose enrollments are to be deleted. Must not be null.
     */
    void deleteAllEnrollmentsByEvent(Event event);
    
    /**
     * Get all accepted enrollments for events that are happening within the specified time range.
     * @param startTime The start of the time range (inclusive).
     * @param endTime   The end of the time range (exclusive).
     * @return          A List of EventEnrollment objects with accepted status for events in the time range.
     */
    List<EventEnrollment> getAcceptedEnrollmentsForUpcomingEvents(LocalDateTime startTime, LocalDateTime endTime);
}
