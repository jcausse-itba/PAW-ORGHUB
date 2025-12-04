package ar.edu.itba.paw.webapp.controller.openposition;

import ar.edu.itba.paw.interfaces.service.OpenPositionService;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.OpenPositionQuestion;
import ar.edu.itba.paw.model.enums.OpenPositionStatus;
import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.form.OpenPositionApplicationForm;
import ar.edu.itba.paw.webapp.util.DateFormatterUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OpenPositionApplicationController {

    private final OpenPositionService openPositionService;

    @GetMapping("/org/{orgID}/open-position/{positionID}/apply")
    @PreAuthorize("@webPermissionService.canApplyOpenPosition(authentication, #orgID, #positionID)")
    public ModelAndView showApplicationFormGET(
            @PathVariable("orgID") long orgID,
            @PathVariable("positionID") long positionID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            HttpServletRequest request
    ) {
        final User user = currentUser.orElseThrow(() -> new IllegalArgumentException("Could not get current user"));

        OpenPosition position = openPositionService.findByID(positionID);
        List<OpenPositionQuestion> questions = openPositionService.getQuestions(positionID);

        Optional<Status> applicationStatus = openPositionService.getApplicationStatus(user.getUserID(), positionID);
        if (applicationStatus.isPresent()) {
            return new ModelAndView("redirect:/org/" + orgID + "/open-position/" + positionID);
        }

        ModelAndView mav = new ModelAndView("open-position/apply");
        mav.addObject("position", position);
        mav.addObject("questions", questions);
        mav.addObject("applicationForm", new OpenPositionApplicationForm());
        mav.addObject("dateFormatter", DateFormatterUtil.createDateFormatter(request));
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        return mav;
    }

    @PostMapping("/org/{orgID}/open-position/{positionID}/apply")
    @PreAuthorize("@webPermissionService.canApplyOpenPosition(authentication, #orgID, #positionID)")
    public ModelAndView applyToPositionPOST(
            @PathVariable("orgID") long orgID,
            @PathVariable("positionID") long positionID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        final User user = currentUser.orElseThrow(() -> new IllegalArgumentException("Could not get current user"));
        Optional<Status> applicationStatus = openPositionService.getApplicationStatus(user.getUserID(), positionID);
        if (applicationStatus.isPresent()) {
            return new ModelAndView("redirect:/org/" + orgID + "/open-position/" + positionID);
        }

        List<OpenPositionQuestion> questions = openPositionService.getQuestions(positionID);
        if (questions.isEmpty()) {
            openPositionService.applyToPosition(user.getUserID(), positionID, null);
        } else {
            return new ModelAndView("redirect:/org/" + orgID + "/open-position/" + positionID + "/apply");
        }

        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer);
        }
        return new ModelAndView("redirect:/org/" + orgID + "/open-position/" + positionID);
    }

    @PostMapping("/org/{orgID}/open-position/{positionID}/apply-form")
    @PreAuthorize("@webPermissionService.canApplyOpenPosition(authentication, #orgID, #positionID)")
    public ModelAndView submitApplicationPOST(
            @PathVariable("orgID") long orgID,
            @PathVariable("positionID") long positionID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            @Valid @ModelAttribute("applicationForm") OpenPositionApplicationForm form,
            BindingResult br,
            HttpServletRequest request
    ) {
        final User user = currentUser.orElseThrow(() -> new IllegalArgumentException("Could not get current user"));
        Optional<Status> applicationStatus = openPositionService.getApplicationStatus(user.getUserID(), positionID);
        if (applicationStatus.isPresent()) {
            return new ModelAndView("redirect:/org/" + orgID + "/open-position/" + positionID);
        }

        if (br.hasErrors()) {
            OpenPosition position = openPositionService.findByID(positionID);
            List<OpenPositionQuestion> questions = openPositionService.getQuestions(positionID);
            
            ModelAndView mav = new ModelAndView("open-position/apply");
            mav.addObject("position", position);
            mav.addObject("questions", questions);
            mav.addObject("dateFormatter", DateFormatterUtil.createDateFormatter(request));
            currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
            return mav;
        }

        openPositionService.applyToPosition(user.getUserID(), positionID, form.getAnswers());
        
        return new ModelAndView("redirect:/org/" + orgID + "/open-position/" + positionID + "?applied=true");
    }

    @PostMapping("/org/{orgID}/open-position/{positionID}/withdraw")
    @PreAuthorize("@webPermissionService.canWithdrawOpenPosition(authentication, #orgID, #positionID)")
    public ModelAndView withdrawApplicationPOST(
            @PathVariable("orgID") long orgID,
            @PathVariable("positionID") long positionID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        final User user = currentUser.orElseThrow(() -> new IllegalArgumentException("Could not get current user"));
        Optional<Status> applicationStatusOpt = openPositionService.getApplicationStatus(user.getUserID(), positionID);
        if (applicationStatusOpt.isEmpty()) {
            return new ModelAndView("redirect:/org/" + orgID + "/open-position/" + positionID);
        }

        Status applicationStatus = applicationStatusOpt.get();
        boolean wasAccepted = applicationStatus == Status.ACCEPTED;
        
        openPositionService.withdrawApplication(user.getUserID(), positionID);
        
        if (wasAccepted) {
            OpenPosition position = openPositionService.findByID(positionID);
            if (position.getStatus() == OpenPositionStatus.CLOSED) {
                openPositionService.reopenPosition(positionID);
            }
        }
        
        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer);
        }
        return new ModelAndView("redirect:/org/" + orgID + "/open-position/" + positionID);
    }

}
