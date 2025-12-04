package ar.edu.itba.paw.webapp.controller.post;

import ar.edu.itba.paw.interfaces.service.PostService;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.controller.PostDetailViewBuilder;
import ar.edu.itba.paw.webapp.form.CommentForm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class PostCommentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostCommentController.class);

    private final PostService postService;
    private final PostDetailViewBuilder builder;

    @PostMapping("/comments/create")
    @PreAuthorize("isAuthenticated() and @webPermissionService.canAccessPost(authentication, #commentForm.postID)")
    public ModelAndView createCommentPOST(
            @Valid @ModelAttribute("commentForm") final CommentForm commentForm,
            BindingResult br,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            @RequestHeader(value = "Referer", required = false) String referer,
            HttpServletRequest request
    ) {
        int commentPage = parsePage(request.getParameter("commentPage"));

        if (br.hasErrors()) {
            return builder.build(commentForm.getPostID(), currentUser, commentForm, br, request, commentPage);
        }

        final User user = currentUser.orElseThrow(() -> new IllegalArgumentException("Could not get current user"));
        LOGGER.debug("User {} creating comment for post {}.", user.getUserEmail(), commentForm.getPostID());
        postService.createComment(user.getUserID(), commentForm.getPostID(), commentForm.getContent());
        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer);
        }
        return new ModelAndView("redirect:/post/" + commentForm.getPostID() + "?commentPage=" + commentPage);
    }

    @PostMapping("/comments/delete")
    @PreAuthorize("@webPermissionService.canManageOrganization(authentication, #orgID)")
    public ModelAndView deleteComment(
            @RequestParam("commentID") final long commentID,
            @RequestParam("postID") final long postID,
            @SuppressWarnings("unused") @RequestParam("orgID") final long orgID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            @RequestHeader(value = "Referer", required = false) String referer
    ) {
        if (currentUser.isEmpty()) {
            LOGGER.warn("Unauthenticated user attempted to delete comment {}.", commentID);
            return new ModelAndView("redirect:/login");
        }

        LOGGER.info("User {} deleting comment {} from post {}.", currentUser.get().getUserEmail(), commentID, postID);
        postService.deleteComment(commentID);
        if (referer != null && !referer.isEmpty()) {
            return new ModelAndView("redirect:" + referer);
        }
        return new ModelAndView("redirect:/post/" + postID);
    }

    private int parsePage(final String value) {
        if (value == null) {
            return 1;
        }
        try {
            int page = Integer.parseInt(value);
            return Math.max(page, 1);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
