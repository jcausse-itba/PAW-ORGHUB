package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.EventEnrollment;
import ar.edu.itba.paw.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventService {
    /**
     * Find an Event via its ID.
     * @param eventID              The ID of the Event to be found.
     * @return                     An Event if it was found.
     * @throws EventNotFoundException if the event was not found.
     */
    Event findByID(long eventID);

    /**
     * Get all events that belong to organizations within a specific institution.
     * @param institutionID        The ID of the Institution.
     * @param order                The order criteria to sort the events.
     * @param pageNum              Page Number (pagination).
     * @return                     A List of Event objects (that might be empty).
     */
    List<Event> findAllForInstitution(long institutionID, OrderCriteria order, int pageNum);

    /**
     * Get all events that are tagged with the specified tags, filtered by the specified criteria.
     * @param institutionID        The ID of the Institution.
     * @param tagIDs               The List of tag IDs.
     * @param filter               The filter criteria to apply to the tags.
     * @param order                The order criteria to sort the events.
     * @param pageNum              Page Number (pagination).
     * @return                     A List of Event objects (that might be empty).
     */
    List<Event> findAllForTags(long institutionID, List<Long> tagIDs, TagFilterCriteria filter, OrderCriteria order, int pageNum);

    /**
     * Get all events published by the specified organizations.
     * @param orgIDs               The List of organization IDs.
     * @param order                The order criteria to sort the events.
     * @param pageNum              Page Number (pagination).
     * @return                     A List of Event objects (that might be empty).
     */
    List<Event> findAllForOrganizations(List<Long> orgIDs, OrderCriteria order, int pageNum);

    /**
     * Get all events published by the specified organizations that are tagged with the specified tags, filtered by the specified criteria.
     * @param orgIDs               The List of organization IDs.
     * @param tagIDs               The List of tag IDs.
     * @param filter               The filter criteria to apply to the tags.
     * @param order                The order criteria to sort the events.
     * @param pageNum              Page Number (pagination).
     * @return                     A List of Event objects (that might be empty).
     */
    List<Event> findAllForOrganizationsAndTags(List<Long> orgIDs, List<Long> tagIDs, TagFilterCriteria filter, OrderCriteria order, int pageNum);

    /**
     * Create a new event and get the Event object that represents it.
     * @param orgID                The ID of the Organization publishing the event.
     * @param title                 The event's title. Must not be null.
     * @param description           The event's description. Must not be null.
     * @param location              The event's location. Must not be null.
     * @param eventDate             The date and time when the event will take place. Must not be null.
     * @param dateTime              The date and time when the event was created.
     * @param tagIDs                The List of tag IDs to associate with the event.
     * @param imageID               Optional image ID for the event flyer.
     * @return                      An Event representing the created event.
     */
    Event create(long orgID, String title, String description, String location, LocalDateTime eventDate, LocalDateTime dateTime, List<Long> tagIDs, Integer imageID);

    /**
     * Update an event.
     * @param eventID               The ID of the Event to update.
     * @param title                 The new title.
     * @param description           The new description.
     * @param location              The new location.
     * @param eventDate             The new event date.
     * @param tagIDs                The List of tag IDs to associate with the event.
     * @param imageID               Optional image ID for the event flyer. If null, keeps existing image. If provided, updates the image.
     */
    void update(long eventID, String title, String description, String location, LocalDateTime eventDate, List<Long> tagIDs, Integer imageID);

    /**
     * Delete an event.
     * @param eventID               The ID of the Event to delete.
     */
    void delete(long eventID);

    /**
     * Enroll a user to an event.
     * @param eventID               The ID of the Event.
     * @param userID                The ID of the User.
     */
    void enrollUser(long eventID, long userID);

    /**
     * Unenroll a user from an event.
     * @param eventID               The ID of the Event.
     * @param userID                The ID of the User.
     */
    void unenrollUser(long eventID, long userID);

    /**
     * Check if a user is enrolled to an event.
     * @param eventID               The ID of the Event.
     * @param userID                The ID of the User.
     * @return                      Boolean indicating whether the user is enrolled.
     */
    boolean isUserEnrolled(long eventID, long userID);

    /**
     * Get the enrollment count for an event.
     * @param eventID               The ID of the Event.
     * @return                      The number of enrollments.
     */
    long getEnrollmentCount(long eventID);

    /**
     * Get all enrollments for an event.
     * @param eventID               The ID of the Event.
     * @return                      A List of EventEnrollment objects (that might be empty).
     */
    List<EventEnrollment> getEnrollments(long eventID);

    /**
     * Get all enrollments for an event filtered by status.
     * @param eventID               The ID of the Event.
     * @param status                The Status to filter by.
     * @return                      A List of EventEnrollment objects (that might be empty).
     */
    List<EventEnrollment> getEnrollmentsByStatus(long eventID, Status status);

    /**
     * Get enrollment counts grouped by status for an event.
     * @param eventID               The ID of the Event.
     * @return                      A Map with Status as key and count as value.
     */
    Map<Status, Long> getEnrollmentCountsByStatus(long eventID);

    /**
     * Get the enrollment status for a user in an event.
     * @param eventID               The ID of the Event.
     * @param userID                The ID of the User.
     * @return                      An Optional containing the Status if the user is enrolled. An empty Optional otherwise.
     */
    Optional<Status> getEnrollmentStatus(long eventID, long userID);

    /**
     * Get all events a user is enrolled to.
     * @param userID                The ID of the User.
     * @return                      A List of EventEnrollment objects (that might be empty).
     */
    List<EventEnrollment> getAllEventsUserEnrolled(long userID);

    /**
     * Update the enrollment status for a user in an event.
     * @param eventID               The ID of the Event.
     * @param userID                The ID of the User.
     * @param status                The new Status.
     */
    void updateEnrollmentStatus(long eventID, long userID, Status status);

    /**
     * Get a map of event IDs to enrollment statuses for a user.
     * @param userID                The ID of the User.
     * @return                      A Map with event ID as key and Status as value.
     */
    Map<Long, Status> getUserEnrollmentsMap(long userID);

    /**
     * Find popular events for a specific institution, ordered by engagement (enrollments) and date.
     * @param institutionID         The ID of the Institution.
     * @param order                 The order criteria to sort the events when enrollment counts are equal.
     * @param limit                 The maximum number of events to return.
     * @return                      A List of Event objects (that might be empty), ordered by engagement and date.
     */
    List<Event> findPopularEventsForInstitution(long institutionID, OrderCriteria order, int limit);

    /**
     * Find popular events for a specific institution that are tagged with the specified tags, filtered by the specified criteria, ordered by engagement (enrollments) and date.
     * @param institutionID         The ID of the Institution.
     * @param tagIDs               The List of tag IDs.
     * @param filter               The filter criteria to apply to the tags.
     * @param order                The order criteria to sort the events when enrollment counts are equal.
     * @param limit                The maximum number of events to return.
     * @return                     A List of Event objects (that might be empty), ordered by engagement and date.
     */
    List<Event> findPopularEventsForTags(long institutionID, List<Long> tagIDs, TagFilterCriteria filter, OrderCriteria order, int limit);

    /**
     * Get the organization ID for an event.
     * @param eventID               The ID of the Event.
     * @return                      The organization ID.
     * @throws EventNotFoundException if the event was not found.
     */
    long getOrganizationIDByID(long eventID);
}
