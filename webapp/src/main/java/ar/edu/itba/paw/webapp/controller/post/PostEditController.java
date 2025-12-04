package ar.edu.itba.paw.webapp.controller.post;

import ar.edu.itba.paw.interfaces.service.PostService;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.model.Post;
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
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class PostEditController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostEditController.class);

    private final PostService postService;
    private final TagService tagService;

    @GetMapping("/org/{orgID}/post/{postID}/edit")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView postEditGET(
            @PathVariable("orgID") final long orgID,
            @PathVariable("postID") final long postID,
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        Post post = postService.findByID(postID);
        
        ModelAndView mav = new ModelAndView("post/edit");
        PostForm form = new PostForm();
        form.setTitle(post.getTitle());
        form.setBody(post.getBody());
        form.setTags(post.getTags().stream().map(Tag::getTagID).toList());
        form.setOrgID(orgID);
        
        mav.addObject("postForm", form);
        mav.addObject("postID", postID);
        mav.addObject("orgID", orgID);
        List<Tag> allTags = tagService.findAll();
        mav.addObject("allTags", allTags);
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        return mav;
    }

    @PostMapping("/org/{orgID}/post/{postID}/edit")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView postEditPOST(
            @PathVariable("orgID") final long orgID,
            @PathVariable("postID") final long postID,
            @Valid @ModelAttribute final PostForm form,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            ModelAndView mav = new ModelAndView("post/edit");
            mav.addObject("postID", postID);
            mav.addObject("orgID", orgID);
            List<Tag> allTags = tagService.findAll();
            mav.addObject("allTags", allTags);
            return mav;
        }

        LOGGER.info("Updating post '{}' (ID: {}) for organization {}.", form.getTitle(), postID, orgID);
        
        try {
            postService.update(postID, form.getTitle(), form.getBody(), form.getTags());
            LOGGER.info("Post '{}' updated successfully.", form.getTitle());
            return new ModelAndView("redirect:/post/" + postID);
        } catch (RuntimeException e) {
            LOGGER.error("Error updating post '{}' (ID: {}): {}.", form.getTitle(), postID, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/org/{orgID}/post/{postID}/delete")
    @PreAuthorize("@webPermissionService.canPublish(authentication, #orgID)")
    public ModelAndView postDeletePOST(
            @PathVariable("orgID") final long orgID,
            @PathVariable("postID") final long postID,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        LOGGER.info("Deleting post {} for organization {}.", postID, orgID);
        
        try {
            postService.delete(postID);
            LOGGER.info("Post {} deleted successfully.", postID);
            if (referer != null && !referer.isEmpty() && !referer.contains("/post/" + postID)) {
                return new ModelAndView("redirect:" + referer);
            }
            return new ModelAndView("redirect:/org/" + orgID + "/profile");
        } catch (RuntimeException e) {
            LOGGER.error("Error deleting post {}: {}.", postID, e.getMessage(), e);
            throw e;
        }
    }
}

