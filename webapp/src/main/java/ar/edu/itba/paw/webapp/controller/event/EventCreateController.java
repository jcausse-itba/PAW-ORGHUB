package ar.edu.itba.paw.webapp.controller.event;

import ar.edu.itba.paw.interfaces.service.EventService;
import ar.edu.itba.paw.interfaces.service.ImageService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.model.Tag;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.image.Image;
import ar.edu.itba.paw.webapp.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/org/{orgID}/event")
public class EventCreateController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventCreateController.class);

    private final EventService eventService;
    private final TagService tagService;
    private final ImageService imageService;

    @GetMapping("/create")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView eventCreateGET(
            @PathVariable("orgID") final long orgID,
            @ModelAttribute("currentUser") Optional < User > currentUser
    ) {
        ModelAndView mav = new ModelAndView("event/create");
        mav.addObject("eventForm", new EventForm());
        mav.addObject("orgID", orgID);
        List <Tag> allTags = tagService.findAll();
        mav.addObject("allTags", allTags);
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        return mav;
    }

    @PostMapping("/create")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView eventCreatePOST(
            @PathVariable("orgID") final long orgID,
            @Valid @ModelAttribute final EventForm form,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            ModelAndView mav = new ModelAndView("event/create");
            mav.addObject("orgID", orgID);
            return mav;
        }
        
        LOGGER.info("Creating event '{}' for organization {}.", form.getTitle(), orgID);
        
        Integer imageID = null;
        if (form.getFlyer() != null && !form.getFlyer().isEmpty()) {
            try {
                byte[] imageBytes = form.getFlyer().getBytes();
                Image image = imageService.uploadImage(imageBytes);
                imageID = image.getImageID();
                LOGGER.debug("Image uploaded successfully with ID: {}", imageID);
            } catch (IOException e) {
                LOGGER.error("Error reading image file for event '{}': {}.", form.getTitle(), e.getMessage(), e);
                ModelAndView mav = new ModelAndView("event/create");
                mav.addObject("orgID", orgID);
                List<Tag> allTags = tagService.findAll();
                mav.addObject("allTags", allTags);
                br.rejectValue("flyer", "validation.event.image.error");
                return mav;
            }
        }
        
        eventService.create(orgID, form.getTitle(), form.getDescription(), form.getLocation(), form.getEventDate(), LocalDateTime.now(), form.getTags(), imageID);
        LOGGER.info("Event '{}' created successfully for organization {}.", form.getTitle(), orgID);
        return new ModelAndView("redirect:/org/" + orgID + "/profile/");
    }
}
