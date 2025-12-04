package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.OpenPositionApplication;
import ar.edu.itba.paw.model.Organigram;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.form.CommentForm;
import ar.edu.itba.paw.webapp.form.RegisterForm;
import ar.edu.itba.paw.webapp.util.DateFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final EventService eventService;
    private final InstitutionService institutionService;
    private final OpenPositionService openPositionService;
    private final PermissionService permissionService;

    @GetMapping(path = "/login")
    @PreAuthorize("isAnonymous()")
    public ModelAndView loginGET() {
        return new ModelAndView("user/login");
    }

    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerGET() {
        ModelAndView mav = new ModelAndView("user/register");
        mav.addObject("registerForm", new RegisterForm());
        return mav;
    }

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerPOST(
            @Valid @ModelAttribute final RegisterForm form,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            ModelAndView mav = new ModelAndView("user/register");
            mav.addObject("registerForm", form);
            return mav;
        }

        LOGGER.info("Attempting to register user {} ({} {}).", form.getEmail(), form.getFirstName(), form.getLastName());
        try {
            userService.create(form.getEmail(), form.getFirstName(), form.getLastName(), form.getPassword());
        } catch (RuntimeException e) {
            LOGGER.error("Error registering user {}: .", form.getEmail(), e);
            throw e;
        }
        LOGGER.info("User {} created.", form.getEmail());

        /* Auto-Login */
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                form.getEmail(),
                form.getPassword()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(token));

        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView profileGET(
            @ModelAttribute("currentUser") Optional<User> currentUser,
            HttpServletRequest request
    ) {
        final ModelAndView mav = new ModelAndView("user/profile");
        currentUser.ifPresent(u -> {
            /* User Info */
            mav.addObject("isInstitutionAdmin", permissionService.isInstitutionAdministrator(u));
            mav.addObject("userCanManageEvents", false);
            mav.addObject("userFirstName", u.getFirstName());
            mav.addObject("userLastName", u.getLastName());
            mav.addObject("userEmail", u.getUserEmail());
            mav.addObject("commentForm", new CommentForm());
            mav.addObject("institutionName", u.getUserID() == 0 ?
                    "OrgHub" : institutionService.findByID(u.getInstitution().getInstitutionID()).getInstitutionName()
            );
            
            // Check if user is system administrator
            boolean isSystemAdmin = permissionService.isSystemAdministrator(u);
            mav.addObject("isSystemAdmin", isSystemAdmin);
            
            // Set user role for header
            String userRole = isSystemAdmin ? "SYSTEM_ADMIN" : "USER";
            mav.addObject("userRole", userRole);

            /* User events and liked posts */
            mav.addObject("enrolledEvents", eventService.getAllEventsUserEnrolled(u.getUserID()));
            List<Post> likedPosts = userService.findLikedPostsByUser(u.getUserID());
            List<Long> likedPostsIDs = likedPosts.stream().map(Post::getPostID).toList();
            mav.addObject("likedPostsIDs", likedPostsIDs );
            mav.addObject("likedPosts", likedPosts);

            List<OpenPositionApplication> applications = openPositionService.getUserApplications(u.getUserID());
            mav.addObject("applications", applications);
            
            List<OpenPosition> applicationPositions = new ArrayList<>();
            for (OpenPositionApplication application : applications) {
                applicationPositions.add(openPositionService.findByID(application.getOpenPosition().getPositionID()));
            }
            mav.addObject("applicationPositions", applicationPositions);
            
            List<Organigram> userPositions = userService.getUserPositions(u.getUserID());
            mav.addObject("userPositions", userPositions);

            mav.addObject("dateFormatter", DateFormatterUtil.createDateFormatter(request));
        });
        return mav;
    }
}
