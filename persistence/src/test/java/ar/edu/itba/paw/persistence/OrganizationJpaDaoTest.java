package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Role;
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
public class OrganizationJpaDaoTest {

    @Autowired
    private OrganizationJpaDao organizationJpaDao;

    @Autowired
    private InstitutionJpaDao institutionJpaDao;

    @Autowired
    private UserJpaDao userJpaDao;

    @Autowired
    private UsersRolesJpaDao usersRolesJpaDao;

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
        final Optional<Organization> org = organizationJpaDao.findByID(999L);
        Assert.assertFalse(org.isPresent());
    }

    @Rollback
    @Test
    public void testFindByEmailNonExistent() {
        final Optional<Organization> org = organizationJpaDao.findByEmail("nonexistent@itba.edu.ar");
        Assert.assertFalse(org.isPresent());
    }

    @Rollback
    @Test
    public void testCreate() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final String email = "org@itba.edu.ar";
        final String name = "Test Organization";
        final String social1 = "https://facebook.com/org";
        final String social2 = "https://twitter.com/org";
        final String desc = "Test Description";

        final Organization org = organizationJpaDao.create(email, name, social1, social2, desc, inst);

        Assert.assertNotNull(org);
        Assert.assertNotNull(org.getOrgID());
        Assert.assertEquals(email, org.getOrgEmail());
        Assert.assertEquals(name, org.getOrgName());
        Assert.assertEquals(social1, org.getSocialLink1());
        Assert.assertEquals(social2, org.getSocialLink2());
        Assert.assertEquals(desc, org.getDescription());
        Assert.assertEquals(inst, org.getInstitution());
    }

    @Rollback
    @Test
    public void testFindByID() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        em.flush();
        em.clear();

        final Optional<Organization> foundOrg = organizationJpaDao.findByID(org.getOrgID());
        Assert.assertTrue(foundOrg.isPresent());
        Assert.assertEquals("Test Org", foundOrg.get().getOrgName());
    }

    @Rollback
    @Test
    public void testFindByEmail() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final String email = "org@itba.edu.ar";
        final Organization org = organizationJpaDao.create(email, "Test Org", null, null, "Desc", inst);
        em.flush();
        em.clear();

        final Optional<Organization> foundOrg = organizationJpaDao.findByEmail(email);
        Assert.assertTrue(foundOrg.isPresent());
        Assert.assertEquals("Test Org", foundOrg.get().getOrgName());
    }

    @Rollback
    @Test
    public void testFindAllForInstitution() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        organizationJpaDao.create("org1@itba.edu.ar", "Org 1", null, null, "Desc", inst);
        organizationJpaDao.create("org2@itba.edu.ar", "Org 2", null, null, "Desc", inst);
        em.flush();
        em.clear();

        final List<Organization> orgs = organizationJpaDao.findAllForInstitution(inst, 0);
        Assert.assertTrue(orgs.size() >= 2);
    }

    @Rollback
    @Test
    public void testFindAll() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        organizationJpaDao.create("org1@itba.edu.ar", "Org 1", null, null, "Desc", inst);
        organizationJpaDao.create("org2@itba.edu.ar", "Org 2", null, null, "Desc", inst);
        em.flush();
        em.clear();

        final List<Organization> allOrgs = organizationJpaDao.findAll(0);
        Assert.assertTrue(allOrgs.size() >= 2);
    }

    @Rollback
    @Test
    public void testGetPublishers() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        usersRolesJpaDao.assignUserRole(user, org, Role.PUBLISHER);
        em.flush();
        em.clear();

        final List<User> publishers = organizationJpaDao.getPublishers(org);
        Assert.assertTrue(publishers.size() >= 1);
        Assert.assertTrue(publishers.stream().anyMatch(u -> u.getUserID().equals(user.getUserID())));
    }

    @Rollback
    @Test
    public void testGetManagers() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        usersRolesJpaDao.assignUserRole(user, org, Role.MANAGER);
        em.flush();
        em.clear();

        final List<User> managers = organizationJpaDao.getManagers(org);
        Assert.assertTrue(managers.size() >= 1);
        Assert.assertTrue(managers.stream().anyMatch(u -> u.getUserID().equals(user.getUserID())));
    }

    @Rollback
    @Test
    public void testExistsEmail() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final String email = "org@itba.edu.ar";
        Assert.assertFalse(organizationJpaDao.existsEmail(email));

        organizationJpaDao.create(email, "Test Org", null, null, "Desc", inst);
        em.flush();
        em.clear();

        Assert.assertTrue(organizationJpaDao.existsEmail(email));
    }
}

