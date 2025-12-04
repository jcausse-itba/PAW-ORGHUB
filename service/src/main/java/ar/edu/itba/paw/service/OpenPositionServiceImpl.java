// DONE

package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.OpenPositionService;
import ar.edu.itba.paw.interfaces.service.OrganizationService;
import ar.edu.itba.paw.interfaces.service.OrganigramService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.OpenPositionNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.OrganizationNotFoundException;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.OpenPositionAnswer;
import ar.edu.itba.paw.model.OpenPositionApplication;
import ar.edu.itba.paw.model.OpenPositionQuestion;
import ar.edu.itba.paw.model.Organigram;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Tag;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.OpenPositionStatus;
import ar.edu.itba.paw.model.enums.Status;
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
public class OpenPositionServiceImpl implements OpenPositionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenPositionServiceImpl.class);

    private final OpenPositionDao openPositionDao;
    private final OpenPositionApplicationDao applicationDao;
    private final OrganizationDao orgDao;
    private final TagDao tagDao;
    private final InstitutionDao institutionDao;
    private final OrganigramDao organigramDao;
    private final EmailService emailService;
    private final OrganizationService organizationService;
    private final UserService userService;
    private final OrganigramService organigramService;

    @Override
    @TransactionalRead
    public OpenPosition findByID(final long positionID) {
        return openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
    }

    @Override
    @TransactionalRead
    public List<OpenPosition> findAllForInstitution(final long institutionID, final OrderCriteria order, final int pageNum) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        return openPositionDao.findAllForInstitution(institution, order, pageNum);
    }

    @Override
    @TransactionalRead
    public List<OpenPosition> findAllForTags(final long institutionID, final List<Long> tagIDs, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        List<Tag> tags = tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return openPositionDao.findAllForTags(institution, tags, filter, order, pageNum);
    }

    @Override
    @TransactionalRead
    public List<OpenPosition> findAllForOrganizations(final List<Long> orgIDs, final OrderCriteria order, final int pageNum) {
        List<Organization> organizations = orgIDs.stream()
                .map(orgID -> orgDao.findByID(orgID).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return openPositionDao.findAllForOrganizations(organizations, order, pageNum);
    }

    @Override
    @TransactionalRead
    public List<OpenPosition> findAllForOrganizationsAndTags(final List<Long> orgIDs, final List<Long> tagIDs, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum) {
        List<Organization> organizations = orgIDs.stream()
                .map(orgID -> orgDao.findByID(orgID).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<Tag> tags = tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return openPositionDao.findAllForOrganizationsAndTags(organizations, tags, filter, order, pageNum);
    }

    @Override
    @Transactional
    public OpenPosition create(final long orgID, final String title, final String description, final String requirements, final String workhours, final LocalDateTime datetime, final List<String> questions, final List<Long> tagIDs, final Long organigramPositionID) {
        LOGGER.debug("Creating open position '{}' for organization {}.", title, orgID);
        
        Organization organization = orgDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        Organigram organigram = organigramPositionID != null ? organigramDao.findByID(organigramPositionID).orElse(null) : null;
        Set<Tag> tags = tagIDs != null ? tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()) : new HashSet<>();
        
        OpenPosition openPosition = openPositionDao.create(organization, title, description, requirements, workhours, datetime, questions, organigram, tags);
        
        List<User> subscribers = organizationService.getSubscribers(orgID);
        String orgName = organization.getOrgName();
        LOGGER.info("Open position '{}' created successfully (ID: {}) for organization '{}'. Notifying {} subscribers.", title, openPosition.getPositionID(), orgName, subscribers.size());
        
        for (User subscriber : subscribers) {
            emailService.sendOpenPositionNotificationEmail(subscriber.getUserEmail(), orgName, title, description, openPosition.getPositionID(), orgID);
        }
        
        return openPosition;
    }

    @Override
    @Transactional
    public void update(final long positionID, final String title, final String description, final String requirements, final String workhours, final Long organigramPositionID, final List<Long> tagIDs) {
        LOGGER.debug("Updating open position {} with title '{}'.", positionID, title);
        
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        position.setTitle(title);
        position.setDescription(description);
        position.setRequirements(requirements);
        position.setWorkHours(workhours);
        
        Organigram organigram = organigramPositionID != null ? organigramDao.findByID(organigramPositionID).orElse(null) : null;
        position.setOrganigramPosition(organigram);
        
        Set<Tag> newTags = tagIDs != null ? tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()) : new HashSet<>();
        
        position.getTags().retainAll(newTags);
        position.getTags().addAll(newTags);
        
        LOGGER.info("Open position {} updated successfully.", positionID);
    }

    @Override
    @Transactional
    public void delete(final long positionID) {
        LOGGER.debug("Deleting open position {}.", positionID);
        
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        
        List<OpenPositionApplication> allApplications = applicationDao.getApplications(position);
        String orgName = position.getOrganization().getOrgName();
        
        List<OpenPositionApplication> acceptedApplications = applicationDao.getApplicationsByStatus(position, Status.ACCEPTED);
        if (!acceptedApplications.isEmpty() && position.getOrganigramPosition() != null) {
            organigramService.unassignUserFromPosition(position.getOrganigramPosition().getOrganigramID());
            LOGGER.debug("Unassigned user from organigram position {} for open position {}.", position.getOrganigramPosition().getOrganigramID(), positionID);
        }
        
        for (OpenPositionApplication application : allApplications) {
            User applicant = application.getUser();
            if (applicant != null && applicant.getUserEmail() != null && !applicant.getUserEmail().isEmpty()) {
                emailService.sendOpenPositionDeletedNotificationEmail(applicant.getUserEmail(), orgName, position.getTitle(), application.getStatus().toString(), position.getOrganization().getOrgID());
                LOGGER.debug("Position deletion notification sent to user {} for position {}.", applicant.getUserEmail(), positionID);
            }
        }
        LOGGER.info("Sent position deletion notifications to {} applicants.", allApplications.size());
        
        applicationDao.deleteAllApplicationsByPosition(position);
        openPositionDao.deleteAllQuestionsByPosition(position);
        openPositionDao.delete(position);
        
        LOGGER.info("Open position {} deleted successfully.", positionID);
    }

    @Override
    @Transactional
    public void addQuestion(final long positionID, final String question) {
        LOGGER.debug("Adding question to open position {}.", positionID);
        
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        openPositionDao.addQuestion(position, question);
        
        LOGGER.info("Question added to open position {}.", positionID);
    }

    @Override
    @TransactionalRead
    public List<OpenPositionQuestion> getQuestions(final long positionID) {
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        return openPositionDao.getQuestions(position);
    }

    @Override
    @Transactional
    public void applyToPosition(final long userID, final long positionID, final Map<Long, String> answers) {
        LOGGER.debug("User {} applying to position {}.", userID, positionID);
        
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        User user = userService.findByID(userID);
        
        OpenPositionApplication application = applicationDao.apply(position, user);
        
        if (answers != null) {
            for (Map.Entry<Long, String> entry : answers.entrySet()) {
                Optional<OpenPositionQuestion> questionOptional = openPositionDao.getQuestionByID(entry.getKey());
                questionOptional.ifPresent(openPositionQuestion -> applicationDao.addAnswer(application, openPositionQuestion, entry.getValue()));
            }
        }
        
        String orgName = position.getOrganization().getOrgName();
        String applicantName = user.getFirstName() + " " + user.getLastName();
        LOGGER.info("User {} applied to position '{}' (ID: {}).", user.getUserEmail(), position.getTitle(), positionID);
        emailService.sendApplicationNotificationEmail(
                position.getOrganization().getOrgEmail(),
                orgName,
                applicantName,
                position.getTitle(),
                positionID,
                position.getOrganization().getOrgID()
        );
    }

    @Override
    @TransactionalRead
    public List<OpenPositionApplication> getApplications(final long positionID) {
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        return applicationDao.getApplications(position);
    }

    @Override
    @TransactionalRead
    public Optional<OpenPositionApplication> getApplication(final long applicationID) {
        return applicationDao.getApplication(applicationID);
    }

    @Override
    @Transactional
    public void updateApplicationStatus(final long applicationID, final Status status) {
        LOGGER.debug("Updating application {} status to {}.", applicationID, status);
        
        OpenPositionApplication application = applicationDao.getApplication(applicationID)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationID));
        
        application.setStatus(status);
        
        User applicant = application.getUser();
        OpenPosition position = application.getOpenPosition();
        String orgName = position.getOrganization().getOrgName();
        emailService.sendApplicationStatusNotificationEmail( applicant.getUserEmail(), orgName, position.getTitle(), status.toString(), position.getPositionID(), position.getOrganization().getOrgID() );
        
        LOGGER.info("Application {} status updated to {}.", applicationID, status);
    }

    @Override
    @TransactionalRead
    public Optional<Status> getApplicationStatus(final long userID, final long positionID) {
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        User user = userService.findByID(userID);
        return applicationDao.getStatus(position, user);
    }

    @Override
    @TransactionalRead
    public List<OpenPositionAnswer> getAnswers(final long applicationID) {
        OpenPositionApplication application = applicationDao.getApplication(applicationID)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationID));
        return applicationDao.getAnswers(application);
    }

    @Override
    @TransactionalRead
    public List<OpenPositionApplication> getUserApplications(final long userID) {
        User user = userService.findByID(userID);
        return applicationDao.getUserApplications(user);
    }

    @Override
    @TransactionalRead
    public Map<Long, Status> getUserApplicationStatusMap(final long userID) {
        User user = userService.findByID(userID);
        return applicationDao.getUserApplicationStatusMap(user);
    }

    @Override
    @Transactional
    public void withdrawApplication(final long userID, final long positionID) {
        LOGGER.debug("User {} withdrawing application from position {}.", userID, positionID);
        
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        User user = userService.findByID(userID);
        
        applicationDao.withdraw(position, user);
        
        LOGGER.info("User {} withdrew application from position {}.", userID, positionID);
    }

    @Override
    @TransactionalRead
    public List<OpenPositionApplication> getApplicationsByStatus(final long positionID, final Status status) {
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        return applicationDao.getApplicationsByStatus(position, status);
    }

    @Override
    @TransactionalRead
    public Map<Status, Long> getApplicationCountsByStatus(final long positionID) {
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        return applicationDao.getApplicationCountsByStatus(position);
    }

    @Override
    @TransactionalRead
    public List<OpenPosition> findPopularOpenPositionsForInstitution(final long institutionID, final OrderCriteria order, final int limit) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        return openPositionDao.findPopularOpenPositionsForInstitution(institution, order, limit);
    }

    @Override
    @TransactionalRead
    public List<OpenPosition> findPopularOpenPositionsForTags(final long institutionID, final List<Long> tagIDs, final TagFilterCriteria filter, final OrderCriteria order, final int limit) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        List<Tag> tags = tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(tag -> tag != null)
                .collect(Collectors.toList());
        return openPositionDao.findPopularOpenPositionsForTags(institution, tags, filter, order, limit);
    }

    @Override
    @Transactional
    public void closePosition(final long positionID) {
        LOGGER.debug("Closing open position {}.", positionID);
        
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        position.setStatus(OpenPositionStatus.CLOSED);
        
        LOGGER.info("Open position {} closed.", positionID);
    }

    @Override
    @Transactional
    public void reopenPosition(final long positionID) {
        LOGGER.debug("Reopening open position {}.", positionID);
        
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        position.setStatus(OpenPositionStatus.ACTIVE);
        
        LOGGER.info("Open position {} reopened.", positionID);
    }

    @Override
    @Transactional
    public void pausePosition(final long positionID) {
        LOGGER.debug("Pausing open position {}.", positionID);
        
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        position.setStatus(OpenPositionStatus.PAUSED);
        
        LOGGER.info("Open position {} paused.", positionID);
    }

    @Override
    @Transactional
    public void rejectAllOtherApplications(final long positionID, final long acceptedApplicationID) {
        LOGGER.debug("Rejecting all other applications for position {} except {}.", positionID, acceptedApplicationID);
        
        OpenPosition position = openPositionDao.findByID(positionID).orElseThrow(() -> new OpenPositionNotFoundException(positionID));
        
        List<OpenPositionApplication> pendingApplications = applicationDao.getApplicationsByStatus(position, Status.PENDING);
        for (OpenPositionApplication application : pendingApplications) {
            application.setStatus(Status.REJECTED);
        }
        
        List<OpenPositionApplication> acceptedApplications = applicationDao.getApplicationsByStatus(position, Status.ACCEPTED);
        for (OpenPositionApplication application : acceptedApplications) {
            if (!application.getApplicationID().equals(acceptedApplicationID)) {
                application.setStatus(Status.REJECTED);
            }
        }
        
        LOGGER.info("Rejected all other applications for position {} except {}.", positionID, acceptedApplicationID);
    }
}
