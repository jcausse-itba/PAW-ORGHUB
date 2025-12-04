package ar.edu.itba.paw.webapp.controller.post;

import ar.edu.itba.paw.interfaces.service.PostService;
import ar.edu.itba.paw.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/likes")
public class PostLikeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostLikeController.class);

    private final PostService postService;

    @PostMapping("/toggle")
    @PreAuthorize("@webPermissionService.canAccessPost(authentication, #postID)")
    public ModelAndView toggleLikePOST(
            @RequestParam("postID") long postID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        final User user = currentUser.orElseThrow(() -> new IllegalArgumentException("Could not get current user"));
        LOGGER.debug("User {} toggling like for post {}.", user.getUserEmail(), postID);
        postService.toggleLikePost(postID, user.getUserID());
        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer);
        }
        return new ModelAndView("redirect:/home");
    }
}
