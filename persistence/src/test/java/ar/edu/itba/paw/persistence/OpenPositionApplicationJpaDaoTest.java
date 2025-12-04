package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.OpenPositionAnswer;
import ar.edu.itba.paw.model.OpenPositionApplication;
import ar.edu.itba.paw.model.OpenPositionQuestion;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Status;
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
import java.util.Map;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class OpenPositionApplicationJpaDaoTest {

    @Autowired
    private OpenPositionApplicationJpaDao openPositionApplicationJpaDao;

    @Autowired
    private OpenPositionJpaDao openPositionJpaDao;

    @Autowired
    private UserJpaDao userJpaDao;

    @Autowired
    private OrganizationJpaDao organizationJpaDao;

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

    @Rollback
    @Test
    public void testApply() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), null, null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        final OpenPositionApplication application = openPositionApplicationJpaDao.apply(position, user);

        Assert.assertNotNull(application);
        Assert.assertNotNull(application.getApplicationID());
        Assert.assertEquals(position, application.getOpenPosition());
        Assert.assertEquals(user, application.getUser());
        Assert.assertEquals(Status.PENDING, application.getStatus());
    }

    @Rollback
    @Test
    public void testGetApplication() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), null, null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        final OpenPositionApplication application = openPositionApplicationJpaDao.apply(position, user);
        em.flush();
        em.clear();

        final Optional<OpenPositionApplication> foundApplication = openPositionApplicationJpaDao.getApplication(application.getApplicationID());
        Assert.assertTrue(foundApplication.isPresent());
        Assert.assertEquals(Status.PENDING, foundApplication.get().getStatus());
    }

    @Rollback
    @Test
    public void testGetApplicationNonExistent() {
        final Optional<OpenPositionApplication> application = openPositionApplicationJpaDao.getApplication(999L);
        Assert.assertFalse(application.isPresent());
    }

    @Rollback
    @Test
    public void testGetApplications() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), null, null, null);
        final User user1 = userJpaDao.findByID(1L).orElseThrow();
        final User user2 = userJpaDao.findByID(2L).orElseGet(() -> {
            em.clear();
            return userJpaDao.create("testuser2@itba.edu.ar", "User", "Two", "pass", user1.getPreferredLanguage(), inst);
        });
        em.flush();

        openPositionApplicationJpaDao.apply(position, user1);
        openPositionApplicationJpaDao.apply(position, user2);
        em.flush();
        em.clear();

        final List<OpenPositionApplication> applications = openPositionApplicationJpaDao.getApplications(position);
        Assert.assertEquals(2, applications.size());
    }

    @Rollback
    @Test
    public void testUpdateStatus() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), null, null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        final OpenPositionApplication application = openPositionApplicationJpaDao.apply(position, user);
        em.flush();
        openPositionApplicationJpaDao.updateStatus(application, Status.ACCEPTED);
        em.flush();
        em.clear();

        final OpenPositionApplication updatedApplication = openPositionApplicationJpaDao.getApplication(application.getApplicationID()).orElseThrow();
        Assert.assertEquals(Status.ACCEPTED, updatedApplication.getStatus());
    }

    @Rollback
    @Test
    public void testGetStatus() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), null, null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        openPositionApplicationJpaDao.apply(position, user);
        em.flush();
        em.clear();

        final Optional<Status> status = openPositionApplicationJpaDao.getStatus(position, user);
        Assert.assertTrue(status.isPresent());
        Assert.assertEquals(Status.PENDING, status.get());
    }

    @Rollback
    @Test
    public void testGetStatusNonExistent() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), null, null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        final Optional<Status> status = openPositionApplicationJpaDao.getStatus(position, user);
        Assert.assertFalse(status.isPresent());
    }

    @Rollback
    @Test
    public void testAddAnswer() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final List<String> questions = List.of("Why do you want this job?");
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), questions, null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        final OpenPositionApplication application = openPositionApplicationJpaDao.apply(position, user);
        em.flush();
        final List<OpenPositionQuestion> positionQuestions = openPositionJpaDao.getQuestions(position);
        Assert.assertFalse(positionQuestions.isEmpty());
        
        openPositionApplicationJpaDao.addAnswer(application, positionQuestions.get(0), "My answer");
        em.flush();
        em.clear();

        final List<OpenPositionAnswer> answers = openPositionApplicationJpaDao.getAnswers(application);
        Assert.assertEquals(1, answers.size());
        Assert.assertEquals("My answer", answers.get(0).getAnswer());
    }

    @Rollback
    @Test
    public void testGetAnswers() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final List<String> questions = List.of("Question 1", "Question 2");
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), questions, null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        final OpenPositionApplication application = openPositionApplicationJpaDao.apply(position, user);
        em.flush();
        final List<OpenPositionQuestion> positionQuestions = openPositionJpaDao.getQuestions(position);
        
        openPositionApplicationJpaDao.addAnswer(application, positionQuestions.get(0), "Answer 1");
        openPositionApplicationJpaDao.addAnswer(application, positionQuestions.get(1), "Answer 2");
        em.flush();
        em.clear();

        final List<OpenPositionAnswer> answers = openPositionApplicationJpaDao.getAnswers(application);
        Assert.assertEquals(2, answers.size());
    }

    @Rollback
    @Test
    public void testWithdraw() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final List<String> questions = List.of("Question 1");
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), questions, null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        final OpenPositionApplication application = openPositionApplicationJpaDao.apply(position, user);
        em.flush();
        final List<OpenPositionQuestion> positionQuestions = openPositionJpaDao.getQuestions(position);
        openPositionApplicationJpaDao.addAnswer(application, positionQuestions.get(0), "Answer");
        em.flush();

        openPositionApplicationJpaDao.withdraw(position, user);
        em.flush();
        em.clear();

        final Optional<Status> status = openPositionApplicationJpaDao.getStatus(position, user);
        Assert.assertFalse(status.isPresent());
    }

    @Rollback
    @Test
    public void testGetUserApplications() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final OpenPosition position1 = openPositionJpaDao.create(org, "Position 1", "Desc", "Reqs", "Full-time", LocalDateTime.now(), null, null, null);
        final OpenPosition position2 = openPositionJpaDao.create(org, "Position 2", "Desc", "Reqs", "Part-time", LocalDateTime.now(), null, null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        openPositionApplicationJpaDao.apply(position1, user);
        openPositionApplicationJpaDao.apply(position2, user);
        em.flush();
        em.clear();

        final List<OpenPositionApplication> applications = openPositionApplicationJpaDao.getUserApplications(user);
        Assert.assertTrue(applications.size() >= 2);
    }

    @Rollback
    @Test
    public void testGetUserApplicationStatusMap() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), null, null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        final OpenPositionApplication application = openPositionApplicationJpaDao.apply(position, user);
        em.flush();
        em.clear();

        final Map<Long, Status> statusMap = openPositionApplicationJpaDao.getUserApplicationStatusMap(user);
        Assert.assertTrue(statusMap.containsKey(position.getPositionID()));
        Assert.assertEquals(Status.PENDING, statusMap.get(position.getPositionID()));
    }

    @Rollback
    @Test
    public void testGetApplicationsByStatus() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), null, null, null);
        final User user1 = userJpaDao.findByID(1L).orElseThrow();
        final User user2 = userJpaDao.findByID(2L).orElseGet(() -> {
            em.clear();
            return userJpaDao.create("testuser2@itba.edu.ar", "User", "Two", "pass", user1.getPreferredLanguage(), inst);
        });
        em.flush();

        final OpenPositionApplication app1 = openPositionApplicationJpaDao.apply(position, user1);
        final OpenPositionApplication app2 = openPositionApplicationJpaDao.apply(position, user2);
        em.flush();
        openPositionApplicationJpaDao.updateStatus(app1, Status.ACCEPTED);
        em.flush();
        em.clear();

        final List<OpenPositionApplication> accepted = openPositionApplicationJpaDao.getApplicationsByStatus(position, Status.ACCEPTED);
        Assert.assertTrue(accepted.size() >= 1);

        final List<OpenPositionApplication> pending = openPositionApplicationJpaDao.getApplicationsByStatus(position, Status.PENDING);
        Assert.assertTrue(pending.size() >= 1);
    }

    @Rollback
    @Test
    public void testGetApplicationCountsByStatus() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), null, null, null);
        final User user1 = userJpaDao.findByID(1L).orElseThrow();
        final User user2 = userJpaDao.findByID(2L).orElseGet(() -> {
            em.clear();
            return userJpaDao.create("testuser2@itba.edu.ar", "User", "Two", "pass", user1.getPreferredLanguage(), inst);
        });
        em.flush();

        final OpenPositionApplication app1 = openPositionApplicationJpaDao.apply(position, user1);
        openPositionApplicationJpaDao.apply(position, user2);
        em.flush();
        openPositionApplicationJpaDao.updateStatus(app1, Status.ACCEPTED);
        em.flush();
        em.clear();

        final Map<Status, Long> counts = openPositionApplicationJpaDao.getApplicationCountsByStatus(position);
        Assert.assertTrue(counts.containsKey(Status.ACCEPTED));
        Assert.assertTrue(counts.containsKey(Status.PENDING));
        Assert.assertTrue(counts.get(Status.ACCEPTED) >= 1);
        Assert.assertTrue(counts.get(Status.PENDING) >= 1);
    }

    @Rollback
    @Test
    public void testDeleteAllApplicationsByPosition() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final List<String> questions = List.of("Question 1");
        final OpenPosition position = openPositionJpaDao.create(org, "Title", "Desc", "Reqs", "Full-time", LocalDateTime.now(), questions, null, null);
        final User user1 = userJpaDao.findByID(1L).orElseThrow();
        final User user2 = userJpaDao.findByID(2L).orElseGet(() -> {
            em.clear();
            return userJpaDao.create("testuser2@itba.edu.ar", "User", "Two", "pass", user1.getPreferredLanguage(), inst);
        });
        em.flush();

        final OpenPositionApplication app1 = openPositionApplicationJpaDao.apply(position, user1);
        final OpenPositionApplication app2 = openPositionApplicationJpaDao.apply(position, user2);
        em.flush();
        final List<OpenPositionQuestion> positionQuestions = openPositionJpaDao.getQuestions(position);
        openPositionApplicationJpaDao.addAnswer(app1, positionQuestions.get(0), "Answer 1");
        openPositionApplicationJpaDao.addAnswer(app2, positionQuestions.get(0), "Answer 2");
        em.flush();

        openPositionApplicationJpaDao.deleteAllApplicationsByPosition(position);
        em.flush();
        em.clear();

        final List<OpenPositionApplication> applications = openPositionApplicationJpaDao.getApplications(position);
        Assert.assertTrue(applications.isEmpty());
    }
}

