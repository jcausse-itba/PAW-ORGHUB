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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class UsersRolesJpaDaoTest {

    @Autowired
    private UsersRolesJpaDao usersRolesJpaDao;

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
    public void testGetUserRoleDefault() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        em.flush();

        final Role role = usersRolesJpaDao.getUserRole(user, org);
        Assert.assertEquals(Role.USER, role);
    }

    @Rollback
    @Test
    public void testAssignUserRole() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        em.flush();

        usersRolesJpaDao.assignUserRole(user, org, Role.PUBLISHER);
        em.flush();
        em.clear();

        final Role role = usersRolesJpaDao.getUserRole(user, org);
        Assert.assertEquals(Role.PUBLISHER, role);
    }

    @Rollback
    @Test
    public void testAssignUserRoleOverwrites() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        em.flush();

        usersRolesJpaDao.assignUserRole(user, org, Role.PUBLISHER);
        em.flush();
        usersRolesJpaDao.assignUserRole(user, org, Role.MANAGER);
        em.flush();
        em.clear();

        final Role role = usersRolesJpaDao.getUserRole(user, org);
        Assert.assertEquals(Role.MANAGER, role);
    }

    @Rollback
    @Test
    public void testAssignUserRoleUSER() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        em.flush();

        usersRolesJpaDao.assignUserRole(user, org, Role.PUBLISHER);
        em.flush();
        usersRolesJpaDao.assignUserRole(user, org, Role.USER);
        em.flush();
        em.clear();

        final Role role = usersRolesJpaDao.getUserRole(user, org);
        Assert.assertEquals(Role.USER, role);
    }

    @Rollback
    @Test
    public void testDeleteUserRole() {
        final User user = userJpaDao.findByID(1L).orElseThrow();
        final Institution inst = institutionJpaDao.findByID(1L).orElseThrow();
        final Organization org = organizationJpaDao.create("org@itba.edu.ar", "Test Org", null, null, "Desc", inst);
        em.flush();

        usersRolesJpaDao.assignUserRole(user, org, Role.PUBLISHER);
        em.flush();
        usersRolesJpaDao.deleteUserRole(user, org);
        em.flush();
        em.clear();

        final Role role = usersRolesJpaDao.getUserRole(user, org);
        Assert.assertEquals(Role.USER, role);
    }
}

