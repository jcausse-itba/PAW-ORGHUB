// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OpenPositionDao {
    /**
     * Find an OpenPosition via its ID.
     * @param positionID    The ID of the OpenPosition to be found.
     * @return              An Optional containing an OpenPosition if it was found. An empty Optional otherwise.
     */
    Optional<OpenPosition> findByID(long positionID);

    /**
     * Get all open positions that are tagged with the specified tags, filtered by the specified criteria, ordered by the specified criteria.
     * @param inst          The Institution.
     * @param tags          The List of Tags.
     * @param filter        The filter criteria to apply to the tags.
     * @param order         The order criteria to sort the positions.
     * @param pageNum       Page Number (pagination)
     * @return              A List of OpenPosition objects (that might be empty).
     */
    List<OpenPosition> findAllForTags(Institution inst, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum);

    /**
     * Get all open positions published by the specified organizations that are tagged with the specified tags, filtered by the specified criteria.
     * @param orgs          The List of Organizations.
     * @param tags          The List of Tags.
     * @param filter        The filter criteria to apply to the tags.
     * @param order         The order criteria to sort the positions.
     * @param pageNum       Page Number (pagination)
     * @return              A List of OpenPosition objects (that might be empty).
     */
    List<OpenPosition> findAllForOrganizationsAndTags(List<Organization> orgs, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum);

    /**
     * Create a new open position and get the OpenPosition object that represents it.
     * @param org               The Organization. Must not be null.
     * @param title             The position title. Must not be null.
     * @param description       The position description. Must not be null.
     * @param requirements      The position requirements. Must not be null.
     * @param workhours         The work hours.
     * @param datetime          The date and time.
     * @param questions         The List of questions for the position.
     * @param organigram        The Organigram position (optional).
     * @param tags              The Set of Tags to associate with the position. Up to CONTENT_MAX_TAGS tags will be added.
     * @return                  An OpenPosition representing the created position.
     */
    OpenPosition create(Organization org, String title, String description, String requirements, String workhours, LocalDateTime datetime, List<String> questions, Organigram organigram, Set<Tag> tags);
    
    /**
     * Get all open positions that belong to organizations within a specific institution.
     * @param inst          The Institution.
     * @param order         The order criteria to sort the positions.
     * @param pageNum       Page Number (pagination)
     * @return              A List of OpenPosition objects (that might be empty).
     */
    List<OpenPosition> findAllForInstitution(Institution inst, OrderCriteria order, int pageNum);
    
    /**
     * Add a question to an open position.
     * @param position          The OpenPosition. Must not be null.
     * @param question          The question text. Must not be null.
     */
    void addQuestion(OpenPosition position, String question);
    
    /**
     * Get all questions for an open position.
     * @param position          The OpenPosition. Must not be null.
     * @return                  A List of OpenPositionQuestion objects (that might be empty).
     */
    List<OpenPositionQuestion> getQuestions(OpenPosition position);
    
    /**
     * Find an OpenPositionQuestion via its ID.
     * @param questionID        The ID of the question to be found.
     * @return                  An Optional containing an OpenPositionQuestion if it was found. An empty Optional otherwise.
     */
    Optional<OpenPositionQuestion> getQuestionByID(long questionID);
    
    /**
     * Get all open positions published by the specified organizations, ordered by date.
     * @param orgs          The List of Organizations.
     * @param order         The order criteria to sort the positions.
     * @param pageNum       Page Number (pagination)
     * @return              A List of OpenPosition objects (that might be empty), ordered by date.
     */
    List<OpenPosition> findAllForOrganizations(List<Organization> orgs, OrderCriteria order, int pageNum);
    
    /**
     * Find popular open positions for a specific institution, ordered by engagement (applications) and date.
     * @param inst          The Institution.
     * @param order         The order criteria to sort the positions when applications are equal.
     * @param limit         The maximum number of positions to return.
     * @return              A List of OpenPosition objects (that might be empty), ordered by engagement and date.
     */
    List<OpenPosition> findPopularOpenPositionsForInstitution(Institution inst, OrderCriteria order, int limit);

    /**
     * Find popular open positions for a specific institution that are tagged with the specified tags, filtered by the specified criteria, ordered by engagement (applications) and date.
     * @param inst          The Institution.
     * @param tags          The List of Tags.
     * @param filter        The filter criteria to apply to the tags.
     * @param order         The order criteria to sort the positions when applications are equal.
     * @param limit         The maximum number of positions to return.
     * @return              A List of OpenPosition objects (that might be empty), ordered by engagement and date.
     */
    List<OpenPosition> findPopularOpenPositionsForTags(Institution inst, List<Tag> tags, TagFilterCriteria filter, OrderCriteria order, int limit);
    
    /**
     * Delete all questions for an open position.
     * @param position          The OpenPosition. Must not be null.
     */
    void deleteAllQuestionsByPosition(OpenPosition position);
    
    /**
     * Delete an open position.
     * @param position          The OpenPosition to delete. Must not be null.
     */
    void delete(OpenPosition position);
    
    /**
     * Find all active OpenPositions for an organization that have an organigramPosition associated.
     * @param organization      The Organization. Must not be null.
     * @return                  A List of OpenPosition objects (that might be empty).
     */
    List<OpenPosition> findActiveByOrganizationWithOrganigram(Organization organization);
}
