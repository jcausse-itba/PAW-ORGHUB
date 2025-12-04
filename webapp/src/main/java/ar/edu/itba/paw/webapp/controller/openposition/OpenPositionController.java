package ar.edu.itba.paw.webapp.controller.openposition;

import ar.edu.itba.paw.interfaces.service.OpenPositionService;
import ar.edu.itba.paw.interfaces.service.PermissionService;
import ar.edu.itba.paw.model.OpenPosition;
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
public class OpenPositionController {

    private final OpenPositionService openPositionService;
    private final PermissionService permissionService;

    @GetMapping("/org/{orgID}/open-position/{positionID}")
    @PreAuthorize("@webPermissionService.canAccessOpenPosition(authentication, #orgID, #positionID)")
    public ModelAndView showOpenPositionGET(
            @SuppressWarnings("unused ") @PathVariable("orgID") long orgID,
            @PathVariable("positionID") long positionID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            HttpServletRequest request
    ) {
        OpenPosition position = openPositionService.findByID(positionID);
        DateTimeFormatter formatter = DateFormatterUtil.createDateFormatter(request);
        
        ModelAndView mav = new ModelAndView("open-position/details");
        mav.addObject("position", position);
        mav.addObject("dateFormatter", formatter);
        currentUser.ifPresent(u -> {
            mav.addObject("userCanManageOpenPosition", permissionService.canManageOpenPositions(u, orgID));
            mav.addObject("canPublish", permissionService.canPublish(u, orgID));
            mav.addObject("userFirstName", u.getFirstName());
            openPositionService.getApplicationStatus(u.getUserID(), positionID).ifPresent(status ->
                mav.addObject("status", status.name())
            );
        });
        return mav;
    }
}
