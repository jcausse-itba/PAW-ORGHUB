package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.EventEnrollment;
import ar.edu.itba.paw.model.Institution;
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
public class EventEnrollmentJpaDaoTest {

    @Autowired
    private EventEnrollmentJpaDao eventEnrollmentJpaDao;

    @Autowired
    private EventJpaDao eventJpaDao;

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
    public void testEnrollUser() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Event event = eventJpaDao.create(org, "Title", "Desc", "Location", LocalDateTime.now().plusDays(1), LocalDateTime.now(), null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        eventEnrollmentJpaDao.enrollUser(event, user);
        em.flush();
        em.clear();

        Assert.assertTrue(eventEnrollmentJpaDao.isUserEnrolled(event, user));
    }

    @Rollback
    @Test
    public void testIsUserEnrolledFalse() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Event event = eventJpaDao.create(org, "Title", "Desc", "Location", LocalDateTime.now().plusDays(1), LocalDateTime.now(), null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        Assert.assertFalse(eventEnrollmentJpaDao.isUserEnrolled(event, user));
    }

    @Rollback
    @Test
    public void testUnenrollUser() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Event event = eventJpaDao.create(org, "Title", "Desc", "Location", LocalDateTime.now().plusDays(1), LocalDateTime.now(), null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        eventEnrollmentJpaDao.enrollUser(event, user);
        em.flush();
        eventEnrollmentJpaDao.unenrollUser(event, user);
        em.flush();
        em.clear();

        Assert.assertFalse(eventEnrollmentJpaDao.isUserEnrolled(event, user));
    }

    @Rollback
    @Test
    public void testGetEnrollments() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Event event = eventJpaDao.create(org, "Title", "Desc", "Location", LocalDateTime.now().plusDays(1), LocalDateTime.now(), null, null);
        final User user1 = userJpaDao.findByID(1L).orElseThrow();
        final User user2 = userJpaDao.findByID(2L).orElseGet(() -> {
            em.clear();
            return userJpaDao.create("testuser2@itba.edu.ar", "User", "Two", "pass", user1.getPreferredLanguage(), inst);
        });
        em.flush();

        eventEnrollmentJpaDao.enrollUser(event, user1);
        eventEnrollmentJpaDao.enrollUser(event, user2);
        em.flush();
        em.clear();

