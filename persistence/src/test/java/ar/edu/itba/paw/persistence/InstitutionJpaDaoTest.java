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
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class InstitutionJpaDaoTest {

    @Autowired
    private InstitutionJpaDao institutionJpaDao;

    @Autowired
    private UserJpaDao userJpaDao;

    @Autowired
    private OrganizationJpaDao organizationJpaDao;

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
    public void testFindByID() {
        final Optional<Institution> inst = institutionJpaDao.findByID(1L);
        Assert.assertTrue(inst.isPresent());
        Assert.assertEquals("ITBA", inst.get().getInstitutionName());
        Assert.assertEquals("itba.edu.ar", inst.get().getDomainName());
    }

    @Rollback
    @Test
    public void testFindByIDNonExistent() {
        final Optional<Institution> inst = institutionJpaDao.findByID(999L);
        Assert.assertFalse(inst.isPresent());
    }

    @Rollback
    @Test
    public void testFindByDomainName() {
        final Optional<Institution> inst = institutionJpaDao.findByDomainName("itba.edu.ar");
        Assert.assertTrue(inst.isPresent());
        Assert.assertEquals("ITBA", inst.get().getInstitutionName());
    }

    @Rollback
    @Test
    public void testFindByDomainNameNonExistent() {
        final Optional<Institution> inst = institutionJpaDao.findByDomainName("nonexistent.edu.ar");
        Assert.assertFalse(inst.isPresent());
    }

    @Rollback
    @Test
    public void testFindByAdminEmail() {
        final Optional<Institution> inst = institutionJpaDao.findByAdminEmail("admin@itba.edu.ar");
        Assert.assertTrue(inst.isPresent());
        Assert.assertEquals("ITBA", inst.get().getInstitutionName());
    }

    @Rollback
    @Test
    public void testFindByAdminEmailNonExistent() {
        final Optional<Institution> inst = institutionJpaDao.findByAdminEmail("nonexistent@itba.edu.ar");
        Assert.assertFalse(inst.isPresent());
    }

    @Rollback
    @Test
    public void testCreate() {
        final String name = "New Institution";
        final String domainName = "newinst.edu.ar";
        final String adminEmail = "admin@newinst.edu.ar";

        final Institution inst = institutionJpaDao.create(name, domainName, adminEmail);

        Assert.assertNotNull(inst);
        Assert.assertNotNull(inst.getInstitutionID());
        Assert.assertEquals(name, inst.getInstitutionName());
        Assert.assertEquals(domainName, inst.getDomainName());
        Assert.assertEquals(adminEmail, inst.getAdminEmail());
    }

    @Rollback
    @Test
    public void testFindAll() {
        // Test that findAll returns at least the existing institution
        final List<Institution> all = institutionJpaDao.findAll(0);
        Assert.assertTrue(all.size() >= 1); // At least the existing one from test data
    }

    @Rollback
    @Test
    public void testGetOrganizations() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Description", inst);
        em.flush();
        em.clear();

        final List<Organization> orgs = institutionJpaDao.getOrganizations(inst, 0);
        Assert.assertTrue(orgs.size() >= 1);
        Assert.assertTrue(orgs.stream().anyMatch(o -> o.getOrgName().equals("Test Org")));
    }

    @Rollback
    @Test
    public void testGetAdministrator() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final User admin = institutionJpaDao.getAdministrator(inst);
        Assert.assertNotNull(admin);
        Assert.assertEquals(inst.getAdminEmail(), admin.getUserEmail());
    }

    @Rollback
    @Test
    public void testExistsDomainName() {
        Assert.assertTrue(institutionJpaDao.existsDomainName("itba.edu.ar"));
        Assert.assertFalse(institutionJpaDao.existsDomainName("nonexistent.edu.ar"));
    }

    @Rollback
    @Test
    public void testExistsAdminEmail() {
        Assert.assertTrue(institutionJpaDao.existsAdminEmail("admin@itba.edu.ar"));
        Assert.assertFalse(institutionJpaDao.existsAdminEmail("nonexistent@itba.edu.ar"));
    }
}

