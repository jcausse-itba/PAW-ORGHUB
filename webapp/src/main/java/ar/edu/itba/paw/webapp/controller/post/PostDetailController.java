package ar.edu.itba.paw.webapp.controller.post;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.controller.PostDetailViewBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PostDetailController {

    private final PostDetailViewBuilder builder;

    @GetMapping("/post/{postID}")
    @PreAuthorize("@webPermissionService.canAccessPost(authentication, #postID)")
    public ModelAndView showPostDetailGET(
            @PathVariable("postID") long postID,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            HttpServletRequest request
    ) {
        int commentPage = parsePage(request.getParameter("commentPage"));
        return builder.build(postID, currentUser, null, null, request, commentPage);
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
