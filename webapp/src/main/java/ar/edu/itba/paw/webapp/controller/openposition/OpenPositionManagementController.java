package ar.edu.itba.paw.webapp.controller.openposition;

import ar.edu.itba.paw.interfaces.service.OpenPositionService;
import ar.edu.itba.paw.interfaces.service.OrganigramService;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.OpenPositionAnswer;
import ar.edu.itba.paw.model.OpenPositionApplication;
import ar.edu.itba.paw.model.enums.OpenPositionStatus;
import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OpenPositionManagementController {

    private final OpenPositionService openPositionService;
    private final OrganigramService organigramService;

    @GetMapping("/org/{orgID}/open-position/{positionID}/manage")
    @PreAuthorize("@webPermissionService.canManageOpenPositions(authentication, #orgID)")
    public ModelAndView manageApplicationsGET(
            @PathVariable("positionID") long positionID,
            @PathVariable("orgID") long orgID,
            @RequestParam(name = "status", defaultValue = "all") String statusFilter,
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        OpenPosition position = openPositionService.findByID(positionID);

        Map<Status, Long> applicationCounts = openPositionService.getApplicationCountsByStatus(positionID);
        
        List<OpenPositionApplication> applications;
        if ("all".equals(statusFilter)) {
            applications = openPositionService.getApplications(positionID);
        } else {
            Status filterStatus = Status.get(statusFilter.toUpperCase());
            applications = openPositionService.getApplicationsByStatus(positionID, filterStatus);
        }

        long pendingCount = applicationCounts.getOrDefault(Status.PENDING, 0L);
        long acceptedCount = applicationCounts.getOrDefault(Status.ACCEPTED, 0L);
        long rejectedCount = applicationCounts.getOrDefault(Status.REJECTED, 0L);
        long totalCount = pendingCount + acceptedCount + rejectedCount;

        boolean hasQuestions = !openPositionService.getQuestions(positionID).isEmpty();
        
        Map<Long, List<OpenPositionAnswer>> answersMap = new java.util.HashMap<>();
        if (hasQuestions) {
            for (OpenPositionApplication application : applications) {
                List<OpenPositionAnswer> answers = openPositionService.getAnswers(application.getApplicationID());
                if (!answers.isEmpty()) {
                    answersMap.put(application.getApplicationID(), answers);
                }
            }
        }

        ModelAndView mav = new ModelAndView("open-position/manage");
        mav.addObject("position", position);
        mav.addObject("applications", applications);
        mav.addObject("allApplications", openPositionService.getApplications(positionID)); // For "all" count
        mav.addObject("statusFilter", statusFilter);
        mav.addObject("pendingCount", pendingCount);
        mav.addObject("acceptedCount", acceptedCount);
        mav.addObject("rejectedCount", rejectedCount);
        mav.addObject("totalCount", totalCount);
        mav.addObject("orgID", orgID);
        mav.addObject("positionID", positionID);
        mav.addObject("hasQuestions", hasQuestions);
        mav.addObject("answersMap", answersMap);
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));

        return mav;
    }

    @PostMapping("/org/{orgID}/open-position/{positionID}/manage/update")
    @PreAuthorize("@webPermissionService.canManageOpenPositions(authentication, #orgID)")
    public String updateApplicationStatusPOST(
            @PathVariable("positionID") long positionID,
            @PathVariable("orgID") long orgID,
            @RequestParam long applicationID,
            @RequestParam Status status,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        OpenPositionApplication application = openPositionService.getApplication(applicationID).orElse(null);
        boolean wasAccepted = application != null && application.getStatus() == Status.ACCEPTED;

        openPositionService.updateApplicationStatus(applicationID, status);
        
        if (status == Status.ACCEPTED) {
            openPositionService.rejectAllOtherApplications(positionID, applicationID);
            
            OpenPosition position = openPositionService.findByID(positionID);
            if (position.getOrganigramPosition() != null && application != null) {
                organigramService.assignUserToPosition(position.getOrganigramPosition().getOrganigramID(), application.getUser().getUserID());
            }
            
            openPositionService.closePosition(positionID);
        } else if (status == Status.REJECTED) {
            OpenPosition position = openPositionService.findByID(positionID);

            if (wasAccepted && position.getOrganigramPosition() != null) {
                organigramService.unassignUserFromPosition(position.getOrganigramPosition().getOrganigramID());
            }

            if (wasAccepted && position.getStatus() == OpenPositionStatus.CLOSED) {
                openPositionService.reopenPosition(positionID);
            }
        }
        
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return String.format("redirect:/org/%d/open-position/%d/manage", orgID, positionID);
    }
}
