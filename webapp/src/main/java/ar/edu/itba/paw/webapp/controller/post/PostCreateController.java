package ar.edu.itba.paw.webapp.controller.post;

import ar.edu.itba.paw.interfaces.service.PostService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.model.Tag;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.form.PostForm;
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
public class PostCreateController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostCreateController.class);

    private final PostService postService;
    private final TagService tagService;

    @GetMapping("/org/{orgID}/post/create")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView postCreateGET(
            @PathVariable("orgID") final long orgID,
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        ModelAndView mav = new ModelAndView( "post/create" );
        mav.addObject( "postForm", new PostForm() );
        mav.addObject( "orgID", orgID );
        List <Tag> allTags = tagService.findAll();
        mav.addObject("allTags", allTags);
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        return mav;
    }

    @PostMapping("/org/{orgID}/post/create")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView postCreatePOST(
            @PathVariable("orgID") final long orgID,
            @Valid @ModelAttribute final PostForm form,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            ModelAndView mav = new ModelAndView("post/create");
            mav.addObject("orgID", orgID);
            List<Tag> allTags = tagService.findAll();
            mav.addObject("allTags", allTags);
            return mav;
        }

        LOGGER.info("Creating post '{}' for organization {}.", form.getTitle(), orgID);
        
        try {
            postService.create(orgID, form.getTitle(), form.getBody(), LocalDateTime.now(), form.getTags());
            LOGGER.info("Post '{}' created successfully for organization {}", form.getTitle(), orgID);
            return new ModelAndView("redirect:/org/" + orgID + "/profile/");
        } catch (RuntimeException e) {
            LOGGER.error("Error creating post '{}' for organization {}: {}.", form.getTitle(), orgID, e.getMessage(), e);
            throw e;
        }
    }
}
