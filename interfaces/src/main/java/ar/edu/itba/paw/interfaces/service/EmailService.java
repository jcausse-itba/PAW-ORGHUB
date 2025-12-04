// DONE

package ar.edu.itba.paw.interfaces.service;

import java.time.LocalDateTime;

public interface EmailService {
    /**
     * Sends a welcome email to a newly created organization.
     * @param toEmail            The recipient's email address.
     * @param organizationName   The organization's name.
     * @param organizationID     The organization's ID.
     */
    void sendCreationEmail(String toEmail, String organizationName, long organizationID);

    /**
     * Sends a notification email when a comment is made on a post.
     * @param toEmail          The recipient's email address.
     * @param postTitle        The post's title.
     * @param commentContent   The comment's content.
     * @param postID           The post's ID.
     */
    void sendCommentNotificationEmail(String toEmail, String postTitle, String commentContent, long postID);

    /**
     * Sends a notification email when a user subscribes to an organization.
     * @param toEmail    The recipient's email address (organization).
     * @param userEmail  The email of the user who subscribed.
     * @param orgName    The organization's name.
     * @param orgID      The organization's ID.
     */
    void sendSubscriptionNotificationEmail(String toEmail, String userEmail, String orgName, long orgID);

    /**
     * Sends a notification email when an organization publishes a new post.
     * @param toEmail    The recipient's email address (subscriber).
     * @param orgName    The organization's name.
     * @param postTitle  The post's title.
     * @param postBody   The post's content.
     * @param postID     The post's ID.
     */
    void sendPostNotificationEmail(String toEmail, String orgName, String postTitle, String postBody, long postID);

    /**
     * Sends a notification email when an organization creates a new event.
     * @param toEmail          The recipient's email address (subscriber).
     * @param orgName          The organization's name.
     * @param eventTitle       The event's title.
     * @param eventDescription The event's description.
     * @param eventLocation    The event's location.
     * @param eventID          The event's ID.
     */
    void sendEventNotificationEmail(String toEmail, String orgName, String eventTitle, String eventDescription, String eventLocation, long eventID);

    /**
     * Sends a notification email when an organization opens a new position.
     * @param toEmail              The recipient's email address (subscriber).
     * @param orgName              The organization's name.
     * @param positionTitle        The position's title.
     * @param positionDescription  The position's description.
     * @param positionID           The position's ID.
     * @param orgID                The organization's ID.
     */
    void sendOpenPositionNotificationEmail(String toEmail, String orgName, String positionTitle, String positionDescription, long positionID, long orgID);

    /**
     * Sends a notification email when a user applies to an open position.
     * @param toEmail        The recipient's email address (organization).
     * @param orgName        The organization's name.
     * @param applicantName  The applicant's name.
     * @param positionTitle  The position's title.
     * @param positionID     The position's ID.
     * @param orgID          The organization's ID.
     */
    void sendApplicationNotificationEmail(String toEmail, String orgName, String applicantName, String positionTitle, long positionID, long orgID);

    /**
     * Sends a notification email when a user enrolls in an event.
     * @param toEmail        The recipient's email address (organization).
     * @param orgName        The organization's name.
     * @param applicantName  The name of the user who enrolled.
     * @param eventTitle     The event's title.
     * @param eventID        The event's ID.
     * @param orgID          The organization's ID.
     */
    void sendEnrollmentNotificationEmail(String toEmail, String orgName, String applicantName, String eventTitle, long eventID, long orgID);

    /**
     * Sends a notification email when an application status changes.
     * @param toEmail      The recipient's email address (applicant).
     * @param orgName      The organization's name.
     * @param positionTitle The position's title.
     * @param status       The new application status.
     * @param positionID   The position's ID.
     * @param orgID        The organization's ID.
     */
    void sendApplicationStatusNotificationEmail(String toEmail, String orgName, String positionTitle, String status, long positionID, long orgID);

    /**
     * Sends a notification email when an enrollment status changes.
     * @param toEmail    The recipient's email address (enrolled user).
     * @param orgName    The organization's name.
     * @param eventTitle The event's title.
     * @param status     The new enrollment status.
     * @param eventID    The event's ID.
     */
    void sendEnrollmentStatusNotificationEmail(String toEmail, String orgName, String eventTitle, String status, long eventID);

    /**
     * Sends a notification email when an event is updated (date or location).
     * @param toEmail      The recipient's email address (accepted enrolled user).
     * @param orgName      The organization's name.
     * @param eventTitle   The event's title.
     * @param eventLocation The event's new location.
     * @param eventDate    The event's new date and time.
     * @param eventID      The event's ID.
     */
    void sendEventUpdateNotificationEmail(String toEmail, String orgName, String eventTitle, String eventLocation, LocalDateTime eventDate, long eventID);

    /**
     * Sends a notification email when an open position is deleted.
     * @param toEmail          The recipient's email address (applicant).
     * @param orgName          The organization's name.
     * @param positionTitle    The deleted position's title.
     * @param applicationStatus The user's application status.
     * @param orgID            The organization's ID.
     */
    void sendOpenPositionDeletedNotificationEmail(String toEmail, String orgName, String positionTitle, String applicationStatus, long orgID);

    /**
     * Sends a notification email when a new institution is created.
     * @param toEmail        The recipient's email address.
     * @param institutionName The institution's name.
     * @param domainName     The institution's domain.
     */
    void sendInstitutionCreationEmail(String toEmail, String institutionName, String domainName);

    /**
     * Sends a notification email when a new role is assigned to a user in an organization.
     * @param toEmail    The recipient's email address.
     * @param orgName    The organization's name.
     * @param isManager  Indicates if the assigned role is a manager role.
     * @param orgID      The organization's ID.
     */
    void sendAddedRoleEmail(String toEmail, String orgName, boolean isManager, long orgID);

    /**
     * Sends a reminder email to a user enrolled in an event.
     * @param toEmail      The recipient's email address (enrolled user).
     * @param orgName      The organization's name.
     * @param eventTitle   The event's title.
     * @param eventLocation The event's location.
     * @param eventDate    The event's date and time.
     * @param eventID      The event's ID.
     */
    void sendEventReminderEmail(String toEmail, String orgName, String eventTitle, String eventLocation, LocalDateTime eventDate, long eventID);

    /**
     * Sends reminder emails to users enrolled in events that occur approximately 24 hours later.
     * Runs automatically every hour and searches for events occurring between 23.5 and 24.5 hours from now
     * (a 1-hour window centered on 24 hours) to ensure all events receive reminders while avoiding duplicates.
     * 
     * Example: If an event is tomorrow at 15:18, the reminder will be sent today between 15:00 and 16:00
     * (when the event falls within the 23.5-24.5 hour window).
     */
    void sendEventReminders();
}
