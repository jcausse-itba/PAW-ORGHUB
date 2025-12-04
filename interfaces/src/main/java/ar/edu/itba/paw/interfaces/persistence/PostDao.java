// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.interfaces.enums.OrderCriteria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostDao {
    /**
     * Find a Post via its ID.
     * @param postID        The ID of the Post to be found.
     * @return              An Optional containing a Post if it was found. An empty Optional otherwise.
     */
    Optional<Post> findByID(long postID);

    /**
     * Get all posts that belong to organizations within a specific institution.
     * @param inst          The Institution.
     * @param order         The order criteria to sort the posts.
     * @param pageNum       Page Number (pagination)
     * @return              A List of Post objects (that might be empty).
     */
    List<Post> findAllForInstitution(Institution inst, OrderCriteria order, int pageNum);

    /**
     * Get all posts published by the specified organizations, ordered by date.
     * @param orgs          The List of Organizations.
     * @param order         The order criteria to sort the posts.
     * @param pageNum       Page Number (pagination)
     * @return              A List of Post objects (that might be empty), ordered by date.
     */
    List<Post> findAllForOrganizations(List<Organization> orgs, OrderCriteria order, int pageNum);

    /**
     * Get all posts that are tagged with the specified tags, filtered by the specified criteria, ordered by the specified criteria.
     * @param inst          The Institution.
     * @param tags          The List of Tags.
     * @param filter        The filter criteria to apply to the tags.
     * @param order         The order criteria to sort the posts.
     * @param pageNum       Page Number (pagination)
     * @return              A List of Post objects (that might be empty).
     */
    List<Post> findAllForTags(Institution inst, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum);

    /**
     * Get all posts published by the specified organizations that are tagged with the specified tags, filtered by the specified criteria.
     * @param orgs          The List of Organizations.
     * @param tags          The List of Tags.
     * @param filter        The filter criteria to apply to the tags.
     * @param order         The order criteria to sort the posts.
     * @param pageNum       Page Number (pagination)
     * @return              A List of Post objects (that might be empty).
     */
    List<Post> findAllForOrganizationsAndTags(List<Organization> orgs, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum);

    /**
     * Create a new post and get the Post object that represents it.
     * @param org           The Organization publishing the post. Must not be null.
     * @param title         The post's title. Must not be null.
     * @param body          The post's body content. Must not be null.
     * @param dateTime      The date and time when the post was created.
     * @param tags          The Set of Tags to associate with the post. Up to CONTENT_MAX_TAGS tags will be added.
     * @return              A Post representing the created post.
     */
    Post create(Organization org, String title, String body, LocalDateTime dateTime, Set<Tag> tags);

    /**
     * Delete a post.
     * @param post          The Post to be deleted. Must not be null.
     */
    void delete(Post post);

    /**
     * Get all comments associated with a specific post.
     * @param post          The Post.
     * @param pageNum       Page Number (pagination)
     * @return              A List of Comment objects (that might be empty).
     */
    List<Comment> getComments(Post post, int pageNum);

    /**
     * Find popular posts for a specific institution, ordered by engagement (likes) and date.
     * @param inst          The Institution.
     * @param order         The order criteria to sort the events when like counts are equal.
     * @param limit         The maximum number of posts to return.
     * @return              A List of Post objects (that might be empty), ordered by engagement and date.
     */
    List<Post> findPopularPostsForInstitution(Institution inst, OrderCriteria order, int limit);

    /**
     * Find popular posts for a specific institution that are tagged with the specified tags, filtered by the specified criteria, ordered by engagement (likes) and date.
     * @param inst          The Institution.
     * @param tags          The List of Tags.
     * @param filter        The filter criteria to apply to the tags.
     * @param order         The order criteria to sort the posts when like counts are equal.
     * @param limit         The maximum number of posts to return.
     * @return              A List of Post objects (that might be empty), ordered by engagement and date.
     */
    List<Post> findPopularPostsForTags(Institution inst, List<Tag> tags, TagFilterCriteria filter, OrderCriteria order, int limit);
}
