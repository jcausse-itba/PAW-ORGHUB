package ar.edu.itba.paw.webapp.controller.openposition;

import ar.edu.itba.paw.interfaces.service.OpenPositionService;
import ar.edu.itba.paw.interfaces.service.OrganigramService;
import ar.edu.itba.paw.interfaces.service.TagService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/org/{orgID}/open-position")
public class OpenPositionCreateController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenPositionCreateController.class);

    private final OpenPositionService openPositionService;
    private final OrganigramService organigramService;
    private final TagService tagService;

    @GetMapping("/create")
    @PreAuthorize("@webPermissionService.canCreateOpenPositions(authentication, #orgID)")
    public ModelAndView openPositionCreateGET(
            @PathVariable("orgID") final long orgID,
            @ModelAttribute("currentUser") Optional < User > currentUser
    ) {
        ModelAndView mav = new ModelAndView("open-position/create");
        mav.addObject("openPositionForm", new OpenPositionForm());
        mav.addObject("orgID", orgID);
        
        List<Organigram> vacantPositions = organigramService.getVacantPositions(orgID);
        mav.addObject("vacantOrganigramPositions", vacantPositions);
        
        List<Tag> allTags = tagService.findAll();
        mav.addObject("allTags", allTags);
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        return mav;
    }

    @PostMapping("/create")
    @PreAuthorize("@webPermissionService.canCreateOpenPositions(authentication, #orgID)")
    public ModelAndView openPositionCreatePOST(
            @PathVariable("orgID") final long orgID,
            @Valid @ModelAttribute final OpenPositionForm form,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            ModelAndView mav = new ModelAndView("open-position/create");
            mav.addObject("openPositionForm", form);
            mav.addObject("orgID", orgID);
            
            List<Organigram> vacantPositions = organigramService.getVacantPositions(orgID);
            mav.addObject("vacantOrganigramPositions", vacantPositions);
            
            List<Tag> allTags = tagService.findAll();
            mav.addObject("allTags", allTags);
            return mav;
        }

        LOGGER.info("Creating open position '{}' for organization {}.", form.getTitle(), orgID);
        
        try {
            openPositionService.create(
                    orgID,
                    form.getTitle(),
                    form.getDescription(),
                    form.getRequirements(),
                    form.getWorkhours(),
                    LocalDateTime.now(),
                    form.getQuestions(),
                    form.getTags(),
                    form.getOrganigramPositionID()
            );
            
            LOGGER.info("Open position '{}' created successfully for organization {}.", form.getTitle(), orgID);
            return new ModelAndView("redirect:/org/" + orgID + "/profile/");
        } catch (RuntimeException e) {
            LOGGER.error("Error creating open position '{}' for organization {}: {}.", form.getTitle(), orgID, e.getMessage(), e);
            throw e;
        }
    }
}
