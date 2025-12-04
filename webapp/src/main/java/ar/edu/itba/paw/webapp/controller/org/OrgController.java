package ar.edu.itba.paw.webapp.controller.org;

import ar.edu.itba.paw.interfaces.service.EventService;
import ar.edu.itba.paw.interfaces.service.OrganizationService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.EventEnrollment;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.webapp.util.DateFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class OrgController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrgController.class);

    private final OrganizationService organizationService;
    private final EventService eventService;
    private final UserService userService;

    @GetMapping("/org")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView orgListAllGET(@ModelAttribute("currentUser") Optional<User> currentUser) {
        LOGGER.debug("Organization list page accessed. User: {}.", 
                currentUser.map(User::getUserEmail).orElse("anonymous"));
        
        ModelAndView mav = new ModelAndView("org/organizations");

        List<Organization> organizations = List.of();
        if (currentUser.isPresent()) {
            organizations = organizationService.findAllForInstitution(currentUser.get().getInstitution().getInstitutionID(), 0);
            LOGGER.debug("Found {} organizations for user {} (institution: {}).", 
                    organizations.size(), currentUser.get().getUserEmail(), currentUser.get().getInstitution().getInstitutionID());
        }

        mav.addObject("organizations", organizations);
        mav.addObject("userService", userService);
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        return mav;
    }

    @PostMapping("/org/{orgID}/subscribe")
    @PreAuthorize("@webPermissionService.canAccessOrganization(authentication, #orgID)")
    public String subscribeToOrganizationPOST(
            @PathVariable("orgID") long orgID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        currentUser.ifPresent(user -> {
            LOGGER.info("User {} subscribing to organization {}.", user.getUserEmail(), orgID);
            userService.addSubscription(user.getUserID(), orgID);
        });
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/org";
    }

    @PostMapping("/org/{orgID}/unsubscribe")
    @PreAuthorize("@webPermissionService.canAccessOrganization(authentication, #orgID)")
    public String unsubscribeFromOrganizationPOST(
            @PathVariable("orgID") long orgID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        currentUser.ifPresent(user -> {
            LOGGER.info("User {} unsubscribing from organization {}.", user.getUserEmail(), orgID);
            userService.removeSubscription(user.getUserID(), orgID);
        });
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/org";
    }

    @GetMapping("/org/{orgID}/event/{eventID}/manage")
    @PreAuthorize("@webPermissionService.canManageEvents(authentication, #orgID)")
    public ModelAndView manageEventEnrollmentsGET(
            @PathVariable("eventID") long eventID,
            @PathVariable("orgID") long orgID,
            @RequestParam(name = "status", defaultValue = "all") String statusFilter,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            HttpServletRequest request
    ) {
        Event event = eventService.findByID(eventID);
        DateTimeFormatter formatter = DateFormatterUtil.createDateFormatter(request);

        Map<Status, Long> enrollmentCounts = eventService.getEnrollmentCountsByStatus(eventID);
        
        List<EventEnrollment> enrollments;
        if ("all".equals(statusFilter)) {
            enrollments = eventService.getEnrollments(eventID);
        } else {
            Status filterStatus = Status.valueOf(statusFilter.toUpperCase());
            enrollments = eventService.getEnrollmentsByStatus(eventID, filterStatus);
        }

        long pendingCount = enrollmentCounts.getOrDefault(Status.PENDING, 0L);
        long acceptedCount = enrollmentCounts.getOrDefault(Status.ACCEPTED, 0L);
        long rejectedCount = enrollmentCounts.getOrDefault(Status.REJECTED, 0L);
        long totalCount = pendingCount + acceptedCount + rejectedCount;

        ModelAndView mav = new ModelAndView("event/manage");
        mav.addObject("orgID", orgID);
        mav.addObject("eventID", eventID);
        mav.addObject("event", event);
        mav.addObject("dateFormatter", formatter);
        mav.addObject("enrollments", enrollments);
        mav.addObject("statusFilter", statusFilter);
        mav.addObject("pendingCount", pendingCount);
        mav.addObject("acceptedCount", acceptedCount);
        mav.addObject("rejectedCount", rejectedCount);
        mav.addObject("totalCount", totalCount);
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        return mav;
    }

    @PostMapping("/org/{orgID}/event/{eventID}/manage/update")
    @PreAuthorize("@webPermissionService.canManageEvents(authentication, #orgID)")
    public String updateEnrollmentStatusPOST(
            @PathVariable("eventID") long eventID,
            @PathVariable("orgID") long orgID,
            @RequestParam long userID,
            @RequestParam Status status,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        LOGGER.info("Updating enrollment status for user {} in event {} to {}.", userID, eventID, status);
        eventService.updateEnrollmentStatus(eventID, userID, status);
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return String.format("redirect:/org/%d/event/%d/manage", orgID, eventID);
    }
}
