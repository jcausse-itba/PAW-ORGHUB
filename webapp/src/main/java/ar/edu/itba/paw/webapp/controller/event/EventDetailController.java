package ar.edu.itba.paw.webapp.controller.event;

import ar.edu.itba.paw.interfaces.service.EventService;
import ar.edu.itba.paw.interfaces.service.PermissionService;
import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.util.DateFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EventDetailController {

    private final EventService eventService;
    private final PermissionService permissionService;

    @GetMapping("/event/{eventID}")
    @PreAuthorize("@webPermissionService.canAccessEvent(authentication, #eventID)")
    public ModelAndView showEventDetailGET(
            @PathVariable("eventID") long eventID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            HttpServletRequest request
    ) {
        final User user = currentUser.orElseThrow(() -> new IllegalArgumentException("Could not get current user"));
        ModelAndView mav = new ModelAndView("event/details");

        Event event = eventService.findByID(eventID);
        DateTimeFormatter formatter = DateFormatterUtil.createDateFormatter(request);

        mav.addObject("event", event);
        mav.addObject("location", event.getLocation());
        mav.addObject("eventDate", event.getEventDate().format(formatter));
        mav.addObject("dateFormatter", formatter);
        mav.addObject("userFirstName", user.getFirstName());
        mav.addObject("userCanManageEvents", permissionService.canManageEvents(user, eventService.getOrganizationIDByID(eventID)));
        mav.addObject("canPublish", permissionService.canPublish(user, eventService.getOrganizationIDByID(eventID)));
        eventService.getEnrollmentStatus(eventID, currentUser.get().getUserID()).ifPresent(s ->
            mav.addObject("status", s.name())
        );

        return mav;
    }

    @PostMapping("/event/{eventID}/enroll")
    @PreAuthorize("@webPermissionService.canEnroll(authentication, #eventID)")
    public ModelAndView enrollEventPOST(
            @PathVariable("eventID") long eventID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        final User user = currentUser.orElseThrow(() -> new IllegalArgumentException("Could not get current user"));
        eventService.enrollUser(eventID, user.getUserID());
        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer);
        }
        return new ModelAndView("redirect:/home");
    }

    @PostMapping("/event/{eventID}/unenroll")
    @PreAuthorize("@webPermissionService.canUnenroll(authentication, #eventID)")
    public ModelAndView unenrollEventPOST(
            @PathVariable("eventID") long eventID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        final User user = currentUser.orElseThrow(() -> new IllegalArgumentException("Could not get current user"));
        eventService.unenrollUser(eventID, user.getUserID());
        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer);
        }
        return new ModelAndView("redirect:/home");
    }
}
