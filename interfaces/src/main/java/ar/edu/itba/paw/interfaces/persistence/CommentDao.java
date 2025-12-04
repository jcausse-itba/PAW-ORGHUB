// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import java.util.Optional;

public interface CommentDao {
    /**
     * Create a new comment and get the Comment object that represents it.
     * @param user      The User creating the comment. Must not be null.
     * @param post      The Post the comment belongs to. Must not be null.
     * @param content   The content of the comment. Must not be null.
     * @return          A Comment representing the created comment.
     */
    Comment create(User user, Post post, String content);
    
    /**
     * Find a Comment via its ID.
     * @param commentID          The ID of the Comment to be found.
     * @return                   An Optional containing a Comment if it was found. An empty Optional otherwise.
     */
    Optional<Comment> findByID(long commentID);
    
    /**
     * Delete a Comment.
     * @param comment      The Comment to be deleted. Must not be null.
     */
    void delete(Comment comment);
    
    /**
     * Delete all Comments for a Post.
     * @param post      The Post whose comments are to be deleted. Must not be null.
     */
    void deleteAllCommentsByPost(Post post);
}
