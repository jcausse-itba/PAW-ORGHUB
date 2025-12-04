package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.PostService;
import ar.edu.itba.paw.interfaces.service.PermissionService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.Organigram;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.form.CommentForm;
import ar.edu.itba.paw.webapp.util.DateFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostDetailViewBuilder {

    private final PostService postService;
    private final UserService userService;
    private final PermissionService permissionService;

    public ModelAndView build(long postID, Optional <User> currentUser, CommentForm commentForm, BindingResult br, HttpServletRequest request, int commentPage) {
        Post post = postService.findByID(postID);

        int sanitizedCommentPage = Math.max(commentPage, 1);
        int commentPageIndex = sanitizedCommentPage - 1;

        List <Comment> comments = postService.findAllCommentsByID(postID, commentPageIndex);
        boolean hasNextComments = !postService.findAllCommentsByID(postID, commentPageIndex + 1).isEmpty();
        boolean hasPreviousComments = sanitizedCommentPage > 1;
        Map <Long, User> users = new HashMap <>();
        Map <Long, List<Organigram>> userPositions = new HashMap <>();
        
        for (Comment comment : comments) {
            User user = comment.getUser();
            if (user != null) {
                users.put(user.getUserID(), user);
                List<Organigram> positionsInOrg = userService.getUserPositionsInOrganization(user.getUserID(), post.getOrganization().getOrgID());
                userPositions.put(user.getUserID(), positionsInOrg);
            }
        }

        ModelAndView mav = new ModelAndView("post/detail");
        mav.addObject("post", post);
        mav.addObject("comments", comments);
        mav.addObject("users", users);
        mav.addObject("userPositions", userPositions);
        mav.addObject("dateFormatter", DateFormatterUtil.createDateFormatter(request));
        mav.addObject("commentCurrentPage", sanitizedCommentPage);
        mav.addObject("hasPreviousComments", hasPreviousComments);
        mav.addObject("hasNextComments", hasNextComments);

        if (commentForm != null) {
            mav.addObject("commentForm", commentForm);
            mav.addObject("org.springframework.validation.BindingResult.commentForm", br);
        } else {
            mav.addObject("commentForm", new CommentForm());
        }

        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));

        boolean liked = false;
        boolean canDeleteComments = false;
        boolean canPublish = false;
        if (currentUser.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isInstitutionAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_INSTITUTION_ADMIN"));
            mav.addObject("isInstitutionAdmin", isInstitutionAdmin);
            liked = postService.isPostLikedByUser(currentUser.get().getUserID(), postID);
            canDeleteComments = permissionService.canManageOrganization(currentUser.get(), post.getOrganization().getOrgID());
            canPublish = permissionService.canPublish(currentUser.get(), post.getOrganization().getOrgID());
        }
        mav.addObject("liked", liked);
        mav.addObject("canDeleteComments", canDeleteComments);
        mav.addObject("canPublish", canPublish);

        return mav;
    }
}
