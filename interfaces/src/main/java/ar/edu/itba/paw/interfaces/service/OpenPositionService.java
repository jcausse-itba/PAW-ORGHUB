package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.OpenPositionAnswer;
import ar.edu.itba.paw.model.OpenPositionApplication;
import ar.edu.itba.paw.model.OpenPositionQuestion;
import ar.edu.itba.paw.model.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OpenPositionService {
    /**
     * Find an OpenPosition via its ID.
     * @param positionID              The ID of the OpenPosition to be found.
     * @return                        An OpenPosition if it was found.
     * @throws OpenPositionNotFoundException if the position was not found.
     */
    OpenPosition findByID(long positionID);

    /**
     * Get all open positions that belong to organizations within a specific institution.
     * @param institutionID          The ID of the Institution.
     * @param order                   The order criteria to sort the positions.
     * @param pageNum                 Page Number (pagination).
     * @return                        A List of OpenPosition objects (that might be empty).
     */
    List<OpenPosition> findAllForInstitution(long institutionID, OrderCriteria order, int pageNum);

    /**
     * Get all open positions that are tagged with the specified tags, filtered by the specified criteria.
     * @param institutionID          The ID of the Institution.
     * @param tagIDs                 The List of tag IDs.
     * @param filter                 The filter criteria to apply to the tags.
     * @param order                  The order criteria to sort the positions.
     * @param pageNum                Page Number (pagination).
     * @return                        A List of OpenPosition objects (that might be empty).
     */
    List<OpenPosition> findAllForTags(long institutionID, List<Long> tagIDs, TagFilterCriteria filter, OrderCriteria order, int pageNum);

    /**
     * Get all open positions published by the specified organizations.
     * @param orgIDs                 The List of organization IDs.
     * @param order                  The order criteria to sort the positions.
     * @param pageNum                Page Number (pagination).
     * @return                        A List of OpenPosition objects (that might be empty).
     */
    List<OpenPosition> findAllForOrganizations(List<Long> orgIDs, OrderCriteria order, int pageNum);

    /**
     * Get all open positions published by the specified organizations that are tagged with the specified tags, filtered by the specified criteria.
     * @param orgIDs                 The List of organization IDs.
     * @param tagIDs                 The List of tag IDs.
     * @param filter                 The filter criteria to apply to the tags.
     * @param order                  The order criteria to sort the positions.
     * @param pageNum                Page Number (pagination).
     * @return                        A List of OpenPosition objects (that might be empty).
     */
    List<OpenPosition> findAllForOrganizationsAndTags(List<Long> orgIDs, List<Long> tagIDs, TagFilterCriteria filter, OrderCriteria order, int pageNum);

    /**
     * Create a new open position and get the OpenPosition object that represents it.
     * @param orgID                  The ID of the Organization publishing the position.
     * @param title                   The position title. Must not be null.
     * @param description             The position description. Must not be null.
     * @param requirements            The position requirements. Must not be null.
     * @param workhours               The work hours.
     * @param datetime                The date and time when the position was created.
     * @param questions               The List of questions for the position.
     * @param tagIDs                  The List of tag IDs to associate with the position.
     * @param organigramPositionID    The ID of the Organigram position (optional).
     * @return                        An OpenPosition representing the created position.
     */
    OpenPosition create(long orgID, String title, String description, String requirements, String workhours, LocalDateTime datetime, List<String> questions, List<Long> tagIDs, Long organigramPositionID);

    /**
     * Update an open position.
     * @param positionID              The ID of the OpenPosition to update.
     * @param title                   The new title.
     * @param description             The new description.
     * @param requirements            The new requirements.
     * @param workhours               The new work hours.
     * @param organigramPositionID    The ID of the Organigram position (optional).
     * @param tagIDs                  The List of tag IDs to associate with the position.
     */
    void update(long positionID, String title, String description, String requirements, String workhours, Long organigramPositionID, List<Long> tagIDs);

    /**
     * Delete an open position.
     * @param positionID              The ID of the OpenPosition to delete.
     */
    void delete(long positionID);

    /**
     * Add a question to an open position.
     * @param positionID              The ID of the OpenPosition.
     * @param question                The question text. Must not be null.
     */
    void addQuestion(long positionID, String question);

    /**
     * Get all questions for an open position.
     * @param positionID              The ID of the OpenPosition.
     * @return                        A List of OpenPositionQuestion objects (that might be empty).
     */
    List<OpenPositionQuestion> getQuestions(long positionID);

    /**
     * Apply to an open position.
     * @param userID                  The ID of the User applying.
     * @param positionID              The ID of the OpenPosition.
     * @param answers                 A Map of question IDs to answer texts.
     */
    void applyToPosition(long userID, long positionID, Map<Long, String> answers);

    /**
     * Get all applications for an open position.
     * @param positionID              The ID of the OpenPosition.
     * @return                        A List of OpenPositionApplication objects (that might be empty).
     */
    List<OpenPositionApplication> getApplications(long positionID);

    /**
     * Find an OpenPositionApplication via its ID.
     * @param applicationID           The ID of the application to be found.
     * @return                        An Optional containing an OpenPositionApplication if it was found. An empty Optional otherwise.
     */
    Optional<OpenPositionApplication> getApplication(long applicationID);

    /**
     * Update the status of an application.
     * @param applicationID           The ID of the OpenPositionApplication.
     * @param status                  The new Status.
     */
    void updateApplicationStatus(long applicationID, Status status);

    /**
     * Get the application status for a user in an open position.
     * @param userID                  The ID of the User.
     * @param positionID              The ID of the OpenPosition.
     * @return                        An Optional containing the Status if the user has applied. An empty Optional otherwise.
     */
    Optional<Status> getApplicationStatus(long userID, long positionID);

    /**
     * Get all answers for an application.
     * @param applicationID           The ID of the OpenPositionApplication.
     * @return                        A List of OpenPositionAnswer objects (that might be empty).
     */
    List<OpenPositionAnswer> getAnswers(long applicationID);

    /**
     * Get all applications for a user.
     * @param userID                  The ID of the User.
     * @return                        A List of OpenPositionApplication objects (that might be empty).
     */
    List<OpenPositionApplication> getUserApplications(long userID);

    /**
     * Get a map of position IDs to application statuses for a user.
     * @param userID                  The ID of the User.
     * @return                        A Map with position ID as key and Status as value.
     */
    Map<Long, Status> getUserApplicationStatusMap(long userID);

    /**
     * Withdraw (delete) an application.
     * @param userID                  The ID of the User.
     * @param positionID              The ID of the OpenPosition.
     */
    void withdrawApplication(long userID, long positionID);

    /**
     * Get all applications for an open position filtered by status.
     * @param positionID              The ID of the OpenPosition.
     * @param status                  The Status to filter by.
     * @return                        A List of OpenPositionApplication objects (that might be empty).
     */
    List<OpenPositionApplication> getApplicationsByStatus(long positionID, Status status);

    /**
     * Get application counts grouped by status for an open position.
     * @param positionID              The ID of the OpenPosition.
     * @return                        A Map with Status as key and count as value.
     */
    Map<Status, Long> getApplicationCountsByStatus(long positionID);

    /**
     * Find popular open positions for a specific institution, ordered by engagement (applications) and date.
     * @param institutionID           The ID of the Institution.
     * @param order                   The order criteria to sort the positions when application counts are equal.
     * @param limit                   The maximum number of positions to return.
     * @return                        A List of OpenPosition objects (that might be empty), ordered by engagement and date.
     */
    List<OpenPosition> findPopularOpenPositionsForInstitution(long institutionID, OrderCriteria order, int limit);

    /**
     * Find popular open positions for a specific institution that are tagged with the specified tags, filtered by the specified criteria, ordered by engagement (applications) and date.
     * @param institutionID          The ID of the Institution.
     * @param tagIDs                 The List of tag IDs.
     * @param filter                 The filter criteria to apply to the tags.
     * @param order                  The order criteria to sort the positions when application counts are equal.
     * @param limit                  The maximum number of positions to return.
     * @return                       A List of OpenPosition objects (that might be empty), ordered by engagement and date.
     */
    List<OpenPosition> findPopularOpenPositionsForTags(long institutionID, List<Long> tagIDs, TagFilterCriteria filter, OrderCriteria order, int limit);

    /**
     * Close an open position (set status to CLOSED).
     * @param positionID              The ID of the OpenPosition.
     */
    void closePosition(long positionID);

    /**
     * Reopen an open position (set status to ACTIVE).
     * @param positionID              The ID of the OpenPosition.
     */
    void reopenPosition(long positionID);

    /**
     * Pause an open position (set status to PAUSED).
     * @param positionID              The ID of the OpenPosition.
     */
    void pausePosition(long positionID);

    /**
     * Reject all other applications for a position when one is accepted.
     * @param positionID              The ID of the OpenPosition.
     * @param acceptedApplicationID   The ID of the accepted application.
     */
    void rejectAllOtherApplications(long positionID, long acceptedApplicationID);
}
