package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
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
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class SubscriptionJpaDaoTest {

    @Autowired
    private SubscriptionJpaDao subscriptionJpaDao;

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
    public void testIsSubscribedFalse() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        em.flush();

        Assert.assertFalse(subscriptionJpaDao.isSubscribed(user, org));
    }

    @Rollback
    @Test
    public void testAddSubscription() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        em.flush();

        subscriptionJpaDao.addSubscription(user, org);
        em.flush();
        em.clear();

        Assert.assertTrue(subscriptionJpaDao.isSubscribed(user, org));
    }

    @Rollback
    @Test
    public void testRemoveSubscription() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        em.flush();

        subscriptionJpaDao.addSubscription(user, org);
        em.flush();
        subscriptionJpaDao.removeSubscription(user, org);
        em.flush();
        em.clear();

        Assert.assertFalse(subscriptionJpaDao.isSubscribed(user, org));
    }

    @Rollback
    @Test
    public void testGetSubscribedOrganizations() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org1 = organizationJpaDao.create("org1@itba.edu.ar", "Org 1", null, null, "Desc", inst);
        final Organization org2 = organizationJpaDao.create("org2@itba.edu.ar", "Org 2", null, null, "Desc", inst);
        em.flush();

        subscriptionJpaDao.addSubscription(user, org1);
        subscriptionJpaDao.addSubscription(user, org2);
        em.flush();
        em.clear();

        final List<Organization> subscribed = subscriptionJpaDao.getSubscribedOrganizations(user);
        Assert.assertEquals(2, subscribed.size());
        Assert.assertTrue(subscribed.stream().anyMatch(o -> o.getOrgName().equals("Org 1")));
        Assert.assertTrue(subscribed.stream().anyMatch(o -> o.getOrgName().equals("Org 2")));
    }

    @Rollback
    @Test
    public void testGetSubscribers() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final User user1 = userJpaDao.findByID(1L).orElseThrow();
        // Use existing user2 from test data instead of creating a new one
        final User user2 = userJpaDao.findByID(2L).orElseGet(() -> {
            // If user2 doesn't exist, create it with a unique email
            em.clear(); // Clear session to avoid conflicts
            return userJpaDao.create("testuser2@itba.edu.ar", "User", "Two", "pass", user1.getPreferredLanguage(), inst);
        });
        em.flush();

        subscriptionJpaDao.addSubscription(user1, org);
        subscriptionJpaDao.addSubscription(user2, org);
        em.flush();
        em.clear();

        final List<User> subscribers = subscriptionJpaDao.getSubscribers(org);
        Assert.assertEquals(2, subscribers.size());
    }
}

