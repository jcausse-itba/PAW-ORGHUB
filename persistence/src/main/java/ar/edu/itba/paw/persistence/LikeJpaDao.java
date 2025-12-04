package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.LikeDao;
import ar.edu.itba.paw.model.manytomany.Like;
import ar.edu.itba.paw.model.manytomany.LikeCompositeID;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class LikeJpaDao implements LikeDao {
    @PersistenceContext private EntityManager em;

    @Override
    public void likePost(final User user, final Post post) {
        em.persist(new Like(user, post));
        incrementLikeCount(post);
    }

    @Override
    public void unlikePost(final User user, final Post post) {
        Optional.ofNullable(em.find(Like.class, new LikeCompositeID(user, post))).ifPresent(like -> {
            em.remove(like);
            decrementLikeCount(post);
        });
    }

    @Override
    public boolean isPostLikedByUser(final User user, final Post post) {
        return em.find(Like.class, new LikeCompositeID(user, post)) != null;
    }

    @Override
    public void deleteAllLikesForPost(final Post post) {
        final String deleteHql = "DELETE FROM Like l WHERE l.post = :post";
        em.createQuery(deleteHql).setParameter("post", post).executeUpdate();
        resetLikeCount(post);
    }

    private void incrementLikeCount(final Post post) {
        final String hql = "UPDATE Post p SET p.likeCount = COALESCE(p.likeCount, 0) + 1 WHERE p.postID = :postID";
        em.createQuery(hql).setParameter("postID", post.getPostID()).executeUpdate();
    }

    private void decrementLikeCount(final Post post) {
        final String hql = "UPDATE Post p SET p.likeCount = GREATEST(COALESCE(p.likeCount, 0) - 1, 0) WHERE p.postID = :postID";
        em.createQuery(hql).setParameter("postID", post.getPostID()).executeUpdate();
    }

    private void resetLikeCount(final Post post) {
        final String hql = "UPDATE Post p SET p.likeCount = 0 WHERE p.postID = :postID";
        em.createQuery(hql)
                .setParameter("postID", post.getPostID())
                .executeUpdate();
    }

    @Override
    public List<Post> findLikedPostsByUser(final User user) {
        final String hql = "SELECT p FROM Like l JOIN l.post p WHERE l.user = :user ORDER BY p.datetime DESC, p.postID DESC";
        return em.createQuery(hql, Post.class)
                .setParameter("user", user)
                .getResultList();
    }
}
