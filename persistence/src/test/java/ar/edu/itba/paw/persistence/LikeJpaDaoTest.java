package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class LikeJpaDaoTest {

    @Autowired
    private LikeJpaDao likeJpaDao;

    @Autowired
    private UserJpaDao userJpaDao;

    @Autowired
    private OrganizationJpaDao organizationJpaDao;

    @Autowired
    private InstitutionJpaDao institutionJpaDao;

    @Autowired
    private PostJpaDao postJpaDao;

    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Rollback
    @Test
    public void testIsPostLikedByUserFalse() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Post post = postJpaDao.create(org, "Title", "Body", LocalDateTime.now(), null);
        em.flush();

        Assert.assertFalse(likeJpaDao.isPostLikedByUser(user, post));
    }

    @Rollback
    @Test
    public void testLikePost() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Post post = postJpaDao.create(org, "Title", "Body", LocalDateTime.now(), null);
        em.flush();

        likeJpaDao.likePost(user, post);
        em.flush();
        em.clear();

        Assert.assertTrue(likeJpaDao.isPostLikedByUser(user, post));
        
        final Post updatedPost = postJpaDao.findByID(post.getPostID()).orElseThrow();
        Assert.assertNotNull(updatedPost.getLikeCount());
        Assert.assertTrue(updatedPost.getLikeCount() >= 1);
    }

    @Rollback
    @Test
    public void testUnlikePost() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Post post = postJpaDao.create(org, "Title", "Body", LocalDateTime.now(), null);
        em.flush();

        likeJpaDao.likePost(user, post);
        em.flush();
        likeJpaDao.unlikePost(user, post);
        em.flush();
        em.clear();

        Assert.assertFalse(likeJpaDao.isPostLikedByUser(user, post));
    }

    @Rollback
    @Test
    public void testDeleteAllLikesForPost() {
        final User user1 = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        // Use existing user2 from test data instead of creating a new one
        final User user2 = userJpaDao.findByID(2L).orElseGet(() -> {
            // If user2 doesn't exist, create it with a unique email
            em.clear(); // Clear session to avoid conflicts
            return userJpaDao.create("testuser2@itba.edu.ar", "User", "Two", "pass", user1.getPreferredLanguage(), inst);
        });
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Post post = postJpaDao.create(org, "Title", "Body", LocalDateTime.now(), null);
        em.flush();

        likeJpaDao.likePost(user1, post);
        likeJpaDao.likePost(user2, post);
        em.flush();
        likeJpaDao.deleteAllLikesForPost(post);
        em.flush();
        em.clear();

        Assert.assertFalse(likeJpaDao.isPostLikedByUser(user1, post));
        Assert.assertFalse(likeJpaDao.isPostLikedByUser(user2, post));
    }

    @Rollback
    @Test
    public void testFindLikedPostsByUser() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Post post1 = postJpaDao.create(org, "Title 1", "Body 1", LocalDateTime.now(), null);
        final Post post2 = postJpaDao.create(org, "Title 2", "Body 2", LocalDateTime.now(), null);
        em.flush();

        likeJpaDao.likePost(user, post1);
        likeJpaDao.likePost(user, post2);
        em.flush();
        em.clear();

        final List<Post> likedPosts = likeJpaDao.findLikedPostsByUser(user);
        Assert.assertTrue(likedPosts.size() >= 2);
        Assert.assertTrue(likedPosts.stream().anyMatch(p -> p.getPostID().equals(post1.getPostID())));
        Assert.assertTrue(likedPosts.stream().anyMatch(p -> p.getPostID().equals(post2.getPostID())));
    }
}

