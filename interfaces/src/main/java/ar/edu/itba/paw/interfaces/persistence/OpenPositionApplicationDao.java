// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.OpenPositionApplication;
import ar.edu.itba.paw.model.OpenPositionAnswer;
import ar.edu.itba.paw.model.OpenPositionQuestion;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Status;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OpenPositionApplicationDao {
    /**
     * Create a new application for an open position.
     * @param position      The OpenPosition to apply to. Must not be null.
     * @param user          The User applying. Must not be null.
     * @return              The created OpenPositionApplication.
     */
    OpenPositionApplication apply(OpenPosition position, User user);
    
    /**
     * Update the status of an application.
     * @param application   The OpenPositionApplication to update. Must not be null.
     * @param status        The new Status.
     */
    void updateStatus(OpenPositionApplication application, Status status);
    
    /**
     * Get all applications for an open position.
     * @param position      The OpenPosition. Must not be null.
     * @return              A List of OpenPositionApplication objects (that might be empty).
     */
    List<OpenPositionApplication> getApplications(OpenPosition position);
    
    /**
     * Find an OpenPositionApplication via its ID.
     * @param applicationID     The ID of the application to be found.
     * @return                  An Optional containing an OpenPositionApplication if it was found. An empty Optional otherwise.
     */
    Optional<OpenPositionApplication> getApplication(long applicationID);
    
    /**
     * Get the application status for a user in an open position.
     * @param position      The OpenPosition. Must not be null.
     * @param user          The User. Must not be null.
     * @return              An Optional containing the Status if the user has applied. An empty Optional otherwise.
     */
    Optional<Status> getStatus(OpenPosition position, User user);
    
    /**
     * Add an answer to an application.
     * @param application   The OpenPositionApplication. Must not be null.
     * @param question      The OpenPositionQuestion. Must not be null.
     * @param answer        The answer text. Must not be null.
     */
    void addAnswer(OpenPositionApplication application, OpenPositionQuestion question, String answer);
    
    /**
     * Get all answers for an application.
     * @param application   The OpenPositionApplication. Must not be null.
     * @return              A List of OpenPositionAnswer objects (that might be empty).
     */
    List<OpenPositionAnswer> getAnswers(OpenPositionApplication application);
    
    /**
     * Withdraw (delete) an application.
     * @param position      The OpenPosition. Must not be null.
     * @param user          The User. Must not be null.
     */
    void withdraw(OpenPosition position, User user);
    
    /**
     * Get all applications for a user.
     * @param user          The User. Must not be null.
     * @return              A List of OpenPositionApplication objects (that might be empty).
     */
    List<OpenPositionApplication> getUserApplications(User user);
    
    /**
     * Get a map of position IDs to application statuses for a user.
     * @param user          The User. Must not be null.
     * @return              A Map with position ID as key and Status as value.
     */
    Map<Long, Status> getUserApplicationStatusMap(User user);
    
    /**
     * Get all applications for an open position filtered by status.
     * @param position      The OpenPosition. Must not be null.
     * @param status        The Status to filter by.
     * @return              A List of OpenPositionApplication objects (that might be empty).
     */
    List<OpenPositionApplication> getApplicationsByStatus(OpenPosition position, Status status);
    
    /**
     * Get application counts grouped by status for an open position.
     * @param position      The OpenPosition. Must not be null.
     * @return              A Map with Status as key and count as value.
     */
    Map<Status, Long> getApplicationCountsByStatus(OpenPosition position);
    
    /**
     * Delete all applications for an open position.
     * @param position      The OpenPosition whose applications are to be deleted. Must not be null.
     */
    void deleteAllApplicationsByPosition(OpenPosition position);
}
