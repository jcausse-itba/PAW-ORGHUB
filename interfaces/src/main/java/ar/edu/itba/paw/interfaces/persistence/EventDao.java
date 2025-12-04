// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Tag;
import ar.edu.itba.paw.model.image.Image;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventDao {
    /**
     * Find an Event via its ID.
     * @param eventID        The ID of the Event to be found.
     * @return               An Optional containing an Event if it was found. An empty Optional otherwise.
     */
    Optional<Event> findByID(long eventID);

    /**
     * Get all events that belong to organizations within a specific institution.
     * @param inst          The Institution.
     * @param order         The order criteria to sort the events.
     * @param pageNum       Page Number (pagination)
     * @return              A List of Event objects (that might be empty).
     */
    List<Event> findAllForInstitution(Institution inst, OrderCriteria order, int pageNum);

    /**
     * Get all events published by the specified organizations, ordered by event date.
     * @param orgs          The List of Organizations.
     * @param order         The order criteria to sort the events.
     * @param pageNum       Page Number (pagination)
     * @return              A List of Event objects (that might be empty), ordered by event date.
     */
    List<Event> findAllForOrganizations(List<Organization> orgs, OrderCriteria order, int pageNum);

    /**
     * Get all events that are tagged with the specified tags, filtered by the specified criteria, ordered by the specified criteria.
     * @param inst          The Institution.
     * @param tags          The List of Tags.
     * @param filter        The filter criteria to apply to the tags.
     * @param order         The order criteria to sort the events.
     * @param pageNum       Page Number (pagination)
     * @return              A List of Event objects (that might be empty).
     */
    List<Event> findAllForTags(Institution inst, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum);

    /**
     * Get all events published by the specified organizations that are tagged with the specified tags, filtered by the specified criteria.
     * @param orgs          The List of Organizations.
     * @param tags          The List of Tags.
     * @param filter        The filter criteria to apply to the tags.
     * @param order         The order criteria to sort the events.
     * @param pageNum       Page Number (pagination)
     * @return              A List of Event objects (that might be empty).
     */
    List<Event> findAllForOrganizationsAndTags(List<Organization> orgs, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum);

    /**
     * Create a new event and get the Event object that represents it.
     * @param org           The Organization publishing the event. Must not be null.
     * @param title         The event's title. Must not be null.
     * @param desc          The event's description. Must not be null.
     * @param location      The event's location. Must not be null.
     * @param eventDate     The date and time when the event will take place. Must not be null.
     * @param dateTime      The date and time when the event was created.
     * @param tags          The Set of Tags to associate with the event. Up to CONTENT_MAX_TAGS tags will be added.
     * @param image         Optional Image for the event flyer. Can be null.
     * @return              An Event representing the created event.
     */
    Event create(Organization org, String title, String desc, String location, LocalDateTime eventDate, LocalDateTime dateTime, Set<Tag> tags, ar.edu.itba.paw.model.image.Image image);

    /**
     * Delete an event.
     * @param event       The Event to be deleted. Must not be null.
     */
    void delete(Event event);
    
    /**
     * Find popular events (an Event is considered popular when there are many Users enrolled to it) for a specific
     * institution, ordered by enrollment count and event date.
     * @param inst          The Institution.
     * @param order         The order criteria to sort the events when enrollment counts are equal.
     * @param limit         The maximum number of events to return.
     * @return              A List of Event objects (that might be empty), ordered by popularity and date.
     */
    List<Event> findPopularEventsForInstitution(Institution inst, OrderCriteria order, int limit);

    /**
     * Find popular events for a specific institution that are tagged with the specified tags, filtered by the specified criteria, ordered by enrollment count and event date.
     * @param inst          The Institution.
     * @param tags          The List of Tags.
     * @param filter        The filter criteria to apply to the tags.
     * @param order         The order criteria to sort the events when enrollment counts are equal.
     * @param limit         The maximum number of events to return.
     * @return              A List of Event objects (that might be empty), ordered by popularity and date.
     */
    List<Event> findPopularEventsForTags(Institution inst, List<Tag> tags, TagFilterCriteria filter, OrderCriteria order, int limit);
}
