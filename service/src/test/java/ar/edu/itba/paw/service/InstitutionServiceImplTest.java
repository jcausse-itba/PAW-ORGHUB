package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.persistence.InstitutionDao;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionAdminEmailAlreadyRegisteredException;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionDomainNameExistsException;
import ar.edu.itba.paw.interfaces.service.exception.InstitutionNotFoundException;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InstitutionServiceImplTest {

    @Mock
    private InstitutionDao institutionDao;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private InstitutionServiceImpl institutionService;

    private Institution testInstitution;
    private User testAdmin;
    private Organization testOrganization;

    @Before
    public void setup() {
        testInstitution = new Institution("ITBA", "itba.edu.ar", "admin@itba.edu.ar");
        // Institution ID is set by the database

        testAdmin = new User("admin@itba.edu.ar", "Admin", "User", "password", null, testInstitution);
        // User ID is set by the database

        testOrganization = new Organization("org@itba.edu.ar", "Test Org", null, null, "Description", testInstitution);
        // Organization ID is set by the database
    }

    @Test
    public void testFindByID() {
        when(institutionDao.findByID(1L)).thenReturn(Optional.of(testInstitution));

        final Institution result = institutionService.findByID(1L);

        Assert.assertNotNull(result);
        Assert.assertEquals(testInstitution, result);
        verify(institutionDao, times(1)).findByID(1L);
    }

    @Test(expected = InstitutionNotFoundException.class)
    public void testFindByIDNonExistent() {
        when(institutionDao.findByID(999L)).thenReturn(Optional.empty());

        institutionService.findByID(999L);
    }

    @Test
    public void testFindByDomainName() {
        when(institutionDao.findByDomainName("itba.edu.ar")).thenReturn(Optional.of(testInstitution));

        final Institution result = institutionService.findByDomainName("itba.edu.ar");

        Assert.assertNotNull(result);
        Assert.assertEquals(testInstitution, result);
        verify(institutionDao, times(1)).findByDomainName("itba.edu.ar");
    }

    @Test(expected = InstitutionNotFoundException.class)
    public void testFindByDomainNameNonExistent() {
        when(institutionDao.findByDomainName("unknown.edu.ar")).thenReturn(Optional.empty());

        institutionService.findByDomainName("unknown.edu.ar");
    }

    @Test
    public void testFindByAdminEmail() {
        when(institutionDao.findByAdminEmail("admin@itba.edu.ar")).thenReturn(Optional.of(testInstitution));

        final Institution result = institutionService.findByAdminEmail("admin@itba.edu.ar");

        Assert.assertNotNull(result);
        Assert.assertEquals(testInstitution, result);
        verify(institutionDao, times(1)).findByAdminEmail("admin@itba.edu.ar");
    }

    @Test(expected = InstitutionNotFoundException.class)
    public void testFindByAdminEmailNonExistent() {
        when(institutionDao.findByAdminEmail("unknown@itba.edu.ar")).thenReturn(Optional.empty());

        institutionService.findByAdminEmail("unknown@itba.edu.ar");
    }

    @Test
    public void testCreate() {
        when(institutionDao.existsDomainName("new.edu.ar")).thenReturn(false);
        when(institutionDao.existsAdminEmail("admin@new.edu.ar")).thenReturn(false);
        when(institutionDao.create("New Institution", "new.edu.ar", "admin@new.edu.ar")).thenReturn(testInstitution);
        doNothing().when(emailService).sendInstitutionCreationEmail(anyString(), anyString(), anyString());

        final Institution result = institutionService.create("New Institution", "new.edu.ar", "admin@new.edu.ar");

        Assert.assertNotNull(result);
        Assert.assertEquals(testInstitution, result);
        verify(institutionDao, times(1)).existsDomainName("new.edu.ar");
        verify(institutionDao, times(1)).existsAdminEmail("admin@new.edu.ar");
        verify(institutionDao, times(1)).create("New Institution", "new.edu.ar", "admin@new.edu.ar");
        verify(emailService, times(1)).sendInstitutionCreationEmail("admin@new.edu.ar", "New Institution", "new.edu.ar");
    }

    @Test(expected = InstitutionDomainNameExistsException.class)
    public void testCreateWithExistingDomainName() {
        when(institutionDao.existsDomainName("existing.edu.ar")).thenReturn(true);

        institutionService.create("New Institution", "existing.edu.ar", "admin@new.edu.ar");

        verify(institutionDao, times(1)).existsDomainName("existing.edu.ar");
        verify(institutionDao, never()).create(anyString(), anyString(), anyString());
    }

    @Test(expected = InstitutionAdminEmailAlreadyRegisteredException.class)
    public void testCreateWithExistingAdminEmail() {
        when(institutionDao.existsDomainName("new.edu.ar")).thenReturn(false);
        when(institutionDao.existsAdminEmail("existing@itba.edu.ar")).thenReturn(true);

        institutionService.create("New Institution", "new.edu.ar", "existing@itba.edu.ar");

        verify(institutionDao, times(1)).existsDomainName("new.edu.ar");
        verify(institutionDao, times(1)).existsAdminEmail("existing@itba.edu.ar");
        verify(institutionDao, never()).create(anyString(), anyString(), anyString());
    }

    @Test
    public void testGetOrganizationsByID() {
        when(institutionDao.findByID(1L)).thenReturn(Optional.of(testInstitution));
        when(institutionDao.getOrganizations(testInstitution, 0)).thenReturn(Arrays.asList(testOrganization));

        final List<Organization> result = institutionService.getOrganizationsByID(1L, 0);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(testOrganization, result.get(0));
        verify(institutionDao, times(1)).getOrganizations(testInstitution, 0);
    }

    @Test
    public void testGetOrganizationsByDomainName() {
        when(institutionDao.findByDomainName("itba.edu.ar")).thenReturn(Optional.of(testInstitution));
        when(institutionDao.getOrganizations(testInstitution, 0)).thenReturn(Arrays.asList(testOrganization));

        final List<Organization> result = institutionService.getOrganizationsByDomainName("itba.edu.ar", 0);

        Assert.assertEquals(1, result.size());
        verify(institutionDao, times(1)).getOrganizations(testInstitution, 0);
    }

    @Test
    public void testGetAdministratorByID() {
        when(institutionDao.findByID(1L)).thenReturn(Optional.of(testInstitution));
        when(institutionDao.getAdministrator(testInstitution)).thenReturn(testAdmin);

        final User result = institutionService.getAdministratorByID(1L);

        Assert.assertNotNull(result);
        Assert.assertEquals(testAdmin, result);
        verify(institutionDao, times(1)).getAdministrator(testInstitution);
    }

    @Test
    public void testGetAdministratorByDomainName() {
        when(institutionDao.findByDomainName("itba.edu.ar")).thenReturn(Optional.of(testInstitution));
        when(institutionDao.getAdministrator(testInstitution)).thenReturn(testAdmin);

        final User result = institutionService.getAdministratorByDomainName("itba.edu.ar");

        Assert.assertNotNull(result);
        Assert.assertEquals(testAdmin, result);
        verify(institutionDao, times(1)).getAdministrator(testInstitution);
    }

    @Test
    public void testIsAdmin() {
        final boolean result = institutionService.isAdmin(testAdmin);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsAdminFalse() {
        final User nonAdmin = new User("user@itba.edu.ar", "User", "Name", "password", null, testInstitution);
        final boolean result = institutionService.isAdmin(nonAdmin);

        Assert.assertFalse(result);
    }

    @Test
    public void testIsAdminNoInstitution() {
        final User userWithoutInstitution = new User("user@itba.edu.ar", "User", "Name", "password", null, null);
        final boolean result = institutionService.isAdmin(userWithoutInstitution);

        Assert.assertFalse(result);
    }

    @Test
    public void testGetAllInstitutions() {
        when(institutionDao.findAll(0)).thenReturn(Arrays.asList(testInstitution));

        final List<Institution> result = institutionService.getAllInstitutions(0);

        Assert.assertEquals(1, result.size());
        verify(institutionDao, times(1)).findAll(0);
    }

    @Test
    public void testExistsDomainName() {
        when(institutionDao.existsDomainName("itba.edu.ar")).thenReturn(true);

        final boolean result = institutionService.existsDomainName("itba.edu.ar");

        Assert.assertTrue(result);
        verify(institutionDao, times(1)).existsDomainName("itba.edu.ar");
    }
}

