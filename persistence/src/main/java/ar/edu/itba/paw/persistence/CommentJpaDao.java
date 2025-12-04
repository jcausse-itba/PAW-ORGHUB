package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.model.Comment;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class CommentJpaDao implements CommentDao {
    @PersistenceContext private EntityManager em;

    @Override
    public Comment create(final User user, final Post post, final String content) {
        final Comment comment = new Comment(user, post, content);
        em.persist(comment);
        incrementCommentCount(post);
        return comment;
    }

    @Override
    public Optional<Comment> findByID(final long commentID) {
        return Optional.ofNullable(em.find(Comment.class, commentID));
    }

    @Override
    public void delete(final Comment comment) {
        decrementCommentCount(comment.getPost());
        em.remove(comment);
    }

    @Override
    public void deleteAllCommentsByPost(final Post post) {
        final String deleteHql = "DELETE FROM Comment c WHERE c.post = :post";
        em.createQuery(deleteHql).setParameter("post", post).executeUpdate();
        resetCommentCount(post);
    }

    private void incrementCommentCount(final Post post) {
        final String hql = "UPDATE Post p SET p.commentCount = COALESCE(p.commentCount, 0) + 1 WHERE p.postID = :postID";
        em.createQuery(hql).setParameter("postID", post.getPostID()).executeUpdate();
    }

    private void decrementCommentCount(final Post post) {
        final String hql = "UPDATE Post p SET p.commentCount = GREATEST(COALESCE(p.commentCount, 0) - 1, 0) WHERE p.postID = :postID";
        em.createQuery(hql).setParameter("postID", post.getPostID()).executeUpdate();
    }

    private void resetCommentCount(final Post post) {
        final String hql = "UPDATE Post p SET p.commentCount = 0 WHERE p.postID = :postID";
        em.createQuery(hql)
                .setParameter("postID", post.getPostID())
                .executeUpdate();
    }
}