        final List<EventEnrollment> enrollments = eventEnrollmentJpaDao.getEnrollments(event);
        Assert.assertEquals(2, enrollments.size());
    }

    @Rollback
    @Test
    public void testGetEnrollmentCount() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Event event = eventJpaDao.create(org, "Title", "Desc", "Location", LocalDateTime.now().plusDays(1), LocalDateTime.now(), null, null);
        final User user1 = userJpaDao.findByID(1L).orElseThrow();
        final User user2 = userJpaDao.findByID(2L).orElseGet(() -> {
            em.clear();
            return userJpaDao.create("testuser2@itba.edu.ar", "User", "Two", "pass", user1.getPreferredLanguage(), inst);
        });
        em.flush();

        eventEnrollmentJpaDao.enrollUser(event, user1);
        eventEnrollmentJpaDao.enrollUser(event, user2);
        em.flush();
        em.clear();

        final long count = eventEnrollmentJpaDao.getEnrollmentCount(event);
        Assert.assertEquals(2L, count);
    }

    @Rollback
    @Test
    public void testGetAllEventsUserEnrolled() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Event event1 = eventJpaDao.create(org, "Event 1", "Desc", "Location", LocalDateTime.now().plusDays(1), LocalDateTime.now(), null, null);
        final Event event2 = eventJpaDao.create(org, "Event 2", "Desc", "Location", LocalDateTime.now().plusDays(2), LocalDateTime.now(), null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        eventEnrollmentJpaDao.enrollUser(event1, user);
        eventEnrollmentJpaDao.enrollUser(event2, user);
        em.flush();
        em.clear();

        final List<EventEnrollment> enrollments = eventEnrollmentJpaDao.getAllEventsUserEnrolled(user);
        Assert.assertTrue(enrollments.size() >= 2);
    }

    @Rollback
    @Test
    public void testUpdateEnrollmentStatus() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Event event = eventJpaDao.create(org, "Title", "Desc", "Location", LocalDateTime.now().plusDays(1), LocalDateTime.now(), null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        eventEnrollmentJpaDao.enrollUser(event, user);
        em.flush();
        eventEnrollmentJpaDao.updateEnrollmentStatus(event, user, Status.ACCEPTED);
        em.flush();
        em.clear();

        final Optional<Status> status = eventEnrollmentJpaDao.getEnrollmentStatus(event, user);
        Assert.assertTrue(status.isPresent());
        Assert.assertEquals(Status.ACCEPTED, status.get());
    }

    @Rollback
    @Test
    public void testGetEnrollmentStatus() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Event event = eventJpaDao.create(org, "Title", "Desc", "Location", LocalDateTime.now().plusDays(1), LocalDateTime.now(), null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        eventEnrollmentJpaDao.enrollUser(event, user);
        em.flush();
        em.clear();

        final Optional<Status> status = eventEnrollmentJpaDao.getEnrollmentStatus(event, user);
        Assert.assertTrue(status.isPresent());
        Assert.assertEquals(Status.PENDING, status.get());
    }

    @Rollback
    @Test
    public void testGetEnrollmentStatusNonExistent() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Event event = eventJpaDao.create(org, "Title", "Desc", "Location", LocalDateTime.now().plusDays(1), LocalDateTime.now(), null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        final Optional<Status> status = eventEnrollmentJpaDao.getEnrollmentStatus(event, user);
        Assert.assertFalse(status.isPresent());
    }

    @Rollback
    @Test
    public void testGetEnrollmentsByStatus() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Event event = eventJpaDao.create(org, "Title", "Desc", "Location", LocalDateTime.now().plusDays(1), LocalDateTime.now(), null, null);
        final User user1 = userJpaDao.findByID(1L).orElseThrow();
        final User user2 = userJpaDao.findByID(2L).orElseGet(() -> {
            em.clear();
            return userJpaDao.create("testuser2@itba.edu.ar", "User", "Two", "pass", user1.getPreferredLanguage(), inst);
        });
        em.flush();

        eventEnrollmentJpaDao.enrollUser(event, user1);
        eventEnrollmentJpaDao.enrollUser(event, user2);
        em.flush();
        eventEnrollmentJpaDao.updateEnrollmentStatus(event, user1, Status.ACCEPTED);
        em.flush();
        em.clear();

        final List<EventEnrollment> accepted = eventEnrollmentJpaDao.getEnrollmentsByStatus(event, Status.ACCEPTED);
        Assert.assertTrue(accepted.size() >= 1);

        final List<EventEnrollment> pending = eventEnrollmentJpaDao.getEnrollmentsByStatus(event, Status.PENDING);
        Assert.assertTrue(pending.size() >= 1);
    }

    @Rollback
    @Test
    public void testGetEnrollmentCountsByStatus() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Event event = eventJpaDao.create(org, "Title", "Desc", "Location", LocalDateTime.now().plusDays(1), LocalDateTime.now(), null, null);
        final User user1 = userJpaDao.findByID(1L).orElseThrow();
        final User user2 = userJpaDao.findByID(2L).orElseGet(() -> {
            em.clear();
            return userJpaDao.create("testuser2@itba.edu.ar", "User", "Two", "pass", user1.getPreferredLanguage(), inst);
        });
        em.flush();

        eventEnrollmentJpaDao.enrollUser(event, user1);
        eventEnrollmentJpaDao.enrollUser(event, user2);
        em.flush();
        eventEnrollmentJpaDao.updateEnrollmentStatus(event, user1, Status.ACCEPTED);
        em.flush();
        em.clear();

        final Map<Status, Long> counts = eventEnrollmentJpaDao.getEnrollmentCountsByStatus(event);
        Assert.assertTrue(counts.containsKey(Status.ACCEPTED));
        Assert.assertTrue(counts.containsKey(Status.PENDING));
        Assert.assertTrue(counts.get(Status.ACCEPTED) >= 1);
        Assert.assertTrue(counts.get(Status.PENDING) >= 1);
    }

    @Rollback
    @Test
    public void testDeleteAllEnrollmentsByEvent() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Event event = eventJpaDao.create(org, "Title", "Desc", "Location", LocalDateTime.now().plusDays(1), LocalDateTime.now(), null, null);
        final User user1 = userJpaDao.findByID(1L).orElseThrow();
        final User user2 = userJpaDao.findByID(2L).orElseGet(() -> {
            em.clear();
            return userJpaDao.create("testuser2@itba.edu.ar", "User", "Two", "pass", user1.getPreferredLanguage(), inst);
        });
        em.flush();

        eventEnrollmentJpaDao.enrollUser(event, user1);
        eventEnrollmentJpaDao.enrollUser(event, user2);
        em.flush();
        eventEnrollmentJpaDao.deleteAllEnrollmentsByEvent(event);
        em.flush();
        em.clear();

        Assert.assertEquals(0L, eventEnrollmentJpaDao.getEnrollmentCount(event));
    }

    @Rollback
    @Test
    public void testGetAcceptedEnrollmentsForUpcomingEvents() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        final LocalDateTime endTime = LocalDateTime.now().plusDays(3);
        final Event event = eventJpaDao.create(org, "Title", "Desc", "Location", LocalDateTime.now().plusDays(2), LocalDateTime.now(), null, null);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        eventEnrollmentJpaDao.enrollUser(event, user);
        em.flush();
        eventEnrollmentJpaDao.updateEnrollmentStatus(event, user, Status.ACCEPTED);
        em.flush();
        em.clear();

        final List<EventEnrollment> enrollments = eventEnrollmentJpaDao.getAcceptedEnrollmentsForUpcomingEvents(startTime, endTime);
        Assert.assertTrue(enrollments.size() >= 1);
    }
}

