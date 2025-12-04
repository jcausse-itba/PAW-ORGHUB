package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Tag;
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
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class TagJpaDaoTest {

    @Autowired
    private TagJpaDao tagJpaDao;

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
        final Optional<Tag> tag = tagJpaDao.findByID(999L);
        Assert.assertFalse(tag.isPresent());
    }

    @Rollback
    @Test
    public void testFindByNameNonExistent() {
        final Optional<Tag> tag = tagJpaDao.findByName("NonExistentTag");
        Assert.assertFalse(tag.isPresent());
    }

    @Rollback
    @Test
    public void testCreate() {
        final String tagName = "TestTag";
        final Tag tag = tagJpaDao.create(tagName);

        Assert.assertNotNull(tag);
        Assert.assertNotNull(tag.getTagID());
        Assert.assertEquals(tagName, tag.getTagName());
    }

    @Rollback
    @Test
    public void testFindByID() {
        final String tagName = "TestTag";
        final Tag createdTag = tagJpaDao.create(tagName);
        em.flush();
        em.clear();

        final Optional<Tag> foundTag = tagJpaDao.findByID(createdTag.getTagID());
        Assert.assertTrue(foundTag.isPresent());
        Assert.assertEquals(tagName, foundTag.get().getTagName());
    }

    @Rollback
    @Test
    public void testFindByName() {
        final String tagName = "TestTag";
        final Tag createdTag = tagJpaDao.create(tagName);
        em.flush();
        em.clear();

        final Optional<Tag> foundTag = tagJpaDao.findByName(tagName);
        Assert.assertTrue(foundTag.isPresent());
        Assert.assertEquals(tagName, foundTag.get().getTagName());
    }

    @Rollback
    @Test
    public void testFindAll() {
        final Tag tag1 = tagJpaDao.create("Tag1");
        final Tag tag2 = tagJpaDao.create("Tag2");
        final Tag tag3 = tagJpaDao.create("Tag3");
        em.flush();
        em.clear();

        final List<Tag> allTags = tagJpaDao.findAll();
        Assert.assertTrue(allTags.size() >= 3);
        Assert.assertTrue(allTags.stream().anyMatch(t -> t.getTagName().equals("Tag1")));
        Assert.assertTrue(allTags.stream().anyMatch(t -> t.getTagName().equals("Tag2")));
        Assert.assertTrue(allTags.stream().anyMatch(t -> t.getTagName().equals("Tag3")));
    }

    @Rollback
    @Test
    public void testExistsTagName() {
        final String tagName = "TestTag";
        Assert.assertFalse(tagJpaDao.existsTagName(tagName));

        tagJpaDao.create(tagName);
        em.flush();
        em.clear();

        Assert.assertTrue(tagJpaDao.existsTagName(tagName));
    }
}

