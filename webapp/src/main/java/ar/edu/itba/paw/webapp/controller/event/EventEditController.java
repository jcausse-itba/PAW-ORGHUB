package ar.edu.itba.paw.webapp.controller.event;

import ar.edu.itba.paw.interfaces.service.EventService;
import ar.edu.itba.paw.interfaces.service.ImageService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.model.Event;
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
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class EventEditController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventEditController.class);

    private final EventService eventService;
    private final TagService tagService;
    private final ImageService imageService;

    @GetMapping("/org/{orgID}/event/{eventID}/edit")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView eventEditGET(
            @PathVariable("orgID") final long orgID,
            @PathVariable("eventID") final long eventID,
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        Event event = eventService.findByID(eventID);
        
        ModelAndView mav = new ModelAndView("event/edit");
        EventForm form = new EventForm();
        form.setTitle(event.getTitle());
        form.setDescription(event.getDescription());
        form.setLocation(event.getLocation());
        form.setEventDate(event.getEventDate());
        form.setTags(event.getTags().stream().map(Tag::getTagID).toList());
        
        mav.addObject("eventForm", form);
        mav.addObject("eventID", eventID);
        mav.addObject("orgID", orgID);
        mav.addObject("currentImageID", event.getImage() != null ? event.getImage().getImageID() : null);
        List<Tag> allTags = tagService.findAll();
        mav.addObject("allTags", allTags);
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        return mav;
    }

    @PostMapping("/org/{orgID}/event/{eventID}/edit")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView eventEditPOST(
            @PathVariable("orgID") final long orgID,
            @PathVariable("eventID") final long eventID,
            @Valid @ModelAttribute final EventForm form,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            ModelAndView mav = new ModelAndView("event/edit");
            mav.addObject("eventID", eventID);
            mav.addObject("orgID", orgID);
            List<Tag> allTags = tagService.findAll();
            mav.addObject("allTags", allTags);
            return mav;
        }

        LOGGER.info("Updating event '{}' (ID: {}) for organization {}.", form.getTitle(), eventID, orgID);
        
        Integer imageID = null;
        if (form.getFlyer() != null && !form.getFlyer().isEmpty()) {
            try {
                byte[] imageBytes = form.getFlyer().getBytes();
                Image image = imageService.uploadImage(imageBytes);
                imageID = image.getImageID();
                LOGGER.debug("Image uploaded successfully with ID: {}", imageID);
            } catch (IOException e) {
                LOGGER.error("Error reading image file for event '{}': {}.", form.getTitle(), e.getMessage(), e);
                ModelAndView mav = new ModelAndView("event/edit");
                mav.addObject("eventID", eventID);
                mav.addObject("orgID", orgID);
                List<Tag> allTags = tagService.findAll();
                mav.addObject("allTags", allTags);
                br.rejectValue("flyer", "validation.event.image.error");
                return mav;
            }
        }
        
        eventService.update(eventID, form.getTitle(), form.getDescription(), form.getLocation(), form.getEventDate(), form.getTags(), imageID);
        LOGGER.info("Event '{}' updated successfully.", form.getTitle());
        return new ModelAndView("redirect:/event/" + eventID);
    }

    @PostMapping("/org/{orgID}/event/{eventID}/delete")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView eventDeletePOST(
            @PathVariable("orgID") final long orgID,
            @PathVariable("eventID") final long eventID,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        LOGGER.info("Deleting event {} for organization {}.", eventID, orgID);
        
        eventService.delete(eventID);
        LOGGER.info("Event {} deleted successfully.", eventID);
        if (referer != null && !referer.isEmpty() && !referer.contains("/event/" + eventID)) {
            return new ModelAndView("redirect:" + referer);
        }
        return new ModelAndView("redirect:/org/" + orgID + "/profile");
    }
}

