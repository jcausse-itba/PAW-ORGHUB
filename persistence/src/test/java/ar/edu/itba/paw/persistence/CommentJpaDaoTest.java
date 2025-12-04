package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Comment;
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
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class CommentJpaDaoTest {

    @Autowired
    private CommentJpaDao commentJpaDao;

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
    public void testFindByIDNonExistent() {
        final Optional<Comment> comment = commentJpaDao.findByID(999L);
        Assert.assertFalse(comment.isPresent());
    }

    @Rollback
    @Test
    public void testCreate() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Post post = postJpaDao.create(org, "Title", "Body", LocalDateTime.now(), null);
        em.flush();

        final String content = "Test comment";
        final Comment comment = commentJpaDao.create(user, post, content);

        Assert.assertNotNull(comment);
        Assert.assertNotNull(comment.getCommentID());
        Assert.assertEquals(content, comment.getContent());
        Assert.assertEquals(user, comment.getUser());
        Assert.assertEquals(post, comment.getPost());
    }

    @Rollback
    @Test
    public void testCreateIncrementsCommentCount() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Post post = postJpaDao.create(org, "Title", "Body", LocalDateTime.now(), null);
        em.flush();

        commentJpaDao.create(user, post, "Comment 1");
        em.flush();
        em.clear();

        final Post updatedPost = postJpaDao.findByID(post.getPostID()).orElseThrow();
        Assert.assertNotNull(updatedPost.getCommentCount());
        Assert.assertTrue(updatedPost.getCommentCount() >= 1);
    }

    @Rollback
    @Test
    public void testFindByID() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Post post = postJpaDao.create(org, "Title", "Body", LocalDateTime.now(), null);
        em.flush();

        final Comment comment = commentJpaDao.create(user, post, "Test comment");
        em.flush();
        em.clear();

        final Optional<Comment> foundComment = commentJpaDao.findByID(comment.getCommentID());
        Assert.assertTrue(foundComment.isPresent());
        Assert.assertEquals("Test comment", foundComment.get().getContent());
    }

    @Rollback
    @Test
    public void testDelete() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Post post = postJpaDao.create(org, "Title", "Body", LocalDateTime.now(), null);
        em.flush();

        final Comment comment = commentJpaDao.create(user, post, "Test comment");
        em.flush();
        final long commentID = comment.getCommentID();

        commentJpaDao.delete(comment);
        em.flush();
        em.clear();

        final Optional<Comment> deletedComment = commentJpaDao.findByID(commentID);
        Assert.assertFalse(deletedComment.isPresent());
    }

    @Rollback
    @Test
    public void testDeleteAllCommentsByPost() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Post post = postJpaDao.create(org, "Title", "Body", LocalDateTime.now(), null);
        em.flush();

        commentJpaDao.create(user, post, "Comment 1");
        commentJpaDao.create(user, post, "Comment 2");
        em.flush();

        commentJpaDao.deleteAllCommentsByPost(post);
        em.flush();
        em.clear();

        final Post updatedPost = postJpaDao.findByID(post.getPostID()).orElseThrow();
        Assert.assertNotNull(updatedPost.getCommentCount());
        Assert.assertEquals(0L, updatedPost.getCommentCount().longValue());
    }
}

