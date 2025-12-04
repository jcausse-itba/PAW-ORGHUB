package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;

import java.util.List;

public interface LikeDao {
    /**
     * Create a like relationship between a User and a Post.
     * @param user      The User who likes the post. Must not be null.
     * @param post      The Post being liked. Must not be null.
     */
    void likePost(User user, Post post);

    /**
     * Remove a like relationship between a User and a Post.
     * @param user      The User who unlikes the post. Must not be null.
     * @param post      The Post being unliked. Must not be null.
     */
    void unlikePost(User user, Post post);

    /**
     * Check whether a specific post is liked by a specific user.
     * @param user      The User to check. Must not be null.
     * @param post      The Post to check. Must not be null.
     * @return          Boolean indicating whether the post is liked by the user.
     */
    boolean isPostLikedByUser(User user, Post post);

    /**
     * Delete all likes for a specific post.
     * @param post      The Post whose likes should be deleted. Must not be null.
     */
    void deleteAllLikesForPost(Post post);

    /**
     * Get all posts that a user has liked.
     * @param user      The User. Must not be null.
     * @return          A List of Post objects (that might be empty).
     */
    List<Post> findLikedPostsByUser(User user);
}
