// DONE

package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.PostService;
import ar.edu.itba.paw.interfaces.service.OrganizationService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.OrganizationNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.PostNotFoundException;
import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.Tag;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.annotation.TransactionalRead;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostDao postDao;
    private final TagDao tagDao;
    private final OrganizationDao orgDao;
    private final InstitutionDao institutionDao;
    private final CommentDao commentDao;
    private final LikeDao likeDao;
    private final EmailService emailService;
    private final OrganizationService organizationService;
    private final UserService userService;

    @Override
    @TransactionalRead
    public Post findByID(final long postID) {
        return postDao.findByID(postID).orElseThrow(() -> new PostNotFoundException(postID));
    }

    @Override
    @TransactionalRead
    public List<Post> findAllForInstitution(final long institutionID, final OrderCriteria order, final int pageNum) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        return postDao.findAllForInstitution(institution, order, pageNum);
    }

    @Override
    @TransactionalRead
    public List<Post> findAllForTags(final long institutionID, final List<Long> tagIDs, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        List<Tag> tags = tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(tag -> tag != null)
                .collect(Collectors.toList());
        return postDao.findAllForTags(institution, tags, filter, order, pageNum);
    }

    @Override
    @TransactionalRead
    public List<Post> findAllForOrganizations(final List<Long> orgIDs, final OrderCriteria order, final int pageNum) {
        List<Organization> organizations = orgIDs.stream()
                .map(orgID -> orgDao.findByID(orgID).orElse(null))
                .filter(org -> org != null)
                .collect(Collectors.toList());
        return postDao.findAllForOrganizations(organizations, order, pageNum);
    }

    @Override
    @TransactionalRead
    public List<Post> findAllForOrganizationsAndTags(final List<Long> orgIDs, final List<Long> tagIDs, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum) {
        List<Organization> organizations = orgIDs.stream()
                .map(orgID -> orgDao.findByID(orgID).orElse(null))
                .filter(org -> org != null)
                .collect(Collectors.toList());
        List<Tag> tags = tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(tag -> tag != null)
                .collect(Collectors.toList());
        return postDao.findAllForOrganizationsAndTags(organizations, tags, filter, order, pageNum);
    }

    @Override
    @Transactional
    public Post create(final long orgID, final String title, final String body, final LocalDateTime dateTime, final List<Long> tagIDs) {
        LOGGER.debug("Creating post '{}' for organization {}.", title, orgID);
        
        Organization organization = orgDao.findByID(orgID).orElseThrow(() -> new OrganizationNotFoundException(orgID));
        Set<Tag> tags = tagIDs != null ? tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(tag -> tag != null)
                .collect(Collectors.toSet()) : new HashSet<>();
        
        Post post = postDao.create(organization, title, body, dateTime, tags);
        
        List<User> subscribers = organizationService.getSubscribers(orgID);
        String orgName = organization.getOrgName();
        LOGGER.info("Post '{}' created successfully (ID: {}) for organization '{}'. Notifying {} subscribers.", 
                title, post.getPostID(), orgName, subscribers.size()
        );
        
        for (User subscriber : subscribers) {
            emailService.sendPostNotificationEmail(subscriber.getUserEmail(), orgName, title, body, post.getPostID());
        }
        
        return post;
    }

    @Override
    @Transactional
    public void update(final long postID, final String title, final String body, final List<Long> tagIDs) {
        LOGGER.debug("Updating post {} with title '{}'.", postID, title);
        
        Post post = postDao.findByID(postID).orElseThrow(() -> new PostNotFoundException(postID));
        post.setTitle(title);
        post.setBody(body);
        
        Set<Tag> newTags = tagIDs != null ? tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(tag -> tag != null)
                .collect(Collectors.toSet()) : new HashSet<>();
        
        post.getTags().retainAll(newTags);
        post.getTags().addAll(newTags);
        
        LOGGER.info("Post {} updated successfully.", postID);
    }

    @Override
    @Transactional
    public void delete(final long postID) {
        LOGGER.debug("Deleting post {}.", postID);
        
        Post post = postDao.findByID(postID).orElseThrow(() -> new PostNotFoundException(postID));
        likeDao.deleteAllLikesForPost(post);
        commentDao.deleteAllCommentsByPost(post);
        postDao.delete(post);
        
        LOGGER.info("Post {} deleted successfully.", postID);
    }

    @Override
    @TransactionalRead
    public List<Comment> findAllCommentsByID(final long postID, final int pageNum) {
        Post post = postDao.findByID(postID).orElseThrow(() -> new PostNotFoundException(postID));
        return postDao.getComments(post, pageNum);
    }

    @Override
    @Transactional
    public void toggleLikePost(final long postID, final long userID) {
        LOGGER.debug("Toggling like for post {} by user {}.", postID, userID);
        
        Post post = postDao.findByID(postID).orElseThrow(() -> new PostNotFoundException(postID));
        User user = userService.findByID(userID);
        
        boolean alreadyLiked = likeDao.isPostLikedByUser(user, post);
        if (alreadyLiked) {
            likeDao.unlikePost(user, post);
            LOGGER.debug("User {} unliked post {}.", userID, postID);
        } else {
            likeDao.likePost(user, post);
            LOGGER.debug("User {} liked post {}.", userID, postID);
        }
    }

    @Override
    @Transactional
    public Comment createComment(final long userID, final long postID, final String content) {
        LOGGER.debug("Creating comment for post {} by user {}.", postID, userID);
        
        Post post = postDao.findByID(postID).orElseThrow(() -> new PostNotFoundException(postID));
        User user = userService.findByID(userID);
        
        Comment comment = commentDao.create(user, post, content);
        
        String organizationEmail = post.getOrganization().getOrgEmail();
        emailService.sendCommentNotificationEmail(organizationEmail, post.getTitle(), content, postID);
        
        LOGGER.info("Comment created successfully (ID: {}) for post '{}' by user {}.", 
                comment.getCommentID(), post.getTitle(), userID
        );

        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(final long commentID) {
        LOGGER.debug("Deleting comment {}.", commentID);
        
        Optional<Comment> commentOptional = commentDao.findByID(commentID);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            commentDao.delete(comment);
            LOGGER.info("Comment {} deleted successfully from post {}.", commentID, comment.getPost().getPostID());
        } else {
            LOGGER.warn("Attempted to delete non-existent comment {}.", commentID);
        }
    }

    @Override
    @TransactionalRead
    public boolean isPostLikedByUser(final long userID, final long postID) {
        Post post = postDao.findByID(postID).orElseThrow(() -> new PostNotFoundException(postID));
        User user = userService.findByID(userID);
        return likeDao.isPostLikedByUser(user, post);
    }

    @Override
    @TransactionalRead
    public List<Post> findPopularPostsForInstitution(final long institutionID, final OrderCriteria order, final int limit) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        return postDao.findPopularPostsForInstitution(institution, order, limit);
    }

    @Override
    @TransactionalRead
    public List<Post> findPopularPostsForTags(final long institutionID, final List<Long> tagIDs, final TagFilterCriteria filter, final OrderCriteria order, final int limit) {
        Institution institution = institutionDao.findByID(institutionID).orElseThrow(() -> new InstitutionNotFoundException(institutionID));
        List<Tag> tags = tagIDs.stream()
                .map(tagID -> tagDao.findByID(tagID).orElse(null))
                .filter(tag -> tag != null)
                .collect(Collectors.toList());
        return postDao.findPopularPostsForTags(institution, tags, filter, order, limit);
    }

    @Override
    @TransactionalRead
    public long getOrganizationIDByID(final long postID) {
        Post post = postDao.findByID(postID).orElseThrow(() -> new PostNotFoundException(postID));
        return post.getOrganization().getOrgID();
    }
}
