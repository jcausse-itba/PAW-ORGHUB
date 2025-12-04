package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organigram;
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
public class OrganigramJpaDaoTest {

    @Autowired
    private OrganigramJpaDao organigramJpaDao;

    @Autowired
    private InstitutionJpaDao institutionJpaDao;

    @Autowired
    private OrganizationJpaDao organizationJpaDao;

    @Autowired
    private UserJpaDao userJpaDao;

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
        final Optional<Organigram> organigram = organigramJpaDao.findByID(999L);
        Assert.assertFalse(organigram.isPresent());
    }

    @Rollback
    @Test
    public void testCreate() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final String positionName = "CEO";
        final String description = "Chief Executive Officer";
        final int level = 0;
        final int sortOrder = 1;

        final Organigram organigram = organigramJpaDao.create(org, positionName, description, null, level, sortOrder);

        Assert.assertNotNull(organigram);
        Assert.assertNotNull(organigram.getOrganigramID());
        Assert.assertEquals(positionName, organigram.getPositionName());
        Assert.assertEquals(description, organigram.getDescription());
        Assert.assertEquals((long) level, (long) organigram.getLevel());
        Assert.assertEquals((long) sortOrder, (long) organigram.getSortOrder());
        Assert.assertEquals(org, organigram.getOrganization());
    }

    @Rollback
    @Test
    public void testCreateWithParent() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Organigram parent = organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        em.flush();

        final Organigram child = organigramJpaDao.create(org, "CTO", "Chief Technology Officer", parent, 1, 1);
        em.flush();
        em.clear();

        Assert.assertNotNull(child.getParentPosition());
        Assert.assertEquals(parent.getOrganigramID(), child.getParentPosition().getOrganigramID());
    }

    @Rollback
    @Test
    public void testFindByID() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Organigram organigram = organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        em.flush();
        em.clear();

        final Optional<Organigram> foundOrganigram = organigramJpaDao.findByID(organigram.getOrganigramID());
        Assert.assertTrue(foundOrganigram.isPresent());
        Assert.assertEquals("CEO", foundOrganigram.get().getPositionName());
    }

    @Rollback
    @Test
    public void testFindByOrganization() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        organigramJpaDao.create(org, "CTO", "Chief Technology Officer", null, 0, 2);
        em.flush();
        em.clear();

        final List<Organigram> organigrams = organigramJpaDao.findByOrganization(org);
        Assert.assertTrue(organigrams.size() >= 2);
    }

    @Rollback
    @Test
    public void testFindByOrganizationAndLevel() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        organigramJpaDao.create(org, "CTO", "Chief Technology Officer", null, 0, 2);
        organigramJpaDao.create(org, "Developer", "Software Developer", null, 1, 1);
        em.flush();
        em.clear();

        final List<Organigram> level0 = organigramJpaDao.findByOrganizationAndLevel(org, 0);
        Assert.assertTrue(level0.size() >= 2);

        final List<Organigram> level1 = organigramJpaDao.findByOrganizationAndLevel(org, 1);
        Assert.assertTrue(level1.size() >= 1);
    }

    @Rollback
    @Test
    public void testFindRootPositionsByOrganization() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Organigram root1 = organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        final Organigram root2 = organigramJpaDao.create(org, "CTO", "Chief Technology Officer", null, 0, 2);
        final Organigram child = organigramJpaDao.create(org, "Developer", "Software Developer", root1, 1, 1);
        em.flush();
        em.clear();

        final List<Organigram> rootPositions = organigramJpaDao.findRootPositionsByOrganization(org);
        Assert.assertTrue(rootPositions.size() >= 2);
        Assert.assertTrue(rootPositions.stream().anyMatch(o -> o.getOrganigramID().equals(root1.getOrganigramID())));
        Assert.assertTrue(rootPositions.stream().anyMatch(o -> o.getOrganigramID().equals(root2.getOrganigramID())));
        Assert.assertFalse(rootPositions.stream().anyMatch(o -> o.getOrganigramID().equals(child.getOrganigramID())));
    }

    @Rollback
    @Test
    public void testFindChildPositions() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Organigram parent = organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        final Organigram child1 = organigramJpaDao.create(org, "CTO", "Chief Technology Officer", parent, 1, 1);
        final Organigram child2 = organigramJpaDao.create(org, "CFO", "Chief Financial Officer", parent, 1, 2);
        em.flush();
        em.clear();

        final List<Organigram> children = organigramJpaDao.findChildPositions(parent);
        Assert.assertTrue(children.size() >= 2);
        Assert.assertTrue(children.stream().anyMatch(o -> o.getOrganigramID().equals(child1.getOrganigramID())));
        Assert.assertTrue(children.stream().anyMatch(o -> o.getOrganigramID().equals(child2.getOrganigramID())));
    }

    @Rollback
    @Test
    public void testUpdatePosition() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Organigram organigram = organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        em.flush();

        final String newName = "Chief Executive";
        final String newDescription = "Updated Description";
        final int newLevel = 1;
        final int newSortOrder = 2;
        final Organigram newParent = organigramJpaDao.create(org, "Board", "Board of Directors", null, 0, 1);
        em.flush();

        organigramJpaDao.updatePosition(organigram, newName, newDescription, newParent, newLevel, newSortOrder);
        em.flush();
        em.clear();

        final Organigram updatedOrganigram = organigramJpaDao.findByID(organigram.getOrganigramID()).orElseThrow();
        Assert.assertEquals(newName, updatedOrganigram.getPositionName());
        Assert.assertEquals(newDescription, updatedOrganigram.getDescription());
        Assert.assertEquals((long) newLevel, (long) updatedOrganigram.getLevel());
        Assert.assertEquals((long) newSortOrder, (long) updatedOrganigram.getSortOrder());
        Assert.assertNotNull(updatedOrganigram.getParentPosition());
    }

    @Rollback
    @Test
    public void testDelete() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Organigram organigram = organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        em.flush();
        final long organigramID = organigram.getOrganigramID();

        organigramJpaDao.delete(organigram);
        em.flush();
        em.clear();

        final Optional<Organigram> deletedOrganigram = organigramJpaDao.findByID(organigramID);
        Assert.assertFalse(deletedOrganigram.isPresent());
    }

    @Rollback
    @Test
    public void testExistsPositionNameInOrganization() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final String positionName = "CEO";
        Assert.assertFalse(organigramJpaDao.existsPositionNameInOrganization(positionName, org));

        organigramJpaDao.create(org, positionName, "Chief Executive Officer", null, 0, 1);
        em.flush();
        em.clear();

        Assert.assertTrue(organigramJpaDao.existsPositionNameInOrganization(positionName, org));
    }

    @Rollback
    @Test
    public void testHasChildPositions() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Organigram parent = organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        Assert.assertFalse(organigramJpaDao.hasChildPositions(parent));

        organigramJpaDao.create(org, "CTO", "Chief Technology Officer", parent, 1, 1);
        em.flush();
        em.clear();

        Assert.assertTrue(organigramJpaDao.hasChildPositions(parent));
    }

    @Rollback
    @Test
    public void testExistsSortOrderInLevel() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final int level = 0;
        final int sortOrder = 1;
        Assert.assertFalse(organigramJpaDao.existsSortOrderInLevel(level, sortOrder, org));

        organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, level, sortOrder);
        em.flush();
        em.clear();

        Assert.assertTrue(organigramJpaDao.existsSortOrderInLevel(level, sortOrder, org));
    }

    @Rollback
    @Test
    public void testExistsSortOrderInParent() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Organigram parent = organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        final int sortOrder = 1;
        Assert.assertFalse(organigramJpaDao.existsSortOrderInParent(parent, sortOrder, org));

        organigramJpaDao.create(org, "CTO", "Chief Technology Officer", parent, 1, sortOrder);
        em.flush();
        em.clear();

        Assert.assertTrue(organigramJpaDao.existsSortOrderInParent(parent, sortOrder, org));
    }

    @Rollback
    @Test
    public void testExistsSortOrderInParentNull() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final int sortOrder = 1;
        Assert.assertFalse(organigramJpaDao.existsSortOrderInParent(null, sortOrder, org));

        organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, sortOrder);
        em.flush();
        em.clear();

        Assert.assertTrue(organigramJpaDao.existsSortOrderInParent(null, sortOrder, org));
    }

    @Rollback
    @Test
    public void testAssignUserToPosition() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Organigram organigram = organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        organigramJpaDao.assignUserToPosition(organigram, user);
        em.flush();
        em.clear();

        final Organigram updatedOrganigram = organigramJpaDao.findByID(organigram.getOrganigramID()).orElseThrow();
        Assert.assertNotNull(updatedOrganigram.getAssignedUser());
        Assert.assertEquals(user.getUserID(), updatedOrganigram.getAssignedUser().getUserID());
    }

    @Rollback
    @Test
    public void testUnassignUserFromPosition() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Organigram organigram = organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        organigramJpaDao.assignUserToPosition(organigram, user);
        em.flush();
        organigramJpaDao.unassignUserFromPosition(organigram);
        em.flush();
        em.clear();

        final Organigram updatedOrganigram = organigramJpaDao.findByID(organigram.getOrganigramID()).orElseThrow();
        Assert.assertNull(updatedOrganigram.getAssignedUser());
    }

    @Rollback
    @Test
    public void testFindByUser() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Organigram organigram1 = organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        final Organigram organigram2 = organigramJpaDao.create(org, "CTO", "Chief Technology Officer", null, 0, 2);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        organigramJpaDao.assignUserToPosition(organigram1, user);
        organigramJpaDao.assignUserToPosition(organigram2, user);
        em.flush();
        em.clear();

        final List<Organigram> organigrams = organigramJpaDao.findByUser(user);
        Assert.assertTrue(organigrams.size() >= 2);
    }

    @Rollback
    @Test
    public void testFindByUserAndOrganization() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org1 = organizationJpaDao.create("org1@itba.edu.ar", "Org 1", null, null, "Desc", inst);
        final Organization org2 = organizationJpaDao.create("org2@itba.edu.ar", "Org 2", null, null, "Desc", inst);
        final Organigram organigram1 = organigramJpaDao.create(org1, "CEO", "Chief Executive Officer", null, 0, 1);
        final Organigram organigram2 = organigramJpaDao.create(org2, "CTO", "Chief Technology Officer", null, 0, 1);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        organigramJpaDao.assignUserToPosition(organigram1, user);
        organigramJpaDao.assignUserToPosition(organigram2, user);
        em.flush();
        em.clear();

        final List<Organigram> organigrams = organigramJpaDao.findByUserAndOrganization(user, org1);
        Assert.assertTrue(organigrams.size() >= 1);
        Assert.assertTrue(organigrams.stream().anyMatch(o -> o.getOrganigramID().equals(organigram1.getOrganigramID())));
    }

    @Rollback
    @Test
    public void testFindVacantPositionsByOrganization() {
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        final Organigram vacant1 = organigramJpaDao.create(org, "CEO", "Chief Executive Officer", null, 0, 1);
        final Organigram vacant2 = organigramJpaDao.create(org, "CTO", "Chief Technology Officer", null, 0, 2);
        final Organigram assigned = organigramJpaDao.create(org, "CFO", "Chief Financial Officer", null, 0, 3);
        final User user = userJpaDao.findByID(1L).orElseThrow();
        em.flush();

        organigramJpaDao.assignUserToPosition(assigned, user);
        em.flush();
        em.clear();

        final List<Organigram> vacantPositions = organigramJpaDao.findVacantPositionsByOrganization(org);
        Assert.assertTrue(vacantPositions.size() >= 2);
        Assert.assertTrue(vacantPositions.stream().anyMatch(o -> o.getOrganigramID().equals(vacant1.getOrganigramID())));
        Assert.assertTrue(vacantPositions.stream().anyMatch(o -> o.getOrganigramID().equals(vacant2.getOrganigramID())));
        Assert.assertFalse(vacantPositions.stream().anyMatch(o -> o.getOrganigramID().equals(assigned.getOrganigramID())));
    }
}

