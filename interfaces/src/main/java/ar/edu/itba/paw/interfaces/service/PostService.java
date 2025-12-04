// DONE

package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.Post;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {
    /**
     * Find a Post via its ID.
     * @param postID              The ID of the Post to be found.
     * @return                    A Post if it was found.
     * @throws PostNotFoundException if the post was not found.
     */
    Post findByID(long postID);

    /**
     * Get all posts that belong to organizations within a specific institution.
     * @param institutionID        The ID of the Institution.
     * @param order                The order criteria to sort the posts.
     * @param pageNum              Page Number (pagination).
     * @return                     A List of Post objects (that might be empty).
     */
    List<Post> findAllForInstitution(long institutionID, OrderCriteria order, int pageNum);

    /**
     * Get all posts that are tagged with the specified tags, filtered by the specified criteria.
     * @param institutionID        The ID of the Institution.
     * @param tagIDs               The List of tag IDs.
     * @param filter               The filter criteria to apply to the tags.
     * @param order                The order criteria to sort the posts.
     * @param pageNum              Page Number (pagination).
     * @return                     A List of Post objects (that might be empty).
     */
    List<Post> findAllForTags(long institutionID, List<Long> tagIDs, TagFilterCriteria filter, OrderCriteria order, int pageNum);

    /**
     * Get all posts published by the specified organizations.
     * @param orgIDs               The List of organization IDs.
     * @param order                The order criteria to sort the posts.
     * @param pageNum              Page Number (pagination).
     * @return                     A List of Post objects (that might be empty).
     */
    List<Post> findAllForOrganizations(List<Long> orgIDs, OrderCriteria order, int pageNum);

    /**
     * Get all posts published by the specified organizations that are tagged with the specified tags, filtered by the specified criteria.
     * @param orgIDs               The List of organization IDs.
     * @param tagIDs               The List of tag IDs.
     * @param filter               The filter criteria to apply to the tags.
     * @param order                The order criteria to sort the posts.
     * @param pageNum              Page Number (pagination).
     * @return                     A List of Post objects (that might be empty).
     */
    List<Post> findAllForOrganizationsAndTags(List<Long> orgIDs, List<Long> tagIDs, TagFilterCriteria filter, OrderCriteria order, int pageNum);

    /**
     * Create a new post and get the Post object that represents it.
     * @param orgID                The ID of the Organization publishing the post.
     * @param title                 The post's title. Must not be null.
     * @param body                  The post's body content. Must not be null.
     * @param dateTime              The date and time when the post was created.
     * @param tagIDs                The List of tag IDs to associate with the post.
     * @return                      A Post representing the created post.
     */
    Post create(long orgID, String title, String body, LocalDateTime dateTime, List<Long> tagIDs);

    /**
     * Update a post.
     * @param postID               The ID of the Post to update.
     * @param title                 The new title.
     * @param body                  The new body content.
     * @param tagIDs                The List of tag IDs to associate with the post.
     */
    void update(long postID, String title, String body, List<Long> tagIDs);

    /**
     * Delete a post.
     * @param postID               The ID of the Post to delete.
     */
    void delete(long postID);

    /**
     * Get all comments associated with a specific post.
     * @param postID                The ID of the Post.
     * @param pageNum               Page Number (pagination).
     * @return                      A List of Comment objects (that might be empty).
     */
    List<Comment> findAllCommentsByID(long postID, int pageNum);

    /**
     * Toggle like status for a post (like if not liked, unlike if already liked).
     * @param postID                The ID of the Post.
     * @param userID                The ID of the User.
     */
    void toggleLikePost(long postID, long userID);

    /**
     * Create a new comment and get the Comment object that represents it.
     * @param userID                The ID of the User creating the comment.
     * @param postID                The ID of the Post.
     * @param content                 The content of the comment. Must not be null.
     * @return                       A Comment representing the created comment.
     */
    Comment createComment(long userID, long postID, String content);
    
    /**
     * Delete a comment.
     * @param commentID             The ID of the Comment to delete.
     */
    void deleteComment(long commentID);

    /**
     * Check whether a specific post is liked by a specific user.
     * @param userID                The ID of the User to check.
     * @param postID                The ID of the Post to check.
     * @return                      Boolean indicating whether the post is liked by the user.
     */
    boolean isPostLikedByUser(long userID, long postID);

    /**
     * Find popular posts for a specific institution, ordered by engagement (likes) and date.
     * @param institutionID         The ID of the Institution.
     * @param order                 The order criteria to sort the posts when like counts are equal.
     * @param limit                 The maximum number of posts to return.
     * @return                      A List of Post objects (that might be empty), ordered by engagement and date.
     */
    List<Post> findPopularPostsForInstitution(long institutionID, OrderCriteria order, int limit);

    /**
     * Find popular posts for a specific institution that are tagged with the specified tags, filtered by the specified criteria, ordered by engagement (likes) and date.
     * @param institutionID         The ID of the Institution.
     * @param tagIDs               The List of tag IDs.
     * @param filter               The filter criteria to apply to the tags.
     * @param order                The order criteria to sort the posts when like counts are equal.
     * @param limit                The maximum number of posts to return.
     * @return                     A List of Post objects (that might be empty), ordered by engagement and date.
     */
    List<Post> findPopularPostsForTags(long institutionID, List<Long> tagIDs, TagFilterCriteria filter, OrderCriteria order, int limit);

    /**
     * Get the organization ID for a post.
     * @param postID                The ID of the Post.
     * @return                      The organization ID.
     * @throws PostNotFoundException if the post was not found.
     */
    long getOrganizationIDByID(long postID);
}
