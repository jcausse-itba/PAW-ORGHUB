package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.PartnershipRequest;
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
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class PartnershipRequestJpaDaoTest {

    @Autowired
    private PartnershipRequestJpaDao partnershipRequestJpaDao;

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
        final Optional<PartnershipRequest> pr = partnershipRequestJpaDao.findByID(999L);
        Assert.assertFalse(pr.isPresent());
    }

    @Rollback
    @Test
    public void testCreate() {
        final String name = "New Institution";
        final String domainName = "newinst.edu.ar";
        final String adminEmail = "admin@newinst.edu.ar";

        final PartnershipRequest pr = partnershipRequestJpaDao.create(name, domainName, adminEmail);

        Assert.assertNotNull(pr);
        Assert.assertNotNull(pr.getRequestID());
        Assert.assertEquals(name, pr.getInstitutionName());
        Assert.assertEquals(domainName, pr.getDomainName());
        Assert.assertEquals(adminEmail, pr.getAdminEmail());
        Assert.assertEquals(Status.PENDING, pr.getStatus());
        Assert.assertNotNull(pr.getRequestDate());
    }

    @Rollback
    @Test
    public void testFindByID() {
        final PartnershipRequest pr = partnershipRequestJpaDao.create("Inst", "inst.edu.ar", "admin@inst.edu.ar");
        em.flush();
        em.clear();

        final Optional<PartnershipRequest> foundPr = partnershipRequestJpaDao.findByID(pr.getRequestID());
        Assert.assertTrue(foundPr.isPresent());
        Assert.assertEquals("Inst", foundPr.get().getInstitutionName());
    }

    @Rollback
    @Test
    public void testFindAll() {
        partnershipRequestJpaDao.create("Inst1", "inst1.edu.ar", "admin1@inst1.edu.ar");
        partnershipRequestJpaDao.create("Inst2", "inst2.edu.ar", "admin2@inst2.edu.ar");
        em.flush();
        em.clear();

        final List<PartnershipRequest> all = partnershipRequestJpaDao.findAll();
        Assert.assertTrue(all.size() >= 2);
    }

    @Rollback
    @Test
    public void testFindByStatus() {
        final PartnershipRequest pr1 = partnershipRequestJpaDao.create("Inst1", "inst1.edu.ar", "admin1@inst1.edu.ar");
        final PartnershipRequest pr2 = partnershipRequestJpaDao.create("Inst2", "inst2.edu.ar", "admin2@inst2.edu.ar");
        partnershipRequestJpaDao.updateStatus(pr2, Status.ACCEPTED);
        em.flush();
        em.clear();

        final List<PartnershipRequest> pending = partnershipRequestJpaDao.findByStatus(Status.PENDING);
        Assert.assertTrue(pending.size() >= 1);
        Assert.assertTrue(pending.stream().anyMatch(p -> p.getRequestID().equals(pr1.getRequestID())));

        final List<PartnershipRequest> accepted = partnershipRequestJpaDao.findByStatus(Status.ACCEPTED);
        Assert.assertTrue(accepted.size() >= 1);
        Assert.assertTrue(accepted.stream().anyMatch(p -> p.getRequestID().equals(pr2.getRequestID())));
    }

    @Rollback
    @Test
    public void testUpdateStatus() {
        final PartnershipRequest pr = partnershipRequestJpaDao.create("Inst", "inst.edu.ar", "admin@inst.edu.ar");
        em.flush();

        partnershipRequestJpaDao.updateStatus(pr, Status.ACCEPTED);
        em.flush();
        em.clear();

        final PartnershipRequest updatedPr = partnershipRequestJpaDao.findByID(pr.getRequestID()).orElseThrow();
        Assert.assertEquals(Status.ACCEPTED, updatedPr.getStatus());
        Assert.assertNotNull(updatedPr.getProcessedDate());
    }

    @Rollback
    @Test
    public void testExistsByDomainName() {
        final String domainName = "inst.edu.ar";
        Assert.assertFalse(partnershipRequestJpaDao.existsByDomainName(domainName));

        partnershipRequestJpaDao.create("Inst", domainName, "admin@inst.edu.ar");
        em.flush();
        em.clear();

        Assert.assertTrue(partnershipRequestJpaDao.existsByDomainName(domainName));
    }
}

