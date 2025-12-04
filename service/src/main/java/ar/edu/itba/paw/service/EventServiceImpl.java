// DONE

package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.EventService;
import ar.edu.itba.paw.interfaces.service.ImageService;
import ar.edu.itba.paw.interfaces.service.OrganizationService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.interfaces.service.exception.EventNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.OrganizationNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.UserNotEnrolledException;
import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.EventEnrollment;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Tag;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.model.image.Image;
import ar.edu.itba.paw.service.annotation.TransactionalRead;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventDao eventDao;
    private final TagDao tagDao;
    private final OrganizationDao orgDao;
    private final InstitutionDao institutionDao;
    private final EventEnrollmentDao eventEnrollmentDao;
    private final EmailService emailService;
    private final OrganizationService organizationService;
    private final UserService userService;
    private final ImageService imageService;

    @Override
    @TransactionalRead
    public Event findByID(final long eventID) {
        return eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
    }

    @Override
    @TransactionalRead
    public List<Event> findAllForInstitution(final long institutionID, final OrderCriteria order, final int pageNum) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        return eventDao.findAllForInstitution(institution, order, pageNum);
    }

    @Override
    @TransactionalRead
    public List<Event> findAllForTags(final long institutionID, final List<Long> tagIDs, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        List<Tag> tags = tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return eventDao.findAllForTags(institution, tags, filter, order, pageNum);
    }

    @Override
    @TransactionalRead
    public List<Event> findAllForOrganizations(final List<Long> orgIDs, final OrderCriteria order, final int pageNum) {
        List<Organization> organizations = orgIDs.stream()
                .map(orgID -> orgDao.findByID(orgID).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return eventDao.findAllForOrganizations(organizations, order, pageNum);
    }

    @Override
    @TransactionalRead
    public List<Event> findAllForOrganizationsAndTags(final List<Long> orgIDs, final List<Long> tagIDs, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum) {
        List<Organization> organizations = orgIDs.stream()
                .map(orgID -> orgDao.findByID(orgID).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<Tag> tags = tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return eventDao.findAllForOrganizationsAndTags(organizations, tags, filter, order, pageNum);
    }

    @Override
    @Transactional
    public Event create(final long orgID, final String title, final String description, final String location, final LocalDateTime eventDate, final LocalDateTime dateTime, final List<Long> tagIDs, final Integer imageID) {
        LOGGER.debug("Creating event '{}' for organization {}.", title, orgID);
        
        Organization organization = orgDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        Set<Tag> tags = tagIDs != null ? tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()) : new HashSet<>();
        
        ar.edu.itba.paw.model.image.Image image = imageID != null 
                ? imageService.getImage(imageID).orElse(null) 
                : null;
        
        Event event = eventDao.create(organization, title, description, location, eventDate, dateTime, tags, image);
        
        List<User> subscribers = organizationService.getSubscribers(orgID);
        String orgName = organization.getOrgName();
        LOGGER.info("Event '{}' created successfully (ID: {}) for organization '{}'. Notifying {} subscribers.", 
                title, event.getEventID(), orgName, subscribers.size()
        );
        
        for (User subscriber : subscribers) {
            emailService.sendEventNotificationEmail(subscriber.getUserEmail(), orgName, title, description, location, event.getEventID());
        }
        
        return event;
    }

    @Override
    @Transactional
    public void update(final long eventID, final String title, final String description, final String location, final LocalDateTime eventDate, final List<Long> tagIDs, final Integer imageID) {
        LOGGER.debug("Updating event {} with title '{}'.", eventID, title);
        
        Event event = eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
        boolean dateChanged = !event.getEventDate().equals(eventDate);
        boolean locationChanged = !event.getLocation().equals(location);
        
        event.setTitle(title);
        event.setDescription(description);
        event.setLocation(location);
        event.setEventDate(eventDate);
        
        if (imageID != null) {
            Image oldImage = event.getImage();
            Image newImage = imageService.getImage(imageID).orElse(null);
            event.setImage(newImage);
            
            if (oldImage != null) {
                imageService.deleteImage(oldImage.getImageID());
                LOGGER.debug("Deleted old image {} for event {}.", oldImage.getImageID(), eventID);
            }
        }
        
        Set<Tag> newTags = tagIDs != null ? tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()) : new HashSet<>();
        
        event.getTags().retainAll(newTags);
        event.getTags().addAll(newTags);
        
        if (dateChanged || locationChanged) {
            LOGGER.debug("Event date or location changed. Notifying accepted participants.");
            List<EventEnrollment> acceptedEnrollments = eventEnrollmentDao.getEnrollmentsByStatus(event, Status.ACCEPTED);
            String orgName = event.getOrganization().getOrgName();
            
            for (EventEnrollment enrollment : acceptedEnrollments) {
                User user = enrollment.getUser();
                if (user != null && user.getUserEmail() != null && !user.getUserEmail().isEmpty()) {
                    emailService.sendEventUpdateNotificationEmail(user.getUserEmail(), orgName, title, location, eventDate, eventID);
                    LOGGER.debug("Event update notification sent to user {} for event {}.", user.getUserEmail(), eventID);
                }
            }
            LOGGER.info("Sent event update notifications to {} accepted participants.", acceptedEnrollments.size());
        }
        
        LOGGER.info("Event {} updated successfully.", eventID);
    }

    @Override
    @Transactional
    public void delete(final long eventID) {
        LOGGER.debug("Deleting event {}.", eventID);
        
        Event event = eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
        
        if (event.getImage() != null) {
            imageService.deleteImage(event.getImage().getImageID());
            LOGGER.debug("Deleted image {} associated with event {}.", event.getImage().getImageID(), eventID);
        }
        
        eventEnrollmentDao.deleteAllEnrollmentsByEvent(event);
        eventDao.delete(event);
        
        LOGGER.info("Event {} deleted successfully.", eventID);
    }

    @Override
    @Transactional
    public void enrollUser(final long eventID, final long userID) {
        LOGGER.debug("Enrolling user {} in event {}.", userID, eventID);
        
        Event event = eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
        User user = userService.findByID(userID);
        
        if (!eventEnrollmentDao.isUserEnrolled(event, user)) {
            eventEnrollmentDao.enrollUser(event, user);
            
            String orgName = event.getOrganization().getOrgName();
            String participantName = user.getFirstName() + " " + user.getLastName();
            LOGGER.info("User {} enrolled in event '{}' (ID: {}).", user.getUserEmail(), event.getTitle(), eventID);
            emailService.sendEnrollmentNotificationEmail(event.getOrganization().getOrgEmail(), orgName, participantName, event.getTitle(), eventID, event.getOrganization().getOrgID());
        } else {
            LOGGER.debug("User {} already enrolled in event {}.", userID, eventID);
        }
    }

    @Override
    @Transactional
    public void unenrollUser(final long eventID, final long userID) {
        LOGGER.debug("Unenrolling user {} from event {}.", userID, eventID);
        
        Event event = eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
        User user = userService.findByID(userID);
        
        Status status = eventEnrollmentDao.getEnrollmentStatus(event, user).orElseThrow(() -> new UserNotEnrolledException(eventID, userID));
        
        if (status == Status.PENDING || status == Status.ACCEPTED) {
            eventEnrollmentDao.unenrollUser(event, user);
            LOGGER.info("User {} unenrolled from event {}.", userID, eventID);
        } else {
            LOGGER.debug("User {} cannot unenroll from event {} with status {}.", userID, eventID, status);
        }
    }

    @Override
    @TransactionalRead
    public boolean isUserEnrolled(final long eventID, final long userID) {
        Event event = eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
        User user = userService.findByID(userID);
        return eventEnrollmentDao.isUserEnrolled(event, user);
    }

    @Override
    @TransactionalRead
    public long getEnrollmentCount(final long eventID) {
        Event event = eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
        return eventEnrollmentDao.getEnrollmentCount(event);
    }

    @Override
    @TransactionalRead
    public List<EventEnrollment> getEnrollments(final long eventID) {
        Event event = eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
        return eventEnrollmentDao.getEnrollments(event);
    }

    @Override
    @TransactionalRead
    public List<EventEnrollment> getEnrollmentsByStatus(final long eventID, final Status status) {
        Event event = eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
        return eventEnrollmentDao.getEnrollmentsByStatus(event, status);
    }

    @Override
    @TransactionalRead
    public Map<Status, Long> getEnrollmentCountsByStatus(final long eventID) {
        Event event = eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
        return eventEnrollmentDao.getEnrollmentCountsByStatus(event);
    }

    @Override
    @TransactionalRead
    public Optional<Status> getEnrollmentStatus(final long eventID, final long userID) {
        Event event = eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
        User user = userService.findByID(userID);
        return eventEnrollmentDao.getEnrollmentStatus(event, user);
    }

    @Override
    @TransactionalRead
    public List<EventEnrollment> getAllEventsUserEnrolled(final long userID) {
        User user = userService.findByID(userID);
        return eventEnrollmentDao.getAllEventsUserEnrolled(user);
    }

    @Override
    @Transactional
    public void updateEnrollmentStatus(final long eventID, final long userID, final Status status) {
        LOGGER.debug("Updating enrollment status for user {} in event {} to {}.", userID, eventID, status);
        
        Event event = eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
        User user = userService.findByID(userID);
        
        eventEnrollmentDao.updateEnrollmentStatus(event, user, status);
        
        String orgName = event.getOrganization().getOrgName();
        emailService.sendEnrollmentStatusNotificationEmail(user.getUserEmail(), orgName, event.getTitle(), status.toString(), eventID);
        
        LOGGER.info("Enrollment status updated for user {} in event {} to {}.", userID, eventID, status);
    }

    @Override
    @TransactionalRead
    public Map<Long, Status> getUserEnrollmentsMap(final long userID) {
        List<EventEnrollment> enrollments = getAllEventsUserEnrolled(userID);
        return enrollments.stream()
                .collect(Collectors.toMap(
                        enrollment -> enrollment.getEvent().getEventID(),
                        EventEnrollment::getStatus
                ));
    }

    @Override
    @TransactionalRead
    public List<Event> findPopularEventsForInstitution(final long institutionID, final OrderCriteria order, final int limit) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        return eventDao.findPopularEventsForInstitution(institution, order, limit);
    }

    @Override
    @TransactionalRead
    public List<Event> findPopularEventsForTags(final long institutionID, final List<Long> tagIDs, final TagFilterCriteria filter, final OrderCriteria order, final int limit) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        List<Tag> tags = tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(tag -> tag != null)
                .collect(Collectors.toList());
        return eventDao.findPopularEventsForTags(institution, tags, filter, order, limit);
    }

    @Override
    @TransactionalRead
    public long getOrganizationIDByID(final long eventID) {
        Event event = eventDao.findByID(eventID).orElseThrow(() -> new EventNotFoundException(eventID));
        return event.getOrganization().getOrgID();
    }
}
