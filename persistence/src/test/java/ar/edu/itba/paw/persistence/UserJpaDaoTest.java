package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Language;
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
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class UserJpaDaoTest {

    @Autowired
    private UserJpaDao userJpaDao;

    @Autowired
    private InstitutionJpaDao institutionJpaDao;

    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    private static final int EXISTING_USER_ID = 1;
    private static final String EXISTING_USER_EMAIL = "testuser1@itba.edu.ar";
    private static final String EXISTING_USER_PASSWORD = "123";
    private static final String EXISTING_USER_FIRST_NAME = "John";
    private static final String EXISTING_USER_LAST_NAME = "Doe";

    @Rollback
    @Test
    public void testFindByID() {
        final Optional<User> maybeUser = userJpaDao.findByID(EXISTING_USER_ID);

        Assert.assertTrue(maybeUser.isPresent());
        User u = maybeUser.get();

        Assert.assertEquals(EXISTING_USER_EMAIL, u.getUserEmail());
        Assert.assertEquals(EXISTING_USER_PASSWORD, u.getEncodedPassword());
        Assert.assertEquals(EXISTING_USER_FIRST_NAME, u.getFirstName());
        Assert.assertEquals(EXISTING_USER_LAST_NAME, u.getLastName());
        Assert.assertNotNull(u.getInstitution());
    }

    @Rollback
    @Test
    public void testFindByIDNonExistent() {
        final Optional<User> maybeUser = userJpaDao.findByID(999L);
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Rollback
    @Test
    public void testFindByEmail() {
        final Optional<User> maybeUser = userJpaDao.findByEmail(EXISTING_USER_EMAIL);

        Assert.assertTrue(maybeUser.isPresent());
        User u = maybeUser.get();
        Assert.assertEquals(EXISTING_USER_EMAIL, u.getUserEmail());
        Assert.assertNotNull(u.getInstitution());
    }

    @Rollback
    @Test
    public void testFindByEmailNonExistent() {
        final Optional<User> maybeUser = userJpaDao.findByEmail("nonexistent@itba.edu.ar");
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Rollback
    @Test
    public void testFindWithoutInstitutionByID() {
        final Optional<User> maybeUser = userJpaDao.findWithoutInstitution(EXISTING_USER_ID);
        Assert.assertTrue(maybeUser.isPresent());
    }

    @Rollback
    @Test
    public void testFindWithoutInstitutionByEmail() {
        final Optional<User> maybeUser = userJpaDao.findWithoutInstitution(EXISTING_USER_EMAIL);
        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(EXISTING_USER_EMAIL, maybeUser.get().getUserEmail());
    }

    @Rollback
    @Test
    public void testCreate() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final String email = "newuser@itba.edu.ar";
        final String firstName = "Jane";
        final String lastName = "Smith";
        final String password = "password123";
        final Language lang = Language.ENGLISH;

        final User user = userJpaDao.create(email, firstName, lastName, password, lang, inst);

        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getUserID());
        Assert.assertEquals(email, user.getUserEmail());
        Assert.assertEquals(firstName, user.getFirstName());
        Assert.assertEquals(lastName, user.getLastName());
        Assert.assertEquals(password, user.getEncodedPassword());
        Assert.assertEquals(lang, user.getPreferredLanguage());
        Assert.assertEquals(inst, user.getInstitution());
    }

    @Rollback
    @Test
    public void testExistsEmail() {
        Assert.assertTrue(userJpaDao.existsEmail(EXISTING_USER_EMAIL));
        Assert.assertFalse(userJpaDao.existsEmail("nonexistent@itba.edu.ar"));
    }

    @Rollback
    @Test
    public void testSetPreferredLanguage() {
        final User user = userJpaDao.findByID(EXISTING_USER_ID).orElseThrow();
        final Language newLang = Language.ENGLISH;

        userJpaDao.setPreferredLanguage(user, newLang);
        em.flush();
        em.clear();

        final User updatedUser = userJpaDao.findByID(EXISTING_USER_ID).orElseThrow();
        Assert.assertEquals(newLang, updatedUser.getPreferredLanguage());
    }
}
