// DONE

package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.persistence.EventEnrollmentDao;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.model.EventEnrollment;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.annotation.TransactionalRead;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    private static final int REMINDER_HOURS_BEFORE = 24;
    private static final int REMINDER_WINDOW_MINUTES = 30;

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final Environment env;
    private final EventEnrollmentDao eventEnrollmentDao;

    @Autowired
    public EmailServiceImpl(
            JavaMailSender mailSender,
            SpringTemplateEngine templateEngine,
            Environment env,
            EventEnrollmentDao eventEnrollmentDao) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.env = env;
        this.eventEnrollmentDao = eventEnrollmentDao;
    }

    private void sendTemplateEmail(
        final String to,
        final String subject,
        final String templateName,
        final Map<String, Object> templateModel
    ) {
        if (to == null || to.isEmpty() || subject == null || templateName == null || templateModel == null) {
            LOGGER.error("Invalid arguments passed to sendTemplateEmail. Email not sent.");
            if (to != null && to.isEmpty()) {
                LOGGER.error("Empty recipient email.");
            }
            return;
        }
        try {
            Context context = new Context(LocaleContextHolder.getLocale());
            context.setVariables(templateModel);
            String htmlBody = templateEngine.process(templateName, context);
            sendHtmlMessage(to, subject, htmlBody);
            LOGGER.info("Template email '{}' sent successfully to: {}", templateName, to);
        } catch (Exception e) {
            LOGGER.error("Failed to send template email to: {}. Error: {}", to, e.getMessage());
        }
    }

    private void sendHtmlMessage(final String to, final String subject, final String htmlBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send HTML email to: {}. Error: {}", to, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendCreationEmail(final String toEmail, final String organizationName, final long organizationID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String organizationUrl = baseUrl + "/org/" + organizationID + "/profile";

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", organizationName);
        templateModel.put("organizationUrl", organizationUrl);

        String subject = "¡Bienvenido a nuestra plataforma!";
        sendTemplateEmail(toEmail, subject, "creation", templateModel);
    }

    @Async
    @Override
    public void sendCommentNotificationEmail(final String toEmail, final String postTitle, final String commentContent, final long postID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String commentUrl = baseUrl + "/post/" + postID;

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("postTitle", postTitle);
        templateModel.put("commentContent", commentContent);
        templateModel.put("commentUrl", commentUrl);

        String subject = "¡Nuevo comentario en tu publicación!";
        sendTemplateEmail(toEmail, subject, "comment_notification", templateModel);
    }

    @Async
    @Override
    public void sendSubscriptionNotificationEmail(final String toEmail, final String userEmail, final String orgName, final long orgID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String organizationUrl = baseUrl + "/org/" + orgID + "/profile";
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", orgName);
        templateModel.put("userEmail", userEmail);
        templateModel.put("organizationUrl", organizationUrl);

        String subject = "¡Nueva suscripción!";
        sendTemplateEmail(toEmail, subject, "subscription_notification", templateModel);
    }

    @Async
    @Override
    public void sendPostNotificationEmail(final String toEmail, final String orgName, final String postTitle, final String postBody, final long postID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String postUrl = baseUrl + "/post/" + postID;

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", orgName);
        templateModel.put("postTitle", postTitle);
        templateModel.put("postBody", postBody);
        templateModel.put("postUrl", postUrl);

        String subject = "¡Nueva publicación de " + orgName + "!";
        sendTemplateEmail(toEmail, subject, "post_notification", templateModel);
    }

    @Async
    @Override
    public void sendEventNotificationEmail(final String toEmail, final String orgName, final String eventTitle, final String eventDescription, final String eventLocation, final long eventID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String eventUrl = baseUrl + "/event/" + eventID;

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", orgName);
        templateModel.put("eventTitle", eventTitle);
        templateModel.put("eventDescription", eventDescription);
        templateModel.put("eventLocation", eventLocation);
        templateModel.put("eventUrl", eventUrl);

        String subject = "¡Nuevo evento de " + orgName + "!";
        sendTemplateEmail(toEmail, subject, "event_notification", templateModel);
    }

    @Async
    @Override
    public void sendOpenPositionNotificationEmail(final String toEmail, final String orgName, final String positionTitle, final String positionDescription, final long positionID, final long orgID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String positionUrl = baseUrl + "/org/" + orgID + "/open-position/" + positionID;

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", orgName);
        templateModel.put("positionTitle", positionTitle);
        templateModel.put("positionDescription", positionDescription);
        templateModel.put("positionUrl", positionUrl);

        String subject = "¡Nueva posición abierta de " + orgName + "!";
        sendTemplateEmail(toEmail, subject, "open_position_notification", templateModel);
    }

    @Async
    @Override
    public void sendApplicationNotificationEmail(final String toEmail, final String orgName, final String applicantName, final String positionTitle, final long positionID, final long orgID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String manageUrl = baseUrl + "/org/" + orgID + "/open-position/" + positionID + "/manage";

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", orgName);
        templateModel.put("applicantName", applicantName);
        templateModel.put("positionTitle", positionTitle);
        templateModel.put("manageUrl", manageUrl);

        String subject = "¡Nueva postulación a " + positionTitle + "!";
        sendTemplateEmail(toEmail, subject, "application_notification", templateModel);
    }

    @Async
    @Override
    public void sendEnrollmentNotificationEmail(final String toEmail, final String orgName, final String applicantName, final String eventTitle, final long eventID, final long orgID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String manageUrl = baseUrl + "/org/" + orgID + "/event/" + eventID + "/manage";

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", orgName);
        templateModel.put("applicantName", applicantName);
        templateModel.put("eventTitle", eventTitle);
        templateModel.put("manageUrl", manageUrl);

        String subject = "¡Nueva inscripción a " + eventTitle + "!";
        sendTemplateEmail(toEmail, subject, "enrollment_notification", templateModel);
    }

    @Async
    @Override
    public void sendApplicationStatusNotificationEmail(final String toEmail, final String orgName, final String positionTitle, final String status, final long positionID, final long orgID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String positionUrl = baseUrl + "/org/" + orgID + "/open-position/" + positionID;

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", orgName);
        templateModel.put("positionTitle", positionTitle);
        templateModel.put("status", status);
        templateModel.put("positionUrl", positionUrl);

        String subject = "Actualización de tu postulación a " + positionTitle;
        sendTemplateEmail(toEmail, subject, "application_status_notification", templateModel);
    }

    @Async
    @Override
    public void sendEnrollmentStatusNotificationEmail(final String toEmail, final String orgName, final String eventTitle, final String status, final long eventID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String eventUrl = baseUrl + "/event/" + eventID;

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", orgName);
        templateModel.put("eventTitle", eventTitle);
        templateModel.put("status", status);
        templateModel.put("eventUrl", eventUrl);

        String subject = "Actualización de tu inscripción a " + eventTitle;
        sendTemplateEmail(toEmail, subject, "enrollment_status_notification", templateModel);
    }

    @Async
    @Override
    public void sendOpenPositionDeletedNotificationEmail(final String toEmail, final String orgName, final String positionTitle, final String applicationStatus, final long orgID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String orgProfileUrl = baseUrl + "/org/" + orgID + "/profile";

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", orgName);
        templateModel.put("positionTitle", positionTitle);
        templateModel.put("applicationStatus", applicationStatus);
        templateModel.put("orgProfileUrl", orgProfileUrl);

        String subject = "Posición eliminada: " + positionTitle;
        sendTemplateEmail(toEmail, subject, "position_deleted_notification", templateModel);
    }

    @Async
    @Override
    public void sendEventUpdateNotificationEmail(final String toEmail, final String orgName, final String eventTitle, final String eventLocation, final LocalDateTime eventDate, final long eventID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String eventUrl = baseUrl + "/event/" + eventID;

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", orgName);
        templateModel.put("eventTitle", eventTitle);
        templateModel.put("eventLocation", eventLocation);
        templateModel.put("eventDate", eventDate);
        templateModel.put("eventUrl", eventUrl);

        String subject = "Actualización del evento " + eventTitle;
        sendTemplateEmail(toEmail, subject, "event_update_notification", templateModel);
    }

    @Async
    @Override
    public void sendInstitutionCreationEmail(final String toEmail, final String institutionName, final String domainName) {
        String baseUrl = env.getProperty("app.baseUrl");
        String loginUrl = baseUrl + "/login";

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("institutionName", institutionName);
        templateModel.put("domainName", domainName);
        templateModel.put("loginUrl", loginUrl);

        String subject = "¡Tu institución " + institutionName + " ha sido creada exitosamente!";
        sendTemplateEmail(toEmail, subject, "institution_creation", templateModel);
    }

    @Async
    @Override
    public void sendAddedRoleEmail(final String toEmail, final String orgName, final boolean isManager, final long orgID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String organizationUrl = baseUrl + "/org/" + orgID + "/profile";

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", orgName);
        templateModel.put("isManager", isManager);
        templateModel.put("organizationUrl", organizationUrl);

        String subject = "¡Nuevo rol asignado en " + orgName + "!";
        sendTemplateEmail(toEmail, subject, "added_role", templateModel);
    }

    @Async
    @Override
    public void sendEventReminderEmail(final String toEmail, final String orgName, final String eventTitle, final String eventLocation, final LocalDateTime eventDate, final long eventID) {
        String baseUrl = env.getProperty("app.baseUrl");
        String eventUrl = baseUrl + "/event/" + eventID;

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("organizationName", orgName);
        templateModel.put("eventTitle", eventTitle);
        templateModel.put("eventLocation", eventLocation);
        templateModel.put("eventDate", eventDate);
        templateModel.put("eventUrl", eventUrl);

        String subject = "Recordatorio: " + eventTitle + " es pronto";
        sendTemplateEmail(toEmail, subject, "event_reminder", templateModel);
    }


    @Scheduled(cron = "0 0 * * * ?")
    @TransactionalRead
    @Override
    public void sendEventReminders() {
        LOGGER.info("Starting event reminder email sending...");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderStart = now.plusHours(REMINDER_HOURS_BEFORE).minusMinutes(REMINDER_WINDOW_MINUTES);
        LocalDateTime reminderEnd = now.plusHours(REMINDER_HOURS_BEFORE).plusMinutes(REMINDER_WINDOW_MINUTES);
        
        List<EventEnrollment> enrollments = eventEnrollmentDao.getAcceptedEnrollmentsForUpcomingEvents(reminderStart, reminderEnd);
        
        LOGGER.info("Found {} accepted enrollments for events occurring in approximately 24 hours (between {} and {}).", 
            enrollments.size(), reminderStart, reminderEnd);
        
        int remindersSent = 0;
        for (EventEnrollment enrollment : enrollments) {
            User user = enrollment.getUser();
            if (user != null && user.getUserEmail() != null && !user.getUserEmail().isEmpty()) {
                try {
                    String orgName = enrollment.getEvent().getOrganization().getOrgName();
                    String eventTitle = enrollment.getEvent().getTitle();
                    String eventLocation = enrollment.getEvent().getLocation();
                    LocalDateTime eventDate = enrollment.getEvent().getEventDate();
                    long eventID = enrollment.getEvent().getEventID();
                    sendEventReminderEmail(user.getUserEmail(), orgName, eventTitle, eventLocation, eventDate, eventID );
                    remindersSent++;
                    LOGGER.debug("Reminder sent to {} for event '{}' (ID: {})", 
                        user.getUserEmail(), eventTitle, eventID);
                } catch (Exception e) {
                    LOGGER.error("Error sending reminder to {} for event {}: {}", 
                        user.getUserEmail(), enrollment.getEvent().getEventID(), e.getMessage());
                }
            }
        }
        
        LOGGER.info("Event reminder sending completed. {} reminders sent successfully.", remindersSent);
    }
}
