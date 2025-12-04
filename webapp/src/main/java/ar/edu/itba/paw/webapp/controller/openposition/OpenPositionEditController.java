package ar.edu.itba.paw.webapp.controller.openposition;

import ar.edu.itba.paw.interfaces.service.OpenPositionService;
import ar.edu.itba.paw.interfaces.service.OrganigramService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.enums.OpenPositionStatus;
import ar.edu.itba.paw.model.Organigram;
import ar.edu.itba.paw.model.Tag;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.form.OpenPositionForm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class OpenPositionEditController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenPositionEditController.class);

    private final OpenPositionService openPositionService;
    private final TagService tagService;
    private final OrganigramService organigramService;

    @GetMapping("/org/{orgID}/open-position/{positionID}/edit")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView openPositionEditGET(
            @PathVariable("orgID") final long orgID,
            @PathVariable("positionID") final long positionID,
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        OpenPosition position = openPositionService.findByID(positionID);
        
        ModelAndView mav = new ModelAndView("open-position/edit");
        OpenPositionForm form = new OpenPositionForm();
        form.setTitle(position.getTitle());
        form.setDescription(position.getDescription());
        form.setRequirements(position.getRequirements());
        form.setWorkhours(position.getWorkHours());
        form.setTags(position.getTags().stream().map(Tag::getTagID).toList());
        form.setOrganigramPositionID(position.getOrganigramPosition() != null ? position.getOrganigramPosition().getOrganigramID() : null);
        
        mav.addObject("openPositionForm", form);
        mav.addObject("positionID", positionID);
        mav.addObject("orgID", orgID);
        mav.addObject("positionStatus", position.getStatus());
        List<Tag> allTags = tagService.findAll();
        mav.addObject("allTags", allTags);
        
        List<Organigram> positionsForPicker = new ArrayList<>();
        
        if (position.getStatus() != OpenPositionStatus.CLOSED) {
            List<Organigram> vacantPositions = organigramService.getVacantPositions(orgID);
            positionsForPicker.addAll(vacantPositions);
        }
        
        if (position.getOrganigramPosition() != null) {
            Organigram currentPosition = position.getOrganigramPosition();
            boolean alreadyInList = positionsForPicker.stream()
                .anyMatch(p -> p.getOrganigramID().equals(currentPosition.getOrganigramID()));
            if (!alreadyInList) {
                positionsForPicker.add(currentPosition);
            }
        }
        
        positionsForPicker.sort(Comparator
            .comparing(Organigram::getLevel)
            .thenComparing(Organigram::getSortOrder));
        
        mav.addObject("vacantOrganigramPositions", positionsForPicker);
        
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        return mav;
    }

    @PostMapping("/org/{orgID}/open-position/{positionID}/edit")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView openPositionEditPOST(
            @PathVariable("orgID") final long orgID,
            @PathVariable("positionID") final long positionID,
            @Valid @ModelAttribute final OpenPositionForm form,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            ModelAndView mav = new ModelAndView("open-position/edit");
            mav.addObject("positionID", positionID);
            mav.addObject("orgID", orgID);
            OpenPosition position = openPositionService.findByID(positionID);
            mav.addObject("positionStatus", position.getStatus());
            List<Tag> allTags = tagService.findAll();
            mav.addObject("allTags", allTags);
            
            List<Organigram> positionsForPicker = new ArrayList<>();
            
            if (position.getStatus() != OpenPositionStatus.CLOSED) {
                List<Organigram> vacantPositions = organigramService.getVacantPositions(orgID);
                positionsForPicker.addAll(vacantPositions);
            }
            
            if (position.getOrganigramPosition() != null) {
                Organigram currentPosition = position.getOrganigramPosition();
                boolean alreadyInList = positionsForPicker.stream()
                    .anyMatch(p -> p.getOrganigramID().equals(currentPosition.getOrganigramID()));
                if (!alreadyInList) {
                    positionsForPicker.add(currentPosition);
                }
            }
            
            positionsForPicker.sort(Comparator
                .comparing(Organigram::getLevel)
                .thenComparing(Organigram::getSortOrder));
            
            mav.addObject("vacantOrganigramPositions", positionsForPicker);
            return mav;
        }

        LOGGER.info("Updating open position '{}' (ID: {}) for organization {}.", form.getTitle(), positionID, orgID);
        
        try {
            OpenPosition position = openPositionService.findByID(positionID);
            Long organigramPositionID;
            if (position.getStatus() != OpenPositionStatus.CLOSED) {
                organigramPositionID = form.getOrganigramPositionID();
            } else {
                organigramPositionID = position.getOrganigramPosition() != null ? position.getOrganigramPosition().getOrganigramID() : null;
            }
            
            openPositionService.update(positionID, form.getTitle(), form.getDescription(), form.getRequirements(), form.getWorkhours(), organigramPositionID, form.getTags());
            LOGGER.info("Open position '{}' updated successfully.", form.getTitle());
            return new ModelAndView("redirect:/org/" + orgID + "/open-position/" + positionID);
        } catch (RuntimeException e) {
            LOGGER.error("Error updating open position '{}' (ID: {}): {}.", form.getTitle(), positionID, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/org/{orgID}/open-position/{positionID}/delete")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView openPositionDeletePOST(
            @PathVariable("orgID") final long orgID,
            @PathVariable("positionID") final long positionID,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        LOGGER.info("Deleting open position {} for organization {}.", positionID, orgID);
        
        try {
            openPositionService.delete(positionID);
            LOGGER.info("Open position {} deleted successfully.", positionID);
            if (referer != null && !referer.isEmpty() && !referer.contains("/open-position/" + positionID)) {
                return new ModelAndView("redirect:" + referer);
            }
            return new ModelAndView("redirect:/org/" + orgID + "/profile");
        } catch (RuntimeException e) {
            LOGGER.error("Error deleting open position {}: {}.", positionID, e.getMessage(), e);
            throw e;
        }
    }
}

