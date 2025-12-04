package ar.edu.itba.paw.webapp.controller.sysadmin;

import ar.edu.itba.paw.interfaces.service.PartnershipRequestService;
import ar.edu.itba.paw.model.PartnershipRequest;
import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.util.DateFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sysadmin/partnership")
@RequiredArgsConstructor
public class SysadminPartnershipController {
    private final PartnershipRequestService partnershipRequestService;

    @GetMapping
    @PreAuthorize("@webPermissionService.isSystemAdministrator(authentication)")
    public ModelAndView managePartnershipRequests(
            @RequestParam(name = "status", defaultValue = "all") String statusFilter,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            HttpServletRequest request
    ) {
        List<PartnershipRequest> requests;
        if ("all".equals(statusFilter)) {
            requests = partnershipRequestService.getAllRequests();
        } else {
            Status status = Status.valueOf(statusFilter.toUpperCase());
            requests = partnershipRequestService.getRequestsByStatus(status);
        }

        List<PartnershipRequest> allRequests = partnershipRequestService.getAllRequests();
        long pendingCount = allRequests.stream().mapToLong(r -> r.getStatus() == Status.PENDING ? 1 : 0).sum();
        long acceptedCount = allRequests.stream().mapToLong(r -> r.getStatus() == Status.ACCEPTED ? 1 : 0).sum();
        long rejectedCount = allRequests.stream().mapToLong(r -> r.getStatus() == Status.REJECTED ? 1 : 0).sum();
        long totalCount = allRequests.size();

        ModelAndView mav = new ModelAndView("sysadmin/partnership_management");
        mav.addObject("requests", requests);
        mav.addObject("statusFilter", statusFilter);
        mav.addObject("pendingCount", pendingCount);
        mav.addObject("acceptedCount", acceptedCount);
        mav.addObject("rejectedCount", rejectedCount);
        mav.addObject("totalCount", totalCount);
        mav.addObject("dateFormatter", DateFormatterUtil.createDateFormatter(request));
        mav.addObject("userRole", "SYSTEM_ADMIN");
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));

        return mav;
    }

    @PostMapping("/{requestID}/approve")
    @PreAuthorize("@webPermissionService.isSystemAdministrator(authentication)")
    public String approveRequest(
            @PathVariable("requestID") long requestID,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        partnershipRequestService.approveRequest(requestID);
        return "redirect:" + (referer != null ? referer : "/sysadmin/partnership_management");
    }

    @PostMapping("/{requestID}/reject")
    @PreAuthorize("@webPermissionService.isSystemAdministrator(authentication)")
    public String rejectRequest(
            @PathVariable("requestID") long requestID,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        partnershipRequestService.rejectRequest(requestID);
        return "redirect:" + (referer != null ? referer : "/sysadmin/partnership_management");
    }
}
