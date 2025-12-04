package ar.edu.itba.paw.webapp.controller.org;

import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.webapp.form.CommentForm;
import ar.edu.itba.paw.webapp.util.DateFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class OrgProfileController {
    private final OrganizationService organizationService;
    private final UserService userService;
    private final EventService eventService;
    private final OpenPositionService openPositionService;
    private final InstitutionService institutionService;
    private final PermissionService permissionService;

    @GetMapping("/org/{orgID}/profile")
    @PreAuthorize("isAuthenticated() and @webPermissionService.canAccessOrganization(authentication, #orgID)")
    public ModelAndView orgProfileGET(
            @PathVariable("orgID") final long orgID,
            @RequestParam(name = "postPage", defaultValue = "1") int postPage,
            @RequestParam(name = "eventPage", defaultValue = "1") int eventPage,
            @RequestParam(name = "positionPage", defaultValue = "1") int positionPage,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            HttpServletRequest request
    ) {
        final User user = currentUser.orElseThrow(() -> new IllegalArgumentException("Could not get current user"));

        Organization org = organizationService.findByID(orgID);

        ModelAndView mav = new ModelAndView("org/profile");

        int sanitizedPostPage = Math.max(postPage, 1);
        int postPageIndex = sanitizedPostPage - 1;
        List <Post> posts = organizationService.getPostsByOrgID(orgID, postPageIndex);
        boolean hasNextPostsPage = !organizationService.getPostsByOrgID(orgID, postPageIndex + 1).isEmpty();
        boolean hasPreviousPostsPage = sanitizedPostPage > 1;
        int sanitizedEventPage = Math.max(eventPage, 1);
        int eventPageIndex = sanitizedEventPage - 1;
        List <Event> events = organizationService.getEventsByOrgID(orgID, eventPageIndex);
        boolean hasNextEventsPage = !organizationService.getEventsByOrgID(orgID, eventPageIndex + 1).isEmpty();
        boolean hasPreviousEventsPage = sanitizedEventPage > 1;
        int sanitizedPositionPage = Math.max(positionPage, 1);
        int positionPageIndex = sanitizedPositionPage - 1;
        List<OpenPosition> openPositions = organizationService.getOpenPositionsByOrgID(orgID, positionPageIndex);
        boolean hasNextPositionsPage = !organizationService.getOpenPositionsByOrgID(orgID, positionPageIndex + 1).isEmpty();
        boolean hasPreviousPositionsPage = sanitizedPositionPage > 1;
        DateTimeFormatter formatter = DateFormatterUtil.createDateFormatter(request);
        mav.addObject("org", org);
        mav.addObject("posts", posts);
        mav.addObject("events", events);
        mav.addObject("openPositions", openPositions);
        mav.addObject("dateFormatter", formatter);
        mav.addObject("institutionName", institutionService.findByID(org.getInstitution().getInstitutionID()).getInstitutionName());
        mav.addObject("commentForm", new CommentForm());
        mav.addObject("orgPostCurrentPage", sanitizedPostPage);
        mav.addObject("hasPreviousOrgPostsPage", hasPreviousPostsPage);
        mav.addObject("hasNextOrgPostsPage", hasNextPostsPage);
        mav.addObject("orgEventCurrentPage", sanitizedEventPage);
        mav.addObject("hasPreviousOrgEventsPage", hasPreviousEventsPage);
        mav.addObject("hasNextOrgEventsPage", hasNextEventsPage);
        mav.addObject("orgPositionCurrentPage", sanitizedPositionPage);
        mav.addObject("hasPreviousOrgPositionsPage", hasPreviousPositionsPage);
        mav.addObject("hasNextOrgPositionsPage", hasNextPositionsPage);

        /* User's name */
        mav.addObject("userFirstName", user.getFirstName());

        /* Check if user is system administrator */
        boolean isSystemAdmin = permissionService.isSystemAdministrator(user);
        mav.addObject("isSystemAdmin", isSystemAdmin);

        if (isSystemAdmin) {
            mav.addObject("userIsOrganizationManager", false);
            mav.addObject("userIsOrganizationPublisher", false);
            mav.addObject("userCanPublish", false);
            mav.addObject("userCanManageEvents", false);
            mav.addObject("userCanManageOpenPositions", false);
            mav.addObject("statusMap", Collections.emptyMap());
            mav.addObject("likedPostsIDs", Collections.emptyList());
            mav.addObject("applicationStatusMap", Collections.emptyMap());
            mav.addObject("isSubscribed", false);
        } else {
            /* User's roles */
            mav.addObject("userIsOrganizationManager", permissionService.isOrganizationManager(user, orgID));
            mav.addObject("userIsOrganizationPublisher", permissionService.isOrganizationPublisher(user, orgID));

            /* User's permissions */
            mav.addObject("userCanPublish", permissionService.canPublish(user, orgID));
            mav.addObject("userCanManageEvents", permissionService.canManageEvents(user, orgID));
            mav.addObject("userCanManageOpenPositions", permissionService.canManageOpenPositions(user, orgID));

            /* User's enrolled events and likes */
            mav.addObject("statusMap", eventService.getUserEnrollmentsMap(user.getUserID()));
            List<Post> likedPosts = userService.findLikedPostsByUser(user.getUserID());
            mav.addObject("likedPostsIDs", likedPosts.stream().map(Post::getPostID).toList());

            /* Application to open positions */
            mav.addObject("applicationStatusMap", openPositionService.getUserApplicationStatusMap(user.getUserID()));

            /* User's subscription status */
            mav.addObject("isSubscribed", userService.isSubscribed(user.getUserID(), orgID));
        }
        return mav;
    }
}
