package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.webapp.form.CommentForm;
import ar.edu.itba.paw.webapp.util.DateFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class HomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private final PostService postService;
    private final TagService tagService;
    private final EventService eventService;
    private final OpenPositionService openPositionService;
    private final UserService userService;
    private final PermissionService permissionService;

    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView homeGET(
            @RequestParam(name="order", defaultValue = "newest") String order,
            @RequestParam(name = "tag", required = false) List<Long> tagIDs,
            @RequestParam(name = "match", defaultValue = "any") String match,
            @RequestParam(name = "type", defaultValue = "posts") String type,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "eventPage", defaultValue = "1") int eventPage,
            @RequestParam(name = "positionPage", defaultValue = "1") int positionPage,
            @ModelAttribute("currentUser") Optional<User> currentUser,
            HttpServletRequest request
    ) {
        int sanitizedPage = Math.max(page, 1);
        int sanitizedEventPage = Math.max(eventPage, 1);
        int sanitizedPositionPage = Math.max(positionPage, 1);
        LOGGER.debug("Home page accessed. User: {}, order: {}, type: {}, match: {}, page: {}, eventPage: {}, positionPage: {}.",
                currentUser.map(User::getUserEmail).orElse("anonymous"), order, type, match, sanitizedPage, sanitizedEventPage, sanitizedPositionPage);
        
        ModelAndView mav = new ModelAndView("home");

        List<Post> posts = List.of();
        List<Event> events = List.of();
        List<OpenPosition> openPositions = List.of();
        boolean isSubscribedToOrgs = false;
        boolean hasNextPostsPage = false;
        boolean hasPreviousPostsPage = false;
        boolean hasNextEventsPage = false;
        boolean hasPreviousEventsPage = false;
        boolean hasNextOpenPositionsPage = false;
        boolean hasPreviousOpenPositionsPage = false;
        int pageIndex = sanitizedPage - 1;
        int eventPageIndex = sanitizedEventPage - 1;
        int positionPageIndex = sanitizedPositionPage - 1;
        
        if (currentUser.isPresent()) {
            List<Organization> subscribedOrgs = userService.getSubscribedOrganizations(currentUser.get().getUserID());
            List<Long> subscribedOrgIDs = subscribedOrgs.stream().map(Organization::getOrgID).toList();
            isSubscribedToOrgs = !subscribedOrgIDs.isEmpty();
            
            OrderCriteria orderCriteria = order.equalsIgnoreCase("newest") ? OrderCriteria.NEWEST_FIRST : OrderCriteria.OLDEST_FIRST;
            long institutionID = currentUser.get().getInstitution().getInstitutionID();
            String normalizedMatch = match.toLowerCase();
            if (isSubscribedToOrgs) {
                events = fetchEventsForSubscribed(normalizedMatch, orderCriteria, tagIDs, subscribedOrgIDs, eventPageIndex);
                openPositions = fetchOpenPositionsForSubscribed(normalizedMatch, order.equalsIgnoreCase("newest"), tagIDs, subscribedOrgIDs, positionPageIndex);
                posts = fetchPostsForSubscribed(normalizedMatch, orderCriteria, tagIDs, subscribedOrgIDs, pageIndex);
                hasNextPostsPage = !fetchPostsForSubscribed(normalizedMatch, orderCriteria, tagIDs, subscribedOrgIDs, pageIndex + 1).isEmpty();
                hasPreviousPostsPage = sanitizedPage > 1;
                hasNextEventsPage = !fetchEventsForSubscribed(normalizedMatch, orderCriteria, tagIDs, subscribedOrgIDs, eventPageIndex + 1).isEmpty();
                hasPreviousEventsPage = sanitizedEventPage > 1;
                hasNextOpenPositionsPage = !fetchOpenPositionsForSubscribed(normalizedMatch, order.equalsIgnoreCase("newest"), tagIDs, subscribedOrgIDs, positionPageIndex + 1).isEmpty();
                hasPreviousOpenPositionsPage = sanitizedPositionPage > 1;
            } else {
                events = fetchEventsPopular(normalizedMatch, orderCriteria, tagIDs, institutionID);
                openPositions = fetchOpenPositionsPopular(normalizedMatch, order.equalsIgnoreCase("newest"), tagIDs, institutionID);
                posts = fetchPostsPopular(normalizedMatch, orderCriteria, tagIDs, institutionID);
                sanitizedPage = 1;
                sanitizedEventPage = 1;
                sanitizedPositionPage = 1;
            }
        }

        DateTimeFormatter formatter = DateFormatterUtil.createDateFormatter(request);
        mav.addObject("posts", posts);
        mav.addObject("events", events);
        mav.addObject("openPositions", openPositions);
        mav.addObject("sort", order);
        mav.addObject("dateFormatter", formatter);
        mav.addObject("selectedTags", tagIDs);
        mav.addObject("match", match);
        mav.addObject("type", type);
        mav.addObject("currentPage", sanitizedPage);
        mav.addObject("hasPreviousPage", hasPreviousPostsPage);
        mav.addObject("hasNextPage", hasNextPostsPage);
        mav.addObject("eventCurrentPage", sanitizedEventPage);
        mav.addObject("hasPreviousEventsPage", hasPreviousEventsPage);
        mav.addObject("hasNextEventsPage", hasNextEventsPage);
        mav.addObject("positionCurrentPage", sanitizedPositionPage);
        mav.addObject("hasPreviousPositionsPage", hasPreviousOpenPositionsPage);
        mav.addObject("hasNextPositionsPage", hasNextOpenPositionsPage);
        mav.addObject("isSubscribedToOrgs", isSubscribedToOrgs);
        mav.addObject("tags", tagService.findAll());
        mav.addObject( "commentForm", new CommentForm() );
        currentUser.ifPresent(u -> {
            mav.addObject("userFirstName", u.getFirstName());
            
            // Check if user is system administrator
            boolean isSystemAdmin = permissionService.isSystemAdministrator(u);
            mav.addObject("isSystemAdmin", isSystemAdmin);
            
            // Set user role for header
            String userRole = isSystemAdmin ? "SYSTEM_ADMIN" : "USER";
            mav.addObject("userRole", userRole);
            
            mav.addObject("userCanManageEvents", false);
            mav.addObject("userCanManageOpenPositions", false);
            mav.addObject("statusMap", eventService.getUserEnrollmentsMap(u.getUserID()));
            List<Post> likedPosts = userService.findLikedPostsByUser(u.getUserID());
            mav.addObject("likedPostIDs", likedPosts.stream().map(Post::getPostID).toList());
            mav.addObject("applicationStatusMap", openPositionService.getUserApplicationStatusMap(u.getUserID()));
        });
        return mav;
    }

    private List<Post> fetchPostsPopular(final String match, final OrderCriteria order, final List<Long> tagIDs, final long institutionID) {
        final int limit = 10;
        if (tagIDs == null || tagIDs.isEmpty()) {
            return postService.findPopularPostsForInstitution(institutionID, order, limit);
        } else {
            TagFilterCriteria filter = "all".equalsIgnoreCase(match) ? TagFilterCriteria.ALL : TagFilterCriteria.ANY;
            return postService.findPopularPostsForTags(institutionID, tagIDs, filter, order, limit);
        }
    }

    private List<Event> fetchEventsPopular(final String match, final OrderCriteria order, final List<Long> tagIDs, final long institutionID) {
        if (tagIDs == null || tagIDs.isEmpty()) {
            return eventService.findPopularEventsForInstitution(institutionID, order, 10);
        } else {
            TagFilterCriteria filter = "all".equalsIgnoreCase(match) ? TagFilterCriteria.ALL : TagFilterCriteria.ANY;
            return eventService.findPopularEventsForTags(institutionID, tagIDs, filter, order, 10);
        }
    }

    private List<OpenPosition> fetchOpenPositionsPopular(final String match, final boolean newestFirst, final List<Long> tagIDs, final long institutionID) {
        OrderCriteria order = newestFirst ? OrderCriteria.NEWEST_FIRST : OrderCriteria.OLDEST_FIRST;
        if (tagIDs == null || tagIDs.isEmpty()) {
            return openPositionService.findPopularOpenPositionsForInstitution(institutionID, order, 10);
        } else {
            TagFilterCriteria filter = "all".equalsIgnoreCase(match) ? TagFilterCriteria.ALL : TagFilterCriteria.ANY;
            return openPositionService.findPopularOpenPositionsForTags(institutionID, tagIDs, filter, order, 10);
        }
    }

    private List<Post> fetchPostsForSubscribed(final String match, final OrderCriteria order, final List<Long> tagIDs, final List<Long> subscribedOrgIDs, final int pageNum) {
        if (tagIDs == null || tagIDs.isEmpty()) {
            return postService.findAllForOrganizations(subscribedOrgIDs, order, pageNum);
        } else {
            TagFilterCriteria filter = "all".equalsIgnoreCase(match) ? TagFilterCriteria.ALL : TagFilterCriteria.ANY;
            return postService.findAllForOrganizationsAndTags(subscribedOrgIDs, tagIDs, filter, order, pageNum);
        }
    }

    private List<Event> fetchEventsForSubscribed(final String match, final OrderCriteria order, final List<Long> tagIDs, final List<Long> subscribedOrgIDs, final int pageNum) {
        if (tagIDs == null || tagIDs.isEmpty()) {
            return eventService.findAllForOrganizations(subscribedOrgIDs, order, pageNum);
        } else {
            TagFilterCriteria filter = "all".equalsIgnoreCase(match) ? TagFilterCriteria.ALL : TagFilterCriteria.ANY;
            return eventService.findAllForOrganizationsAndTags(subscribedOrgIDs, tagIDs, filter, order, pageNum);
        }
    }

    private List<OpenPosition> fetchOpenPositionsForSubscribed(final String match, final boolean newestFirst, final List<Long> tagIDs, final List<Long> subscribedOrgIDs, final int pageNum) {
        OrderCriteria order = newestFirst ? OrderCriteria.NEWEST_FIRST : OrderCriteria.OLDEST_FIRST;
        if (tagIDs == null || tagIDs.isEmpty()) {
            return openPositionService.findAllForOrganizations(subscribedOrgIDs, order, pageNum);
        } else {
            TagFilterCriteria filter = "all".equalsIgnoreCase(match) ? TagFilterCriteria.ALL : TagFilterCriteria.ANY;
            return openPositionService.findAllForOrganizationsAndTags(subscribedOrgIDs, tagIDs, filter, order, pageNum);
        }
    }
}
